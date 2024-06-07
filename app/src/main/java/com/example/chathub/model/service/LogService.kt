package com.example.chathub.model.service

import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import javax.inject.Inject

class LogService @Inject constructor() {
     fun logNonFatalCrash(throwable: Throwable) =
        Firebase.crashlytics.recordException(throwable)
}