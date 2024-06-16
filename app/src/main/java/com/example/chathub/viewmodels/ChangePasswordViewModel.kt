package com.example.chathub.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.chathub.ChatAppViewModel
import com.example.chathub.R
import com.example.chathub.ext.isValidPassword
import com.example.chathub.ext.passwordMatches
import com.example.chathub.model.service.AccountService
import com.example.chathub.model.service.LogService
import com.example.chathub.snackbar.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val accountService: AccountService,
    logService: LogService
) : ChatAppViewModel(logService) {

    var uiState = mutableStateOf(ChangePasswordUiState())
        private set

    private val oldPassword get() = uiState.value.oldPassword
    private val newPassword get() = uiState.value.newPassword
    private val confirmPassword get() = uiState.value.confirmPassword

    fun onOldPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(oldPassword = newValue)
    }

    fun onNewPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(newPassword = newValue)
    }

    fun onConfirmPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(confirmPassword = newValue)
    }

    fun changePassword(navigateUp: () -> Unit) {

        if (oldPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
            SnackbarManager.showMessage(R.string.all_fields_required)
            return
        }

        if (!newPassword.isValidPassword()) {
            SnackbarManager.showMessage(R.string.password_error)
            return
        }

        if (oldPassword.passwordMatches(newPassword)) {
            SnackbarManager.showMessage(R.string.password_match)
            return
        }

        if (!newPassword.passwordMatches(confirmPassword)) {
            SnackbarManager.showMessage(R.string.password_do_not_match)
            return
        }

        uiState.value = uiState.value.copy(inProcess = true)
        viewModelScope.launch {
            val successful = accountService.changePassword(oldPassword, newPassword)
            if (successful) {
                navigateUp()
            }
        }.invokeOnCompletion {
            uiState.value = uiState.value.copy(inProcess = false)
        }
    }
}

data class ChangePasswordUiState(
    val oldPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val inProcess: Boolean = false
)