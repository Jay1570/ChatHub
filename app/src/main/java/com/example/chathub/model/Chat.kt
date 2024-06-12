package com.example.chathub.model

import com.google.firebase.firestore.DocumentId

data class Chat(
    @DocumentId val chatId: String = "",
    val message: String = "",
    val sessionId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val timestamp: String = "",
    val read: Boolean = false,
)