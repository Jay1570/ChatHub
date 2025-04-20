package com.example.chathub_android.screens.register

import com.example.chathub_android.*
import com.example.chathub_android.ext.isValidEmail
import com.example.chathub_android.ext.isValidPassword
import com.example.chathub_android.ext.passwordMatches
import com.example.chathub_android.models.SignupRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RegistrationViewModel(
    private val userPreference: UserPreference,
) : ChatAppViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState get() = _uiState.asStateFlow()

    private val name get() = _uiState.value.name
    private val email get() = _uiState.value.email
    private val password get() = _uiState.value.password
    private val repeatPassword get() = _uiState.value.repeatPassword

    fun onNameChange(newValue: String) {
        _uiState.update { it.copy(name = newValue) }
    }

    fun onEmailChange(newValue: String) {
        _uiState.update { it.copy(email = newValue) }
    }

    fun onPasswordChange(newValue: String) {
        _uiState.update { it.copy(password = newValue) }
    }

    fun onRepeatPasswordChange(newValue: String) {
        _uiState.update { it.copy(repeatPassword = newValue) }
    }

    fun onCreateAccountClick(clearAndNavigate: (Routes) -> Unit) {
        if (name.isBlank() || email.isBlank() || password.isBlank() || repeatPassword.isBlank()) {
            showSnackbar("All fields are required")
            return
        }
        if (!email.isValidEmail()) {
            showSnackbar("Invalid Email")
            return
        }
        if (!password.isValidPassword()) {
            showSnackbar("Password must contain at least 8 characters and one lowercase, one uppercase, one number and one special character.")
            return
        }
        if (!password.passwordMatches(repeatPassword)) {
            showSnackbar("Passwords do not match")
            return
        }

        _uiState.update { it.copy(inProcess = true) }

        launchCatching {
            val signupRequest = SignupRequest(name, email, password)
            val response = RetrofitInstance.api.signup(signupRequest)
            if (!response.isSuccessful) {
                val errorResponse = RetrofitInstance.parseErrorResponse(response.errorBody())
                showSnackbar("Registration Failed: ${errorResponse.error}")
                return@launchCatching
            }
            val authResponse = response.body()
            if (authResponse == null) {
                showSnackbar("Registration Failed: Response body is null")
                return@launchCatching
            }
            userPreference.setToken(authResponse.token)
            userPreference.setUser(authResponse.user)
            clearAndNavigate(Home)
            showSnackbar("Registration Successful")
        }.invokeOnCompletion {
            _uiState.update { it.copy(inProcess = false) }
        }
    }
}

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val repeatPassword: String = "",
    val inProcess: Boolean = false
)