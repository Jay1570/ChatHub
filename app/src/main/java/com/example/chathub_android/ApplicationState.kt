package com.example.chathub_android

import androidx.compose.runtime.Stable
import androidx.navigation.NavHostController

@Stable
class ApplicationState(
    val navController: NavHostController,
) {
    fun popUp() {
        navController.popBackStack()
    }
    fun navigate(route: Routes) {
        navController.navigate(route) { launchSingleTop = true }
    }

    fun navigateAndPopUp(route: Routes, popUp: Routes) {
        navController.navigate(route) {
            launchSingleTop = true
            popUpTo(popUp) { inclusive = true }
        }
    }

    fun clearAndNavigate(route: Routes) {
        navController.navigate(route) {
            launchSingleTop = true
            popUpTo(0) { inclusive = true }
        }
    }
}