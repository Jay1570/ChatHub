package com.example.chathub.module

import android.content.Context
import com.example.chathub.ThemePreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {

    @Provides
    fun provideApplicationContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    fun provideThemePreferenceManager(context: Context): ThemePreferenceManager {
        return ThemePreferenceManager(context)
    }
}