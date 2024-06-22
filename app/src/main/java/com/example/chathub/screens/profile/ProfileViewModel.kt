package com.example.chathub.screens.profile

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.chathub.ChatAppViewModel
import com.example.chathub.R
import com.example.chathub.model.Profile
import com.example.chathub.service.AccountService
import com.example.chathub.service.LogService
import com.example.chathub.snackbar.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val accountService: AccountService,
    logService: LogService
) : ChatAppViewModel(logService) {

    val profile = accountService.profile

    var uiState = mutableStateOf(ProfileUiState())
        private set

    private val profileState:Profile get() = uiState.value.profile

    init {
        viewModelScope.launch {
            profile.collect { profile ->
                uiState.value = uiState.value.copy(profile = profile)
            }
        }
    }

    fun onNameChange(newValue: String) {
        uiState.value = uiState.value.copy(profile = profileState.copy(name = newValue))
    }

    fun onStatusMessageChange(newValue: String) {
        uiState.value = uiState.value.copy(profile = profileState.copy(statusMessage = newValue))
    }

    fun onImageChange(uri: Uri) {
        uiState.value = uiState.value.copy(inProcess = true)
        viewModelScope.launch {
            try {
                val imageUrl = accountService.uploadImageToFirebase(uri)
                uiState.value = uiState.value.copy(profile = profileState.copy(imageUrl = imageUrl))
                updateProfile()
                SnackbarManager.showMessage(R.string.successful_update_profile)
            } catch (e: Exception) {
                Log.e("ProfileViewModel", e.message.toString())
                SnackbarManager.showMessage(R.string.error_update_profile)
            }
        }.invokeOnCompletion {
            uiState.value = uiState.value.copy(inProcess = false)
        }
    }

    fun onDoneClick(navigateUp: () -> Unit) {
        uiState.value = uiState.value.copy(inProcess = true)
        viewModelScope.launch {
            try {
                updateProfile()
                SnackbarManager.showMessage(R.string.successful_update_profile)
                uiState.value = uiState.value.copy(inProcess = false)
                navigateUp()
            } catch (e: Exception) {
                Log.e("ProfileViewModel", e.message.toString())
                SnackbarManager.showMessage(R.string.error_update_profile)
            }
        }
    }

    private fun updateProfile() {
        viewModelScope.launch {
            accountService.update(profileState)
        }
    }
}

data class ProfileUiState(
    val profile: Profile = Profile(),
    val inProcess: Boolean = false
)