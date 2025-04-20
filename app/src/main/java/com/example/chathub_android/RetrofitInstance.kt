package com.example.chathub_android

import com.example.chathub_android.models.ErrorResponse
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "http://192.168.29.124:8080/api/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    fun parseErrorResponse(errorBody: ResponseBody?): ErrorResponse {
        return try {
            val gson = Gson()
            gson.fromJson(errorBody?.string(), ErrorResponse::class.java)
                ?: ErrorResponse(error = "Unknown error occurred")
        } catch (e: Exception) {
            ErrorResponse(error = "Failed to parse error response")
        }
    }
}