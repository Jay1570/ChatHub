package com.example.chathub_android

import android.content.Context
import androidx.core.content.edit
import com.example.chathub_android.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserPreference(private val context: Context) {

    companion object {
        const val PREFS_KEY = "user_preference"
        const val TOKEN_KEY = "token"
        const val USER_ID_KEY = "user_id"
        const val USER_NAME_KEY = "user_name"
        const val USER_EMAIL_KEY = "user_email"
        const val USER_IMAGE_URL_KEY = "user_image_url"
    }

    private val sharedPreferences by lazy {
        context.applicationContext.getSharedPreferences(
            PREFS_KEY,
            Context.MODE_PRIVATE
        )
    }

    private val _tokenFlow by lazy { MutableStateFlow(getSavedToken()) }
    val token: StateFlow<String> by lazy { _tokenFlow }

    private val _userFlow by lazy { MutableStateFlow(getUser()) }
    val user: StateFlow<User> by lazy { _userFlow }

    private fun getUser(): User {
        return User(
            id = sharedPreferences.getInt(USER_ID_KEY, -1),
            name = sharedPreferences.getString(USER_NAME_KEY, "") ?: "",
            email = sharedPreferences.getString(USER_EMAIL_KEY, "") ?: "",
            imageUrl = sharedPreferences.getString(USER_IMAGE_URL_KEY, "") ?: ""
        )
    }

    fun setUser(user: User) {
        sharedPreferences.edit {
            putInt(USER_ID_KEY, user.id)
            putString(USER_NAME_KEY, user.name)
            putString(USER_EMAIL_KEY, user.email)
            putString(USER_IMAGE_URL_KEY, user.imageUrl)
        }
        _userFlow.value = user
    }

    private fun getSavedToken(): String {
        return sharedPreferences.getString(TOKEN_KEY, "") ?: ""
    }

    fun setToken(token: String) {
        sharedPreferences.edit() { putString(TOKEN_KEY, token) }
        _tokenFlow.value = token
    }
}