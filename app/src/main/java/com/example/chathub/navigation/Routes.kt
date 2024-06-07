package com.example.chathub.navigation

sealed class DestinationScreen(var route: String) {
    object Login: DestinationScreen("login")
    object SignUp: DestinationScreen("signup")
    object Profile: DestinationScreen("profile")
    object ChatList: DestinationScreen("chatList")
    object Chat: DestinationScreen("chat/{chatId}") {
        fun createRoute(id: String) = "chat/$id"
    }
    object StatusList: DestinationScreen("statusList")
    object Status: DestinationScreen("status/{userId}") {
        fun createRoute(id: String) = "chat/$id"
    }
    object ProfilePicture: DestinationScreen("profilePicture")
}