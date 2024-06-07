package com.example.chathub.model.service

import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class StorageService @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: AccountService
) {

}