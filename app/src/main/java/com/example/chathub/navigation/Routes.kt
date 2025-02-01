package com.example.chathub.navigation

import kotlinx.serialization.Serializable

sealed interface Routes

@Serializable
data object Login : Routes

@Serializable
data object SignUp : Routes

@Serializable
data object Profile : Routes

@Serializable
data object Home : Routes

@Serializable
data class ChatRoute(
    val id: String?
) : Routes

@Serializable
data object Settings : Routes

@Serializable
data object ChangePassword : Routes