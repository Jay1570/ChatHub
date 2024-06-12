package com.example.chathub.model

import com.google.firebase.firestore.DocumentId

data class ChatList(
    @DocumentId val chatId: String = "",
    val user1Id: String = "",
    val user2Id: String= "",
    val unreadCount: Int = 0,
)