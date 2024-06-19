package com.example.chathub.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.chathub.ApplicationState
import com.example.chathub.screens.change_password.ChangePasswordScreen
import com.example.chathub.screens.chat.ChatScreen
import com.example.chathub.screens.chat_list.ChatListScreen
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
    NavHost(navController = appState.navController, startDestination = startDestination) {

        composable(route = DestinationScreen.Login.route) {
            LoginScreen(
                openScreen = { route -> appState.navigate(route) },
                openAndPopUp = { route,popUp -> appState.navigateAndPopUp(route,popUp) }
            )
        }

        composable(route = DestinationScreen.SignUp.route) {
            SignUpScreen(
                navigateUp = { appState.navController.navigateUp() },
                openScreen = { route -> appState.clearAndNavigate(route) }
            )
        }

        composable(route = DestinationScreen.ChatList.route) {
            ChatListScreen(
                openScreen = { route -> appState.navigate(route) }
            )
        }

        composable(
            route = DestinationScreen.Chat.route,
            arguments = listOf(navArgument("chatId") {
                type = NavType.StringType
            })
        ){
            ChatScreen(
                onNavigateBack = { appState.navController.navigateUp() }
            )
        }

        composable(route = DestinationScreen.Settings.route) {
            SettingsScreen(
                openAndClear = { route -> appState.clearAndNavigate(route) },
                openScreen = { route -> appState.navigate(route) },
                navigateUp = { appState.navController.navigateUp() }
            )
        }

        composable(route = DestinationScreen.Profile.route) {
            ProfileScreen(
                navigateUp = { appState.navController.navigateUp() }
            )
        }

        composable(route = DestinationScreen.ChangePassword.route) {
            ChangePasswordScreen(
                navigateUp = { appState.navController.navigateUp() }
            )
        }
    }
}