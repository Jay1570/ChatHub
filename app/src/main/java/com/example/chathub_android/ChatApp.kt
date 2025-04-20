package com.example.chathub_android

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chathub_android.screens.entry.EntryScreen
import com.example.chathub_android.screens.home.HomeScreen
import com.example.chathub_android.screens.login.LoginScreen
import com.example.chathub_android.screens.register.RegistrationScreen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ChatApp() {
    Surface(color = MaterialTheme.colorScheme.background) {
        val appState = rememberAppState()
        Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
            NavHost(
                navController = appState.navController,
                startDestination = AppEntryPoint
            ) {
                composable<AppEntryPoint> {
                    EntryScreen(navigateAndClearBackStack = {
                        appState.clearAndNavigate(it)
                    })
                }

                composable<Login>{
                    LoginScreen(
                        openScreen = {
                            appState.navigate(it)
                        },
                        openAndPopUp = { open, popUp ->
                            appState.navigateAndPopUp(open, popUp)
                        }
                    )
                }

                composable<Register> {
                    RegistrationScreen(
                        openAndPopUp = {
                            appState.clearAndNavigate(it)
                        },
                        navigateUp = {
                            appState.popUp()
                        }
                    )
                }

                composable<Home> {
                    HomeScreen()
                }
            }
        }
    }
}

@Composable
fun rememberAppState(navController: NavHostController = rememberNavController()): ApplicationState {
    return remember(navController) {
        ApplicationState(navController)
    }
}