package com.example.chathub.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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

    val context = LocalContext.current
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
            onAccountSecurityClick = { viewModel.onAccountSecurityClick(context, openScreen) },
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
    onAccountSecurityClick: () -> Unit,
    openAndClear: (String) -> Unit
) {
    val context = LocalContext.current
    Column(modifier = modifier){
        ProfileCard(profile = profile, onClick = onProfileClick)
        Spacer(modifier = Modifier.width(10.dp))
        HorizontalDivider(modifier = Modifier.padding(8.dp))
        Spacer(modifier = Modifier.width(10.dp))
        AccountSecurityCard(onClick = onAccountSecurityClick)
        Spacer(modifier = Modifier.width(10.dp))
        InviteAFriend()
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
fun AccountSecurityCard(
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
        Icon(painter = painterResource(id = R.drawable.security), contentDescription = null, modifier = Modifier.size(30.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = stringResource(id = R.string.account_security), style = MaterialTheme.typography.bodyLarge)
            Text(text = stringResource(id = R.string.change_password), style = MaterialTheme.typography.bodyMedium)
        }
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
            .padding(16.dp),
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

@Composable
fun InviteAFriend() {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = { shareInvite(context) }
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Icon(imageVector = Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(30.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = stringResource(id = R.string.invite_friends), style = MaterialTheme.typography.bodyLarge)
            Text(text = stringResource(id = R.string.share_app), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

private fun shareInvite(context: Context) {
    val downloadLink = "https://github.com/Jay1570/ChatHub/"
    val invitationMessage = context.getString(R.string.invitation_message, downloadLink)
    val intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, invitationMessage)
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(intent, context.getString(R.string.invite_friends)))
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
            onProfileClick = {},
            onAccountSecurityClick = {}
        )
    }
}