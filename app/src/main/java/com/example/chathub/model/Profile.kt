package com.example.chathub.model

data class Profile(
    val userId: String = "",
    val email: String? = "",
    val name: String? = "",
    val imageUrl: String? = ""
) {
    fun toMap() = mapOf(
        "userId" to userId,
        "email" to email,
        "name" to name,
        "imageUrl" to imageUrl
    )
}