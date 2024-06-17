package com.example.chathub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.chathub.ui.theme.ChatHubTheme
import com.example.chathub.viewmodels.Theme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var themePreferenceManager: ThemePreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val theme by themePreferenceManager.theme.collectAsState(initial = Theme.SYSTEM_DEFAULT)
            val darkTheme: Boolean =
                when(theme) {
                    Theme.DARK -> true
                    Theme.LIGHT -> false
                    Theme.SYSTEM_DEFAULT -> isSystemInDarkTheme()
                }
            val dynamicColor by themePreferenceManager.dynamicColor.collectAsState(initial = false)
            ChatHubTheme(darkTheme = darkTheme, dynamicColor = dynamicColor) {
                ChatApp()
            }
        }
    }
}
