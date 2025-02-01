package com.example.chathub

import android.content.res.Resources
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Stable
import androidx.navigation.NavHostController
import com.example.chathub.navigation.Routes
import com.example.chathub.snackbar.SnackbarManager
import com.example.chathub.snackbar.SnackbarMessage.Companion.toMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

@Stable
class ApplicationState(
    val snackbarState: SnackbarHostState,
    val navController: NavHostController,
    private val snackbarManager: SnackbarManager,
    private val resources: Resources,
    coroutineScope: CoroutineScope
) {
    init {
        coroutineScope.launch {
            snackbarManager.snackbarMessages.filterNotNull().collect {snackbarMessage ->
                val text = snackbarMessage.toMessage(resources)
                snackbarState.showSnackbar(text)
                snackbarManager.clearSnackbarState()
            }
        }
    }
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