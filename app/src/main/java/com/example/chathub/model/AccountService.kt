package com.example.chathub.model

import android.net.Uri
import com.google.firebase.auth.AuthCredential

interface AccountService {
    val currentUserId: String
    val hasUser: Boolean
    suspend fun authenticate(email: String, password: String)
    fun signOut()
    suspend fun sendRecoveryEmail(email: String)
    suspend fun googleSignIn(credential: AuthCredential)
    suspend fun createAccount(name: String, email: String, password: String)
    suspend fun updateProfilePicture(uri: Uri)
}