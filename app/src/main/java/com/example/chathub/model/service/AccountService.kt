package com.example.chathub.model.service

import android.util.Log
import com.example.chathub.model.Profile
import com.example.chathub.model.trace
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AccountService @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    val currentUserId: String
        get() = auth.currentUser?.uid.orEmpty()

    suspend fun getProfile(userId: String): Profile? {
        return try {
            val profileDoc = firestore.collection(PROFILE_COLLECTION).document(userId).get().await()
            if (profileDoc.exists()) {
                val profileData = profileDoc.toObject(Profile::class.java)
                profileData?.copy(userId = profileDoc.id)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("AccountService", "Error getting profile", e)
            null
        }
    }

    val hasUser: Boolean
        get() = auth.currentUser != null

    suspend fun update(profile: Profile): Unit =
    trace(UPDATE_PROFILE_TRACE) {
            firestore.collection(PROFILE_COLLECTION).document(profile.userId).set(profile).await()
        }

    suspend fun authenticate(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    fun signOut() {
        auth.signOut()
    }

    suspend fun sendRecoveryEmail(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    suspend fun googleSignIn(credential: AuthCredential) {
         auth.signInWithCredential(credential).await()
    }

    suspend fun createAccount(name: String, email: String, password: String) {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        result.user?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).build())?.await()
    }

    suspend fun storeOrUpdateProfile(userData: Profile) {
        val userId = currentUserId
        val profile = userData.copy(userId = userId)
        val profileDocRef = firestore.collection("profiles").document(userId)

        try {
            profileDocRef.set(profile).await()
            Log.d("AccountService", "Profile stored/updated successfully")
        } catch (e: Exception) {
            Log.e("AccountService", "Error storing/updating profile", e)
            throw e
        }
    }
    companion object {
        private const val PROFILE_COLLECTION = "profiles"
        private const val USER_ID_FIELD = "userId"
        private const val UPDATE_PROFILE_TRACE = "updateProfile"
    }
}