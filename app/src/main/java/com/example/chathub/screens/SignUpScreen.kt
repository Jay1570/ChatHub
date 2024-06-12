package com.example.chathub.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chathub.R
import com.example.chathub.common.BasicButton
import com.example.chathub.common.BasicField
import com.example.chathub.common.BasicToolBar
import com.example.chathub.common.EmailField
import com.example.chathub.common.PasswordField
import com.example.chathub.common.RepeatPasswordField
import com.example.chathub.ext.fieldModifier
import com.example.chathub.ui.theme.ChatHubTheme
import com.example.chathub.viewmodels.SignUpUiState
import com.example.chathub.viewmodels.SignUpViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    openScreen: (String) -> Unit,
    navigateUp: () -> Unit,
    viewModel: SignUpViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState

    Scaffold(
        topBar = {
            BasicToolBar(title = R.string.signup, canNavigateBack = true, navigateUp = navigateUp)
        }
    )
    { innerPadding ->
        SignUpScreenContent(
            uiState = uiState,
            onNameChange = viewModel::onNameChange,
            onEmailChange = viewModel::onEmailChange,
            onPasswordChange = viewModel::onPasswordChange,
            onRepeatPasswordChange = viewModel::onRepeatPasswordChange,
            onCreateAccountClick = { viewModel.onCreateAccountClick(openScreen) },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}

@Composable
fun SignUpScreenContent(
    uiState: SignUpUiState,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRepeatPasswordChange: (String) -> Unit,
    onCreateAccountClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val enabled = !uiState.inProcess
    val fieldModifier = Modifier.fieldModifier()

    Box(modifier = modifier){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .alpha(if (uiState.inProcess) 0.5f else 1f)
                .imePadding(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = stringResource(id = R.string.signup_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.padding(20.dp))

            BasicField(
                text = R.string.enter_name,
                value = uiState.name,
                onNewValue = onNameChange,
                modifier = Modifier.fieldModifier(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                enabled = enabled
            )

            EmailField(
                value = uiState.email,
                onNewValue = onEmailChange,
                modifier = fieldModifier,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                enabled = enabled
            )

            PasswordField(
                value = uiState.password,
                onNewValue = onPasswordChange,
                modifier = fieldModifier,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                enabled = enabled
            )

            RepeatPasswordField(
                value = uiState.repeatPassword,
                onNewValue = onRepeatPasswordChange,
                modifier = fieldModifier,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                enabled = enabled
            )

            BasicButton(
                text = R.string.create_account,
                action = onCreateAccountClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 40.dp),
                enabled = enabled
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
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    ChatHubTheme {
        SignUpScreenContent(
            uiState = SignUpUiState(),
            onNameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onRepeatPasswordChange = {},
            onCreateAccountClick = {}
        )
    }
}

@Preview
@Composable
fun SignUpScreenDarkPreview() {
    ChatHubTheme(darkTheme = true) {
        SignUpScreenContent(
            uiState = SignUpUiState(),
            onNameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onRepeatPasswordChange = {},
            onCreateAccountClick = {}
        )
    }
}