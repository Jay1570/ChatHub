package com.example.chathub.model

import com.google.firebase.firestore.DocumentId

data class Profile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val email: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val statusMessage: String = "Busy",
)