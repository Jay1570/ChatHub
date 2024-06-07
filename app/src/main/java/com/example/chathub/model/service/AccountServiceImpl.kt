package com.example.chathub.model.service

import android.net.Uri
import com.example.chathub.model.AccountService
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AccountServiceImpl @Inject constructor(private val auth: FirebaseAuth) : AccountService {
    override val currentUserId: String
        get() = auth.currentUser?.uid.orEmpty()

    override val hasUser: Boolean
        get() = auth.currentUser != null

    override suspend fun authenticate(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    override fun signOut() {
        auth.signOut()
    }

    override suspend fun sendRecoveryEmail(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    override suspend fun googleSignIn(credential: AuthCredential) {
         auth.signInWithCredential(credential).await()
    }

    override suspend fun createAccount(name: String, email: String, password: String) {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        result.user?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).build())?.await()
    }

    override suspend fun updateProfilePicture(uri: Uri) {
        val user = auth.currentUser
        user?.updateProfile(UserProfileChangeRequest.Builder().setPhotoUri(uri).build())?.await()
    }
}