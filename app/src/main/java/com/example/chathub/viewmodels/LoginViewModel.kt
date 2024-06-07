package com.example.chathub.viewmodels

import androidx.compose.runtime.mutableStateOf
import com.example.chathub.ChatAppViewModel
import com.example.chathub.R
import com.example.chathub.ext.isValidEmail
import com.example.chathub.ext.isValidPassword
import com.example.chathub.model.AccountService
import com.example.chathub.model.LogService
import com.example.chathub.navigation.DestinationScreen
import com.example.chathub.snackbar.SnackbarManager
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val accountService: AccountService,
    logService: LogService
) : ChatAppViewModel(logService) {
    var uiState = mutableStateOf(LoginUiState())
        private set

    private val email get() = uiState.value.email
    private val password get() = uiState.value.password

    fun onEmailChange(newValue: String) {
        uiState.value = uiState.value.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(password = newValue)
    }

    fun onSignInClick(openAndPopUp: (String, String) -> Unit) {
        if (!email.isValidEmail()) {
            SnackbarManager.showMessage(R.string.email_error)
            return
        }
        if (password.isBlank()) {
            SnackbarManager.showMessage(R.string.empty_password_error)
            return
        }
        if (!password.isValidPassword()) {
            SnackbarManager.showMessage(R.string.password_error)
            return
        }
        launchCatching {
            accountService.authenticate(email, password)
            openAndPopUp(DestinationScreen.ChatList.route, DestinationScreen.Login.route)
        }
    }

    fun onForgotPasswordClick() {
        if (!email.isValidEmail()) {
            SnackbarManager.showMessage(R.string.email_error)
            return
        }
        launchCatching {
            accountService.sendRecoveryEmail(email)
            SnackbarManager.showMessage(R.string.recovery_email_sent)
        }
    }

    fun onGoogleLoginClick(account: GoogleSignInAccount, openAndPopUp: (String, String) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        launchCatching {
            accountService.googleSignIn(credential)
            val name = account.displayName ?: ""
            val email = account.email ?: ""
            val profilePicture = account.photoUrl.toString()

            uiState.value = uiState.value.copy(
                email = email,
                name = name,
                profilePicture = profilePicture
            )

            // Navigate to chat list after successful login
            openAndPopUp(DestinationScreen.ChatList.route, DestinationScreen.Login.route)
        }
    }
}

data class LoginUiState(
    val email: String = "",
    val name: String = "",
    val password: String = "",
    val profilePicture: String = ""
)