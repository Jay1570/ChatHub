package com.example.chathub_android

import com.example.chathub_android.models.AuthResponse
import com.example.chathub_android.models.LoginRequest
import com.example.chathub_android.models.SignupRequest
import com.example.chathub_android.models.ValidateTokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @GET("auth/validate-token")
    suspend fun validateToken(@Header("Authorization") token: String): Response<ValidateTokenResponse>

    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<AuthResponse>

    @POST("auth/signup")
    suspend fun signup(@Body loginRequest: SignupRequest): Response<AuthResponse>
}