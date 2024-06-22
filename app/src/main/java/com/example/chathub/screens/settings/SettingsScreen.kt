package com.example.chathub.screens.settings

import android.content.Context
import android.content.Intent
import android.os.Build
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.chathub.screens.home.ProfileImage
import com.example.chathub.ui.theme.ChatHubTheme

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
    val uiState by viewModel.uiState
    val dynamicColor: Boolean by viewModel.isDynamicColorEnabled.collectAsStateWithLifecycle(initialValue = false, lifecycleOwner.lifecycle)

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
            onThemeClick = viewModel::onThemeClick,
            isDynamicColorEnabled = dynamicColor,
            onDynamicColorsSwitchChange = viewModel::onDynamicColorSwitchChanged,
            openAndClear = openAndClear
        )
        if (uiState.isThemeDialogVisible) {
            ThemeSelectionDialog(
                currentTheme = uiState.currentTheme,
                onDismiss = viewModel::onDismissThemeDialog,
                onThemeSelected = viewModel::onThemeSelected
            )
        }
    }
}

@Composable
fun SettingsScreenContent(
    profile: Profile,
    modifier: Modifier = Modifier,
    onSignOutClick: (Context, (String) -> Unit) -> Unit,
    onProfileClick: () -> Unit,
    onAccountSecurityClick: () -> Unit,
    onThemeClick: () -> Unit,
    isDynamicColorEnabled: Boolean,
    onDynamicColorsSwitchChange: (Boolean) -> Unit,
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
        Spacer(modifier = Modifier.width(10.dp))
        ThemeSelectionCard(onClick = onThemeClick)
        Spacer(modifier = Modifier.width(10.dp))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            DynamicColorSwitch(
                isDynamicColorEnabled = isDynamicColorEnabled,
                onSwitchChanged = onDynamicColorsSwitchChange
            )
            Spacer(modifier = Modifier.width(10.dp))
        }
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

@Composable
fun ThemeSelectionCard(onClick:() -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(painter = painterResource(id = R.drawable.theme), contentDescription = null, modifier = Modifier.size(30.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = stringResource(id = R.string.theme), style = MaterialTheme.typography.bodyLarge)
            Text(text = stringResource(id = R.string.change_theme), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun ThemeSelectionDialog(
    currentTheme: Theme,
    onDismiss: () -> Unit,
    onThemeSelected: (Theme) -> Unit
) {

    val options = listOf(Theme.LIGHT, Theme.DARK, Theme.SYSTEM_DEFAULT)
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(currentTheme) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.select_theme)) },
        text = {
            Column {
                options.forEach { theme ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOptionSelected(theme) }
                    ) {
                        RadioButton(
                            selected = (theme == selectedOption),
                            onClick = { onOptionSelected(theme) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(id = theme.toTextResId()))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onThemeSelected(selectedOption) }) {
                Text(text = stringResource(id = R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.cancel))
            }
        }
    )
}

@Composable
fun DynamicColorSwitch(
    isDynamicColorEnabled: Boolean,
    onSwitchChanged: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSwitchChanged(!isDynamicColorEnabled) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(painter = painterResource(id = R.drawable.dynamic_color), contentDescription = null, modifier = Modifier.size(30.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = stringResource(id = R.string.dynamic_color), style = MaterialTheme.typography.bodyLarge)
            Text(text = stringResource(id = R.string.dynamic_color_description), style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = isDynamicColorEnabled,
            onCheckedChange = { onSwitchChanged(it) }
        )
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
            onProfileClick = {},
            onAccountSecurityClick = {},
            onThemeClick = {},
            isDynamicColorEnabled = false,
            onDynamicColorsSwitchChange = {}
        )
    }
}