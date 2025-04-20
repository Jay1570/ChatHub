package com.example.chathub_android

import android.app.Application
import com.google.firebase.FirebaseApp

class MyApp : Application() {

    lateinit var themePreference: ThemePreference
    lateinit var userPreference: UserPreference

    override fun onCreate() {
        super.onCreate()
        themePreference = ThemePreference(this)
        userPreference = UserPreference(this)
        FirebaseApp.initializeApp(this)
    }
}