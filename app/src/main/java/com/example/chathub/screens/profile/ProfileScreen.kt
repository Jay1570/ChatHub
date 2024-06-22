package com.example.chathub.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chathub.R
import com.example.chathub.common.BasicButton
import com.example.chathub.common.BasicToolBar
import com.example.chathub.ext.basicButton
import com.example.chathub.model.Profile
import com.example.chathub.screens.home.ProfileImage
import com.example.chathub.ui.theme.ChatHubTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navigateUp: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.onImageChange(it)
        }
    }

    Scaffold(
        topBar = {
            BasicToolBar(title = R.string.profile, canNavigateBack = true, navigateUp = navigateUp)
        }
    ) { innerPadding ->
        ProfileScreenContent(
            uiState = uiState,
            onNameChange = viewModel::onNameChange,
            onStatusMessageChange = viewModel::onStatusMessageChange,
            onImageClick = { imagePickerLauncher.launch("image/*") },
            onDoneClick = { viewModel.onDoneClick(navigateUp) },
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        )
    }
}

@Composable
fun ProfileScreenContent(
    uiState: ProfileUiState,
    onNameChange: (String) -> Unit,
    onStatusMessageChange: (String) -> Unit,
    onDoneClick: () -> Unit,
    onImageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        EditProfileImage(
            imageUrl = uiState.profile.imageUrl,
            onImageClick = onImageClick,
            enabled = !uiState.inProcess
        )
        Spacer(modifier = Modifier.height(24.dp))
        ProfileInfoItem(
            label = stringResource(R.string.enter_name),
            value = uiState.profile.name,
            onChange = onNameChange,
            imeAction = ImeAction.Next,
            enabled = !uiState.inProcess
        )
        Spacer(modifier = Modifier.height(16.dp))
        ProfileInfoItem(
            label = stringResource(id = R.string.about),
            value = uiState.profile.statusMessage,
            onChange = onStatusMessageChange,
            imeAction = ImeAction.Done,
            enabled = !uiState.inProcess
        )
        Spacer(modifier = Modifier.height(16.dp))
        ProfileInfoItem(
            label = stringResource(id = R.string.email),
            value = uiState.profile.email,
            enabled = false,
            trailingIcon = false,
            imeAction = ImeAction.Default
        )
        Spacer(modifier = Modifier.weight(1f))
        BasicButton(
            text = R.string.done,
            action = { onDoneClick() },
            modifier = Modifier.basicButton(),
            enabled = !uiState.inProcess
        )
    }
    if (uiState.inProcess) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
        ) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
fun ProfileInfoItem(
    label: String,
    value: String,
    imeAction: ImeAction,
    onChange: (String) -> Unit = {},
    enabled: Boolean = true,
    trailingIcon: Boolean = true
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = label,
            color = Color(0xFF888888),
            fontSize = 14.sp
        )
        TextField(
            value = value,
            enabled = enabled,
            onValueChange = { onChange(it) },
            trailingIcon = {
                if (trailingIcon) Icon(
                    painter = painterResource(id = R.drawable.edit),
                    contentDescription = null
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                disabledContainerColor = MaterialTheme.colorScheme.background,
                disabledTextColor = MaterialTheme.colorScheme.onBackground,
                disabledIndicatorColor = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                imeAction = imeAction
            )
        )
    }
}

@Composable
fun EditProfileImage(
    imageUrl: String,
    enabled: Boolean,
    onImageClick: () -> Unit
) {
    Box(contentAlignment = Alignment.BottomEnd) {
        ProfileImage(imageUrl = imageUrl, size = 120.dp)
        IconButton(
            onClick = onImageClick,
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFF25D366), shape = CircleShape),
            enabled = enabled
        ) {
            Icon(
                painter = painterResource(id = R.drawable.edit),
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ChatHubTheme {
        ProfileScreenContent(
            uiState = ProfileUiState(
                Profile(
                    imageUrl = "",
                    name = "Jay",
                    statusMessage = "Busy",
                    email = "jay@test.com"
                )
            ),
            onNameChange = {},
            onStatusMessageChange = {},
            onImageClick = {},
            onDoneClick = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}