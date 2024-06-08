package com.example.chathub.model

import com.google.firebase.firestore.DocumentId

data class Chat(
    @DocumentId val chatId: String = "",
    val user1: ChatUser = ChatUser(),
    val user2: ChatUser = ChatUser(),
)

data class ChatUser(
    val userId: String = "",
    val email: String = "",
    val name: String = "",
    val imageUrl: String = ""
)