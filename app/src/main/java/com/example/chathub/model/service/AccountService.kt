package com.example.chathub.model.service

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.chathub.R
import com.example.chathub.model.Profile
import com.example.chathub.model.trace
import com.example.chathub.snackbar.SnackbarManager
import com.example.chathub.snackbar.SnackbarMessage
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@Suppress("DEPRECATION")
class AccountService @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    val currentUserId: String
        get() = auth.currentUser?.uid.orEmpty()

    val profile: Flow<Profile> = flow {
        val docRef = firestore.collection(PROFILE_COLLECTION).document(currentUserId).get().await()
        val profile = docRef.toObject(Profile::class.java)
        emit(profile?: Profile())
    }

    suspend fun getProfile(userId: String = currentUserId): Profile? {
        return try {
            val profileDoc = firestore.collection(PROFILE_COLLECTION).document(userId).get().await()
            if (profileDoc.exists()) {
                val profileData = profileDoc.toObject(Profile::class.java)
                profileData?.copy(id = profileDoc.id)
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

    suspend fun uploadImageToFirebase(uri: Uri): String {
        val storageReference = FirebaseStorage.getInstance().reference
        val imageReference = storageReference.child("profileImages/${currentUserId}.jpg")
        imageReference.putFile(uri).await()
        return imageReference.downloadUrl.await().toString()
    }

    suspend fun authenticate(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    fun isSignedInWithGoogle(context: Context): Boolean {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        return account != null
    }

    suspend fun signOut(context: Context) {

        val signInClient = GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_SIGN_IN)
        val account = GoogleSignIn.getLastSignedInAccount(context)

        if (account != null) {
            signInClient.signOut().await()
        }
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

    suspend fun changePassword(oldPassword: String, newPassword: String) : Boolean{
        try {
            val user = auth.currentUser ?: return false
            val email = user.email ?: return false
            val credential = EmailAuthProvider.getCredential(email, oldPassword)
            user.reauthenticate(credential).await()
            user.updatePassword(newPassword).await()
            SnackbarManager.showMessage(R.string.password_changed_successful)
            return true
        } catch (e: Exception) {
            Log.e("AccountService", e.message.toString())
            SnackbarManager.showMessage(SnackbarMessage.StringSnackbar(e.message.toString()))
            return false
        }
    }

    suspend fun storeProfile(userData: Profile) {
        val userId = currentUserId
        val profile = userData.copy(userId = userId)
        val profileDocRef = firestore.collection(PROFILE_COLLECTION).document(userId)
        val profileDoc = profileDocRef.get().await()
        if (!profileDoc.exists()) {
            try {
                profileDocRef.set(profile).await()
                Log.d("AccountService", "Profile stored successfully")
            } catch (e: Exception) {
                Log.e("AccountService", "Error storing profile", e)
                throw e
            }
        }
    }
    companion object {
        private const val PROFILE_COLLECTION = "profiles"
        private const val UPDATE_PROFILE_TRACE = "updateProfile"
    }
}