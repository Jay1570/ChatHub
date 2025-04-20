package com.example.chathub_android.screens.entry

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chathub_android.R
import com.example.chathub_android.Routes
import com.example.chathub_android.screens.AppViewModelProvider

@Composable
fun EntryScreen(
    viewModel: EntryScreenViewModel = viewModel(factory = AppViewModelProvider.factory),
    navigateAndClearBackStack: (Routes) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.checkTokenWithApi(navigateAndClearBackStack)
    }

    Scaffold(modifier = Modifier.fillMaxSize().background(color = Color.White)) {
        Box(
            modifier = Modifier
                .fillMaxSize().background(color = Color.White),
            contentAlignment = Alignment.Center
        ) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.chat),
                contentDescription = "Logo"
            )
        }
    }
}