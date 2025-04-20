package com.example.chathub_android.screens.login

import com.example.chathub_android.*
import com.example.chathub_android.ext.isValidEmail
import com.example.chathub_android.ext.isValidPassword
import com.example.chathub_android.models.LoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel(private val userPreference: UserPreference) : ChatAppViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState get() = _uiState.asStateFlow()

    private val email get() = _uiState.value.email
    private val password get() = _uiState.value.password

    fun onEmailChange(newValue: String) {
        _uiState.update { it.copy(email = newValue) }
    }

    fun onPasswordChange(newValue: String) {
        _uiState.update { it.copy(password = newValue) }
    }

    fun onSignInClick(openAndPopUp: (Routes, Routes) -> Unit) {
        if (email.isBlank() && password.isBlank()) {
            showSnackbar("Every field is mandatory")
            return
        }

        if (!email.isValidEmail()) {
            showSnackbar("Invalid email")
            return
        }

        if (!password.isValidPassword()) {
            showSnackbar("Password must contain at least 8 characters and one lowercase, one uppercase, one number and one special character.")
            return
        }

        _uiState.update { it.copy(inProcess = true) }

        launchCatching {
            val loginRequest = LoginRequest(email, password)
            val response = RetrofitInstance.api.login(loginRequest)
            if (!response.isSuccessful) {
                val errorResponse = RetrofitInstance.parseErrorResponse(response.errorBody())
                showSnackbar("Login Failed: ${errorResponse.error}")
                return@launchCatching
            }
            val loginResponse = response.body()
            if (loginResponse == null) {
                showSnackbar("Login Failed: Response body is null")
                return@launchCatching
            }
            userPreference.setToken(loginResponse.token)
            userPreference.setUser(loginResponse.user)
            openAndPopUp(Home, Login)
            showSnackbar("Login Successful")
        }.invokeOnCompletion {
            _uiState.update { it.copy(inProcess = false) }
        }
    }
}

data class LoginUiState(
    val email: String = "",
    val name: String = "",
    val password: String = "",
    val profilePicture: String = "",
    val inProcess: Boolean = false
)
