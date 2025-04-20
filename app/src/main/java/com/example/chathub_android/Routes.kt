package com.example.chathub_android

import kotlinx.serialization.Serializable

sealed interface Routes

@Serializable
data object AppEntryPoint: Routes

@Serializable
data object Login : Routes

@Serializable
data object Register : Routes

@Serializable
data object Home : Routes