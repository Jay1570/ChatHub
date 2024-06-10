package com.example.chathub.model.service

import android.icu.util.Calendar
import android.util.Log
import com.example.chathub.model.Chat
import com.example.chathub.model.ChatList
import com.example.chathub.model.Profile
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class ChatService @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: AccountService
) {

    val currentUserId = auth.currentUserId

    val chatList: Flow<List<ChatList>> =
        auth.currentUser.flatMapLatest { user ->
            firestore.collection(CHAT_LIST_COLLECTION)
                .where(
                    Filter.or(
                        Filter.equalTo("user1Id", currentUserId),
                        Filter.equalTo("user2Id", currentUserId)
                    )
                )
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(ChatList::class.java)
                }
                .catch { e ->
                    Log.e("ChatService", "Error fetching chats", e)
                    emit(emptyList())
                }
        }

    val profiles: Flow<List<Profile>> =
        chatList.flatMapLatest { chats ->
            val userIds = chats.flatMap { listOf(it.user1Id, it.user2Id) }
                .filter { it != currentUserId }
                .distinct()
            firestore.collection(PROFILE_COLLECTION)
                .whereIn("userId", userIds)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(Profile::class.java)
                }
                .catch { e ->
                    Log.e("ChatService", "Error fetching profiles", e)
                    emit(emptyList())
                }
        }

    suspend fun getProfile(chatId: String): Profile? {
        val userId = getChatId(chatId)
        return try {
            val profile = auth.getProfile(userId) ?: Profile()
            Log.d("ChatService", profile.userId)
            return profile
        } catch (e: FirebaseFirestoreException) {
            Log.e("ChatService",e.message.toString())
            null
        }
    }

    private suspend fun getChatId(chatId: String): String {
        val chatDoc = firestore.collection(CHAT_LIST_COLLECTION).document(chatId).get().await()
        if (chatDoc.exists()) {
            val chat = chatDoc.toObject(ChatList::class.java) ?: ChatList()
            return if (currentUserId != chat.user1Id) chat.user1Id else chat.user2Id
        } else {
            return ""
        }
    }

    fun getChats(sessionId: String): Flow<List<Chat>> {
        return firestore.collection(CHAT_COLLECTION)
            .whereEqualTo("sessionId", sessionId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(Chat::class.java)
            }
            .catch { e ->
                Log.e("ChatService", "Error fetching chats", e)
                emit(emptyList())
            }
    }

    suspend fun createChat(user2Id: String): ChatList? {
        try {
            val existingChat = fetchChatByUser(user2Id)
            if (existingChat != null) {
                return existingChat
            }

            val newChatDocRef = firestore.collection(CHAT_LIST_COLLECTION).document()
            val newChat = ChatList(chatId = newChatDocRef.id, user1Id = currentUserId, user2Id = user2Id)

            newChatDocRef.set(newChat).await()

            return newChat
        } catch (e: Exception) {
            Log.e("ChatService", "Error creating chat", e)
            return null
        }
    }

    private suspend fun fetchChatByUser(user2Id: String): ChatList? {
        try {
            val querySnapshot1 = firestore.collection(CHAT_LIST_COLLECTION)
                .whereEqualTo("user1Id", currentUserId)
                .whereEqualTo("user2Id", user2Id)
                .get()
                .await()

            val querySnapshot2 = firestore.collection(CHAT_LIST_COLLECTION)
                .whereEqualTo("user1Id", user2Id)
                .whereEqualTo("user2Id", currentUserId)
                .get()
                .await()


            if (!querySnapshot1.isEmpty) {

                return querySnapshot1.documents[0].toObject(ChatList::class.java)
            }
            if (!querySnapshot2.isEmpty) {
                return querySnapshot2.documents[0].toObject(ChatList::class.java)
            }
        } catch (e: Exception) {
            Log.e("ChatService", "Error finding chat by users", e)
        }
        return null
    }

    suspend fun fetchUsersByNameAndEmail(query: String): List<Profile>{
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
            document.toObject(Profile::class.java)
        }

        val usersByEmail = usersByEmailSnapshot.documents.mapNotNull { document ->
            document.toObject(Profile::class.java)
        }
        return (usersByName + usersByEmail).distinctBy { it.userId }
    }

    suspend fun sendMessage(sessionId: String, message: String, receiverId: String) {
        try {
            val chat = Chat(
                sessionId = sessionId,
                message = message,
                senderId = currentUserId,
                receiverId = receiverId,
                timestamp = Calendar.getInstance().time.toString(),
                isRead = false
            )
            firestore.collection(CHAT_COLLECTION).add(chat).await()
        } catch (e: Exception) {
            Log.e("ChatService", "Error sending message", e)
        }
    }

    companion object {
        private const val CHAT_LIST_COLLECTION = "chatList"
        private const val PROFILE_COLLECTION = "profiles"
        private const val CHAT_COLLECTION = "chats"
    }
}