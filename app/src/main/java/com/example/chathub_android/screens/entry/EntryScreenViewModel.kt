package com.example.chathub_android.screens.entry

import android.util.Log
import com.example.chathub_android.*
import com.example.chathub_android.models.User
import kotlinx.coroutines.flow.first

class EntryScreenViewModel(private val userPreference: UserPreference) : ChatAppViewModel() {

    fun checkTokenWithApi(navigateAndClearBackStack: (Routes) -> Unit) {

        launchCatching {
            val token = userPreference.token.first()

            if (token.isEmpty()) {
                navigateAndClearBackStack(Login)
                return@launchCatching
            }

            val authHeader = "Bearer $token"
            val response = RetrofitInstance.api.validateToken(authHeader)
            Log.d("EntryScreenViewModel", "Response: ${response.body()}")
            if (!response.isSuccessful) {
                val errorResponse = RetrofitInstance.parseErrorResponse(response.errorBody())
                showSnackbar("Invalid token response: ${errorResponse.error}")
                navigateAndClearBackStack(Login)
            }
            val isValid = response.body()?.valid == true
            if (isValid) {
                userPreference.setToken(token)
                navigateAndClearBackStack(Home)
            } else {
                showSnackbar("Invalid token")
                userPreference.setToken("")
                userPreference.setUser(User(-1, "", "", ""))
                navigateAndClearBackStack(Login)
            }
        }
    }
}