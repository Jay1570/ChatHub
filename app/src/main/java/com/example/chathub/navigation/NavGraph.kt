package com.example.chathub.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.chathub.ApplicationState
import com.example.chathub.screens.ChatListScreen
import com.example.chathub.screens.LoginScreen
import com.example.chathub.screens.SignUpScreen
import com.example.chathub.viewmodels.NavigationViewModel

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
            ChatListScreen()
        }
    }
}