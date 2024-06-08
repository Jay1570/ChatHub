package com.example.chathub.model

data class Profile(
    val userId: String = "",
    val email: String? = "",
    val name: String? = "",
    val imageUrl: String = "https://firebasestorage.googleapis.com/v0/b/chathub-cc672.appspot.com/o/icons8-user-50.png?alt=media&token=7ae8302b-538b-41c6-8f58-fe25a90f35a4",
    val statusMessage: String = "Busy",
) {
    fun toMap() = mapOf(
        "userId" to userId,
        "email" to email,
        "name" to name,
        "imageUrl" to imageUrl
    )
}