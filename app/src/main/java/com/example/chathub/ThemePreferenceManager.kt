package com.example.chathub

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.chathub.viewmodels.Theme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore("settings")

class ThemePreferenceManager @Inject constructor(private val context: Context) {

    private val dataStore = context.dataStore

    companion object {
        val THEME_KEY = intPreferencesKey("theme")
        val COLOR_KEY = booleanPreferencesKey("dynamicColor")
    }

    val theme: Flow<Theme> = dataStore.data.map { preferences ->
        when (preferences[THEME_KEY] ?: 2) {
            1 -> Theme.DARK
            2 -> Theme.SYSTEM_DEFAULT
            else -> Theme.LIGHT
        }
    }

    val dynamicColor: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[COLOR_KEY] ?: true
    }

    suspend fun setDynamicColorEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[COLOR_KEY] = enabled
        }
    }

    suspend fun setTheme(theme: Theme) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = when (theme) {
                Theme.LIGHT -> 0
                Theme.DARK -> 1
                Theme.SYSTEM_DEFAULT -> 2
            }
        }
    }
}