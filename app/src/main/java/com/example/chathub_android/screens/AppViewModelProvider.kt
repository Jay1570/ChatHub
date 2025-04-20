package com.example.chathub_android.screens

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.chathub_android.MyApp
import com.example.chathub_android.screens.entry.EntryScreenViewModel
import com.example.chathub_android.screens.login.LoginViewModel
import com.example.chathub_android.screens.register.RegistrationViewModel

object AppViewModelProvider {
    val factory = viewModelFactory {
        initializer {
            EntryScreenViewModel(myApp().userPreference)
        }

        initializer {
            LoginViewModel(myApp().userPreference)
        }

        initializer {
            RegistrationViewModel(myApp().userPreference)
        }
    }
}

fun CreationExtras.myApp(): MyApp {
    return this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApp
}