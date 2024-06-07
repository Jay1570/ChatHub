package com.example.chathub.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.chathub.ApplicationState
import com.example.chathub.screens.LoginScreen
import com.example.chathub.screens.SignUpScreen

@Composable
fun Navigation(
    appState: ApplicationState
) {
    NavHost(navController = appState.navController, startDestination = DestinationScreen.Login.route) {
        composable(route = DestinationScreen.Login.route) {
            LoginScreen(
                openScreen = { route -> appState.navigate(route) },
                openAndPopUp = { route,popUp -> appState.navigateAndPopUp(route,popUp) }
            )
        }
        composable(route = DestinationScreen.SignUp.route) {
            SignUpScreen(
                navigateUp = { appState.navController.navigateUp() },
                openScreen = { route -> appState.navigate(route) }
            )
        }
    }
}