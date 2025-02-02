package com.example.chathub.screens.login

import androidx.compose.runtime.mutableStateOf
import com.example.chathub.*
import com.example.chathub.ext.isValidEmail
import com.example.chathub.ext.isValidPassword
import com.example.chathub.model.Profile
import com.example.chathub.navigation.Home
import com.example.chathub.navigation.Login
import com.example.chathub.navigation.Routes
import com.example.chathub.service.AccountService
import com.example.chathub.service.LogService
import com.example.chathub.snackbar.SnackbarManager
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
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

    fun onSignInClick(openAndPopUp: (Routes, Routes) -> Unit) {
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

        uiState.value = uiState.value.copy(inProcess = true)
        launchCatching {
            accountService.authenticate(email, password)
            openAndPopUp(Home, Login)
        }.invokeOnCompletion {
            uiState.value = uiState.value.copy(inProcess = false)
        }
    }

    fun onGoogleLoginClick(account: GoogleIdTokenCredential, openAndPopUp: (Routes, Routes) -> Unit) {
        uiState.value = uiState.value.copy(inProcess = true)
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        launchCatching {
            accountService.googleSignIn(credential)
            val name = account.displayName ?: ""
            val email = account.id
            val imageUrl = account.profilePictureUri?.toString() ?: ""

            accountService.storeProfile(Profile(name = name, email =  email, imageUrl =  imageUrl))

            openAndPopUp(Home, Login)
        }.invokeOnCompletion {
            uiState.value = uiState.value.copy(inProcess = false)
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