package com.example.chathub.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.chathub.ChatAppViewModel
import com.example.chathub.R
import com.example.chathub.ext.isValidEmail
import com.example.chathub.ext.passwordMatches
import com.example.chathub.model.AccountService
import com.example.chathub.model.LogService
import com.example.chathub.navigation.DestinationScreen
import com.example.chathub.snackbar.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val accountService: AccountService,
    logService: LogService
) : ChatAppViewModel(logService) {

    var uiState = mutableStateOf(SignUpUiState())
        private set

    private val name get() = uiState.value.name
    private val email get() = uiState.value.email
    private val password get() = uiState.value.password
    private val repeatPassword get() = uiState.value.repeatPassword

    fun onNameChange(newValue: String) {
        uiState.value = uiState.value.copy(name = newValue)
    }

    fun onEmailChange(newValue: String) {
        uiState.value = uiState.value.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(password = newValue)
    }

    fun onRepeatPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(repeatPassword = newValue)
    }

    fun onCreateAccountClick(openScreen: (String) -> Unit) {
        if (!password.passwordMatches(repeatPassword)) {
            SnackbarManager.showMessage(R.string.password_do_not_match)
            return
        }
        if (name.isBlank() || email.isBlank() || password.isBlank() || repeatPassword.isBlank()) {
            SnackbarManager.showMessage(R.string.all_fields_required)
            return
        }
        if (!email.isValidEmail()) {
            SnackbarManager.showMessage(R.string.email_error)
            return
        }

        viewModelScope.launch {
            try {
                accountService.createAccount(name, email, password)
                SnackbarManager.showMessage(R.string.account_created)
                openScreen(DestinationScreen.ProfilePicture.route)
            } catch (e: Exception) {
                SnackbarManager.showMessage(R.string.signup_failed)
            }
        }
    }
}

data class SignUpUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val repeatPassword: String = "",
)