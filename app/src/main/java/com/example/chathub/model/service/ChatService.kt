package com.example.chathub.model.service

import com.example.chathub.model.Chat
import com.example.chathub.model.ChatUser
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChatService @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: AccountService
) {
    suspend fun fetchChats(): List<Chat> {
        val currentUserId = auth.currentUserId
        val chatSnapshot = firestore.collection(CHAT_COLLECTION)
            .where(
                Filter.or(
                    Filter.equalTo("user1.userId", currentUserId),
                    Filter.equalTo("user2.userId", currentUserId)
                )
            )
            .get()
            .await()

        return chatSnapshot.documents.mapNotNull { document ->
            document.toObject(Chat::class.java)
        }
    }

    suspend fun fetchUsersByNameAndEmail(query: String): List<ChatUser>{
        val usersByNameSnapshot = firestore.collection(PROFILE_COLLECTION)
            .whereGreaterThanOrEqualTo("name", query)
            .whereLessThanOrEqualTo("name", query + '\uf8ff')
            .get()
            .await()

        val usersByEmailSnapshot = firestore.collection(PROFILE_COLLECTION)
            .whereGreaterThanOrEqualTo("email", query)
            .whereLessThanOrEqualTo("email", query + '\uf8ff')
            .get()
            .await()

        val usersByName = usersByNameSnapshot.documents.mapNotNull { document ->
            document.toObject(ChatUser::class.java)
        }

        val usersByEmail = usersByEmailSnapshot.documents.mapNotNull { document ->
            document.toObject(ChatUser::class.java)
        }

        // Combine the results and remove duplicates
        return (usersByName + usersByEmail).distinctBy { it.userId }
    }

    companion object {
        private const val CHAT_COLLECTION = "chatList"
        private const val PROFILE_COLLECTION = "profiles"
    }
}