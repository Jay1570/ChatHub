package com.example.chathub.screens

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chathub.R
import com.example.chathub.common.BasicButton
import com.example.chathub.common.BasicToolBar
import com.example.chathub.ext.basicButton
import com.example.chathub.model.Profile
import com.example.chathub.ui.theme.ChatHubTheme
import com.example.chathub.viewmodels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    openAndClear: (String) -> Unit,
    navigateUp: () -> Unit,
    openScreen: (String) -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {

    val lifecycleOwner = LocalLifecycleOwner.current
    val profile = viewModel.profile.collectAsStateWithLifecycle(Profile(), lifecycleOwner.lifecycle)

    Scaffold(
        topBar = { BasicToolBar(title = R.string.settings, canNavigateBack = true, navigateUp = navigateUp) }
    ) { innerPadding ->
        SettingsScreenContent(
            profile = profile.value,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            onSignOutClick = viewModel::signOut,
            onProfileClick = { viewModel.onProfileClick(openScreen) },
            openAndClear = openAndClear
        )
    }
}

@Composable
fun SettingsScreenContent(
    profile: Profile,
    modifier: Modifier = Modifier,
    onSignOutClick: (Context, (String) -> Unit) -> Unit,
    onProfileClick: () -> Unit,
    openAndClear: (String) -> Unit
) {
    val context = LocalContext.current
    Column(modifier = modifier){
        ProfileCard(profile = profile, onClick = onProfileClick)
        Spacer(modifier = Modifier.width(10.dp))
        HorizontalDivider(modifier = Modifier.padding(8.dp))
        Spacer(modifier = Modifier.width(10.dp))
        BasicButton(
            text = R.string.signout,
            action = {
                onSignOutClick(context, openAndClear)
            },
            modifier = Modifier.basicButton()
        )
    }
}

@Composable
fun ProfileCard(
    profile: Profile,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick
            )
            .padding(16.dp)
        ,
        verticalAlignment = Alignment.CenterVertically
    ){
        ProfileImage(imageUrl = profile.imageUrl, size = 70.dp)
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = profile.name, style = MaterialTheme.typography.bodyLarge)
            Text(text = profile.email, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    ChatHubTheme {
        SettingsScreenContent(
            profile = Profile(name = "Jay", email = "jay@gmail.com", statusMessage = "Busy"),
            modifier = Modifier
                .fillMaxSize(),
            onSignOutClick = {_,_ ->},
            openAndClear = {},
            onProfileClick = {}
        )
    }
}