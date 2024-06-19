package com.example.chathub.screens.sign_up

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.chathub.ChatAppViewModel
import com.example.chathub.R
import com.example.chathub.ext.isValidEmail
import com.example.chathub.ext.isValidPassword
import com.example.chathub.ext.passwordMatches
import com.example.chathub.model.Profile
import com.example.chathub.model.service.AccountService
import com.example.chathub.model.service.LogService
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
        if (name.isBlank() || email.isBlank() || password.isBlank() || repeatPassword.isBlank()) {
            SnackbarManager.showMessage(R.string.all_fields_required)
            return
        }
        if (!email.isValidEmail()) {
            SnackbarManager.showMessage(R.string.email_error)
            return
        }
        if (!password.isValidPassword()) {
            SnackbarManager.showMessage(R.string.password_error)
            return
        }
        if (!password.passwordMatches(repeatPassword)) {
            SnackbarManager.showMessage(R.string.password_do_not_match)
            return
        }
        uiState.value = uiState.value.copy(inProcess = true)
        viewModelScope.launch {
            try {
                accountService.createAccount(name, email, password)
                accountService.storeProfile(Profile(name = name, email = email))
                SnackbarManager.showMessage(R.string.account_created)
                openScreen(DestinationScreen.ChatList.route)
            } catch (e: Exception) {
                SnackbarManager.showMessage(R.string.signup_failed)
            } finally {
                uiState.value = uiState.value.copy(inProcess = false)
            }
        }
    }
}

data class SignUpUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val repeatPassword: String = "",
    val inProcess: Boolean = false
)