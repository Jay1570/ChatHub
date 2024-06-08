package com.example.chathub.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.chathub.ChatAppViewModel
import com.example.chathub.R
import com.example.chathub.model.Chat
import com.example.chathub.model.ChatUser
import com.example.chathub.model.service.AccountService
import com.example.chathub.model.service.ChatService
import com.example.chathub.model.service.LogService
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

    private val _chatList = MutableStateFlow<List<Chat>>(emptyList())
    val chatList: StateFlow<List<Chat>> get() = _chatList

    private val _userList = MutableStateFlow<List<ChatUser>>(emptyList())
    val userList: StateFlow<List<ChatUser>> get() = _userList

    var uiState = mutableStateOf(ChatListUiState())

    init {
        fetchChatList()
    }

    fun onSearchClick() {
        uiState.value = uiState.value.copy(isSearchBarVisible = !uiState.value.isSearchBarVisible)
    }

    fun onSearch(query: String) {
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

    private fun fetchChatList() {
        uiState.value = uiState.value.copy(currentUserId = accountService.currentUserId)
        viewModelScope.launch {
            try {
                val chats = chatService.fetchChats()
                _chatList.value = chats
            } catch (e: Exception) {
                Log.e("ChatListViewModel", e.toString())
                SnackbarManager.showMessage(R.string.chat_list_error)
            }
        }
    }
}

data class ChatListUiState(
    val currentUserId: String = "",
    val isSearchBarVisible: Boolean = false
)