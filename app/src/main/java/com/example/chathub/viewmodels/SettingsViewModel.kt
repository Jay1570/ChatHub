package com.example.chathub.viewmodels

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.chathub.ChatAppViewModel
import com.example.chathub.model.service.AccountService
import com.example.chathub.model.service.LogService
import com.example.chathub.navigation.DestinationScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val accountService: AccountService,
    logService: LogService
) : ChatAppViewModel(logService) {

    fun signOut(context: Context, openAndPopUp: (String) -> Unit) {
        viewModelScope.launch {
            accountService.signOut(context)
        }.invokeOnCompletion {
            openAndPopUp(DestinationScreen.Login.route)
        }
    }
}