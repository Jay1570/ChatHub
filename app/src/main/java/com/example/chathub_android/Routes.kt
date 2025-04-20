package com.example.chathub_android

sealed interface Routes

data object Login : Routes

data object Register : Routes