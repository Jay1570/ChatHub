package com.example.chathub.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.chathub.ChatAppViewModel
import com.example.chathub.R
import com.example.chathub.model.Profile
import com.example.chathub.model.service.AccountService
import com.example.chathub.model.service.ChatService
import com.example.chathub.model.service.LogService
import com.example.chathub.navigation.DestinationScreen
import com.example.chathub.snackbar.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val accountService: AccountService,
    private val chatService: ChatService,
    logService: LogService
) : ChatAppViewModel(logService) {

    val chatList = chatService.chatList
    val profiles = chatService.profiles

    private val _userList = MutableStateFlow<List<Profile>>(emptyList())
    val userList: StateFlow<List<Profile>> get() = _userList

    var uiState = mutableStateOf(ChatListUiState())
        private set

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

    fun onUserClick(userId: String, openScreen: (String) -> Unit) {
        viewModelScope.launch{
            val chat = chatService.createChat(userId)
            openScreen(DestinationScreen.Chat.createRoute(chat?.chatId))
        }.invokeOnCompletion { uiState.value = uiState.value.copy(isSearchBarVisible = false) }
    }

    fun onChatClick(chatId: String, openScreen: (String) -> Unit) {
        openScreen(DestinationScreen.Chat.createRoute(chatId))
    }

    fun onSettingsClick(openScreen: (String) -> Unit) {
        openScreen(DestinationScreen.Settings.route)
    }
}

data class ChatListUiState(
    val query: String = "",
    val currentUserId: String = "",
    val isSearchBarVisible: Boolean = false
)