package com.example.chathub.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class ChatList(
    @DocumentId val chatId: String = "",
    val user1Id: String = "",
    val user2Id: String= "",
    val timestamp: Timestamp = Timestamp.now(),
)