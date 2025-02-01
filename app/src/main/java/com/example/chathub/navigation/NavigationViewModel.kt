package com.example.chathub.navigation

import com.example.chathub.ChatAppViewModel
import com.example.chathub.service.AccountService
import com.example.chathub.service.LogService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
    private val accountService: AccountService,
    logService: LogService
) : ChatAppViewModel(logService) {
    fun checkUserStatus() : Routes {
        return if (accountService.hasUser) {
            Home
        } else {
            Login
        }
    }
}