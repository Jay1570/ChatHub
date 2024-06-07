package com.example.chathub.viewmodels

import com.example.chathub.ChatAppViewModel
import com.example.chathub.model.service.AccountService
import com.example.chathub.model.service.LogService
import com.example.chathub.navigation.DestinationScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
    private val accountService: AccountService,
    logService: LogService
) : ChatAppViewModel(logService) {
    fun checkUserStatus() : String {
        return if (accountService.hasUser) {
            DestinationScreen.ChatList.route
        } else {
            DestinationScreen.Login.route
        }
    }
}