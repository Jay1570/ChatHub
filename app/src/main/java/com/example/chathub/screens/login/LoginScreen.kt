package com.example.chathub.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chathub.R
import com.example.chathub.common.*
import com.example.chathub.ext.basicButton
import com.example.chathub.ext.fieldModifier
import com.example.chathub.navigation.Routes
import com.example.chathub.navigation.SignUp
import com.example.chathub.snackbar.SnackbarManager
import com.example.chathub.ui.theme.ChatHubTheme
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    openScreen: (Routes) -> Unit,
    openAndPopUp: (Routes, Routes) -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState
    val context = LocalContext.current
    val credentialManager = CredentialManager.create(context)
    val coroutineScope = rememberCoroutineScope()

    val googleIdOption: GetSignInWithGoogleOption = GetSignInWithGoogleOption.Builder(context.getString(R.string.web_client_id)).build()

    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    fun signInWithGoogle() {
        coroutineScope.launch {
            try {
                val result = credentialManager.getCredential(request = request, context = context)
                val credential = result.credential
                val googleIdCredential = GoogleIdTokenCredential.createFrom(credential.data)
                viewModel.onGoogleLoginClick(googleIdCredential, openAndPopUp)
            } catch (e: Exception) {
                SnackbarManager.showMessage(R.string.google_sign_in_failed)
            }
        }
    }

    Scaffold(
        topBar = {
            BasicToolBar(title = R.string.login_title, canNavigateBack = false)
        }
    ) { innerPadding ->
        LoginScreenContent(
            uiState = uiState,
            onEmailChange = viewModel::onEmailChange,
            onPasswordChange = viewModel::onPasswordChange,
            onGoogleLoginClick = { signInWithGoogle() },
            onLoginClick = { viewModel.onSignInClick(openAndPopUp) },
            onNoAccountClick = { openScreen(SignUp) },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}

@Composable
fun LoginScreenContent(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    onNoAccountClick: () -> Unit,
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

            OutlinedButton(
                onClick = onGoogleLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 20.dp),
                enabled = enabled
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.google_login),
                    contentDescription = stringResource(id = R.string.google_login),
                    modifier = Modifier
                        .wrapContentHeight()
                        .size(ButtonDefaults.IconSize)
                )
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(text = stringResource(id = R.string.google_login))
            }

            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
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
                    imeAction = ImeAction.Done
                ),
                enabled = enabled
            )

            BasicTextButton(
                text = R.string.no_account,
                action = onNoAccountClick,
                modifier = Modifier.basicButton(),
                enabled = enabled
            )

            BasicButton(
                text = R.string.login,
                action = onLoginClick,
                modifier = Modifier.basicButton(),
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

@Composable
fun Divider(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(color = DividerDefaults.color)
        )
        Text(
            text = "OR",
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .align(Alignment.CenterVertically)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(color = DividerDefaults.color)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    ChatHubTheme {
        LoginScreenContent(
            uiState = LoginUiState(),
            onEmailChange = {},
            onPasswordChange = {},
            onLoginClick = {},
            onGoogleLoginClick = {},
            onNoAccountClick = {}
        )
    }
}

@Preview
@Composable
fun LoginScreenDarkPreview() {
    ChatHubTheme(darkTheme = true) {
        LoginScreenContent(
            uiState = LoginUiState(),
            onEmailChange = {},
            onPasswordChange = {},
            onLoginClick = {},
            onGoogleLoginClick = {},
            onNoAccountClick = {}
        )
    }
}