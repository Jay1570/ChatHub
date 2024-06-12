package com.example.chathub.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chathub.R
import com.example.chathub.common.BasicButton
import com.example.chathub.common.BasicToolBar
import com.example.chathub.viewmodels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    openAndPopUp: (String) -> Unit,
    navigateUp: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    Scaffold(
        topBar = { BasicToolBar(title = R.string.settings, canNavigateBack = true, navigateUp = navigateUp) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            BasicButton(text = R.string.signout, action = { viewModel.signOut(context, openAndPopUp) })
        }
    }
}