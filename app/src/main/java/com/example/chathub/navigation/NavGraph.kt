package com.example.chathub.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.chathub.ApplicationState
import com.example.chathub.screens.change_password.ChangePasswordScreen
import com.example.chathub.screens.chat.ChatScreen
import com.example.chathub.screens.home.HomeScreen
import com.example.chathub.screens.login.LoginScreen
import com.example.chathub.screens.profile.ProfileScreen
import com.example.chathub.screens.settings.SettingsScreen
import com.example.chathub.screens.sign_up.SignUpScreen

@Composable
fun Navigation(
    appState: ApplicationState,
    viewModel: NavigationViewModel = hiltViewModel()
) {
    val startDestination = viewModel.checkUserStatus()
    NavHost(
        navController = appState.navController,
        startDestination = startDestination
    ) {
        composable<Login> {
            LoginScreen(
                openScreen = { route -> appState.navigate(route) },
                openAndPopUp = { route,popUp -> appState.navigateAndPopUp(route,popUp) }
            )
        }

        composable<SignUp> {
            SignUpScreen(
                navigateUp = { appState.navController.navigateUp() },
                openScreen = { route -> appState.clearAndNavigate(route) }
            )
        }

        composable<Home> {
            HomeScreen(
                openScreen = { route -> appState.navigate(route) }
            )
        }

        composable<ChatRoute> {
            ChatScreen(
                onNavigateBack = { appState.navController.navigateUp() }
            )
        }

        composable<Settings> {
            SettingsScreen(
                openAndClear = { route -> appState.clearAndNavigate(route) },
                openScreen = { route -> appState.navigate(route) },
                navigateUp = { appState.navController.navigateUp() }
            )
        }

        composable<Profile> {
            ProfileScreen(
                navigateUp = { appState.popUp() }
            )
        }

        composable<ChangePassword> {
            ChangePasswordScreen(
                navigateUp = { appState.popUp() }
            )
        }
    }
}