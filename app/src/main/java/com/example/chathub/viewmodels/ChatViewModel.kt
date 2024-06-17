package com.example.chathub.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.chathub.ChatAppViewModel
import com.example.chathub.ThemePreferenceManager
import com.example.chathub.model.Chat
import com.example.chathub.model.Profile
import com.example.chathub.model.service.ChatService
import com.example.chathub.model.service.LogService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val chatService: ChatService,
    themePreferenceManager: ThemePreferenceManager,
    logService: LogService
) : ChatAppViewModel(logService) {

    val uiState = mutableStateOf(ChatUiState())

    private var sessionId: String = checkNotNull(savedStateHandle["chatId"])

    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> get() = _chats

    val profile: Profile get() = uiState.value.profile
    val theme = themePreferenceManager.theme

    init {
        sessionId = checkNotNull(savedStateHandle["chatId"])
        uiState.value = uiState.value.copy(currentUserId =  chatService.currentUserId)
        loadProfile()
        loadChats(sessionId)
    }

    private fun loadProfile() {
        viewModelScope.launch {
            uiState.value= uiState.value.copy(profile = chatService.getProfile(sessionId) ?: Profile())
        }
    }
    private fun loadChats(sessionId: String) {
        viewModelScope.launch {
            chatService.getChats(sessionId)
                .catch { e ->
                    Log.e("ChatViewModel", "Error loading chats", e)
                }
                .collect { chatList ->
                    _chats.value = chatList
                    Log.d("ChatViewModel", "Loaded chats: ${chatList.size}")
                }
        }
    }

    fun sendMessage() {
        if (uiState.value.message.isNotBlank()) {
            viewModelScope.launch {
                chatService.sendMessage(sessionId, uiState.value.message, profile.userId)
            }
        }
    }

    fun markMessagesAsRead() {
        viewModelScope.launch {
            val unreadMessages = _chats.value.filter { it.receiverId == uiState.value.currentUserId && !it.read }
            if (unreadMessages.isNotEmpty()) {
                chatService.markMessagesAsRead(unreadMessages.map { it.chatId })
            }
        }.invokeOnCompletion {
            uiState.value = uiState.value.copy(message = "")
        }
    }

    fun onMessageChange(newValue: String) {
        uiState.value = uiState.value.copy(message = newValue)
    }
}

data class ChatUiState(
    val currentUserId: String = "",
    val message: String = "",
    val profile: Profile = Profile()
)