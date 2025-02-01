package com.example.chathub.screens.settings

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.chathub.*
import com.example.chathub.navigation.ChangePassword
import com.example.chathub.navigation.Login
import com.example.chathub.navigation.Profile
import com.example.chathub.navigation.Routes
import com.example.chathub.service.AccountService
import com.example.chathub.service.LogService
import com.example.chathub.snackbar.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val accountService: AccountService,
    logService: LogService,
    private val themePreferenceManager: ThemePreferenceManager
) : ChatAppViewModel(logService) {

    val profile = accountService.profile
    var uiState = mutableStateOf(SettingsUiState())
        private set
    val isDynamicColorEnabled = themePreferenceManager.dynamicColor

    init {
        viewModelScope.launch {
            themePreferenceManager.theme.collectLatest { theme ->
                uiState.value = uiState.value.copy(currentTheme = theme)
            }
            themePreferenceManager.dynamicColor.collectLatest { isEnabled ->
                uiState.value = uiState.value.copy(isDynamicColorEnabled = isEnabled)
            }
        }
    }

    fun signOut(context: Context, openAndPopUp: (Routes) -> Unit) {
        viewModelScope.launch {
            accountService.signOut(context)
        }.invokeOnCompletion {
            openAndPopUp(Login)
        }
    }

    fun onDynamicColorSwitchChanged(isEnabled: Boolean) {
        viewModelScope.launch{
            themePreferenceManager.setDynamicColorEnabled(isEnabled)
        }
        uiState.value = uiState.value.copy(isDynamicColorEnabled = isEnabled)
    }

    fun onThemeClick() {
        uiState.value = uiState.value.copy(isThemeDialogVisible = true)
    }

    fun onDismissThemeDialog() {
        uiState.value = uiState.value.copy(isThemeDialogVisible = false)
    }

    fun onThemeSelected(theme: Theme) {
        viewModelScope.launch {
            themePreferenceManager.setTheme(theme)
        }
        uiState.value = uiState.value.copy(currentTheme = theme, isThemeDialogVisible = false)
    }

    fun onProfileClick(openScreen: (Routes) -> Unit){
        openScreen(Profile)
    }

    fun onAccountSecurityClick(context: Context, openScreen: (Routes) -> Unit){
        if (accountService.isSignedInWithGoogle(context)) {
            SnackbarManager.showMessage(R.string.change_password_google_error)
            return
        }
        openScreen(ChangePassword)
    }
}

enum class Theme {
    LIGHT,
    DARK,
    SYSTEM_DEFAULT;

    fun toTextResId(): Int {
        return when (this) {
            DARK -> R.string.dark
            LIGHT -> R.string.light
            SYSTEM_DEFAULT -> R.string.system
        }
    }
}

data class SettingsUiState(
    val isThemeDialogVisible: Boolean = false,
    val currentTheme: Theme = Theme.SYSTEM_DEFAULT,
    val isDynamicColorEnabled: Boolean = false
)