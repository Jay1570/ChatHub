package com.example.chathub_android.models

data class ValidateTokenResponse(val valid: Boolean)

data class LoginRequest(
    val email: String,
    val password: String
)

data class SignupRequest(
    val name: String,
    val email: String,
    val password: String
)

data class AuthResponse(
    val token: String,
    val user: User
)
