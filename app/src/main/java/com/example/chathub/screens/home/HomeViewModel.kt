package com.example.chathub.screens.home

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.chathub.*
import com.example.chathub.model.Profile
import com.example.chathub.navigation.ChatRoute
import com.example.chathub.navigation.Routes
import com.example.chathub.navigation.Settings
import com.example.chathub.service.AccountService
import com.example.chathub.service.ChatService
import com.example.chathub.service.LogService
import com.example.chathub.snackbar.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val accountService: AccountService,
    private val chatService: ChatService,
    logService: LogService
) : ChatAppViewModel(logService) {

    val chatList = chatService.chatList
    val profiles = chatService.profiles

    private val _userList = MutableStateFlow<List<Profile>>(emptyList())
    val userList: StateFlow<List<Profile>> get() = _userList

    var uiState = mutableStateOf(HomeUiState())
        private set

    val unreadMessageCounts = chatService.unreadMessageCount

    private val query:String get() = uiState.value.query


    init {
        uiState.value  = uiState.value.copy(currentUserId = accountService.currentUserId)
        viewModelScope.launch {
            profiles.collect { profile ->
                Log.d("ChatListViewModel", "Loaded profiles: ${profile.size}")
            }
        }
    }

    fun onSearchClick() {
        _userList.value = emptyList()
        uiState.value = uiState.value.copy(isSearchBarVisible = !uiState.value.isSearchBarVisible)
        uiState.value = uiState.value.copy(query = "")
    }

    fun onSearch(newValue: String) {
        uiState.value = uiState.value.copy(query = newValue)
        if (query == "") {
            _userList.value = emptyList()
            return
        }
        viewModelScope.launch {
            try {
                val users = chatService.fetchUsersByNameAndEmail(query)
                _userList.value = users
            } catch (e: Exception) {
                SnackbarManager.showMessage(R.string.user_list_error)
            }
        }
    }

    fun onUserClick(userId: String, openScreen: (Routes) -> Unit) {
        viewModelScope.launch{
            val chat = chatService.createChat(userId)
            openScreen(ChatRoute(chat?.chatId))
        }.invokeOnCompletion { uiState.value = uiState.value.copy(isSearchBarVisible = false) }
    }

    fun onChatClick(chatId: String, openScreen: (Routes) -> Unit) {
        openScreen(ChatRoute(chatId))
    }

    fun onSettingsClick(openScreen: (Routes) -> Unit) {
        openScreen(Settings)
    }
}

data class HomeUiState(
    val query: String = "",
    val currentUserId: String = "",
    val isSearchBarVisible: Boolean = false
)