@file:Suppress("DEPRECATION")

package com.example.chathub.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chathub.R
import com.example.chathub.common.BasicButton
import com.example.chathub.common.BasicTextButton
import com.example.chathub.common.BasicToolBar
import com.example.chathub.common.EmailField
import com.example.chathub.common.PasswordField
import com.example.chathub.ext.basicButton
import com.example.chathub.ext.fieldModifier
import com.example.chathub.ext.textButton
import com.example.chathub.navigation.DestinationScreen
import com.example.chathub.snackbar.SnackbarManager
import com.example.chathub.ui.theme.ChatHubTheme
import com.example.chathub.viewmodels.LoginUiState
import com.example.chathub.viewmodels.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    openScreen: (String) -> Unit,
    openAndPopUp: (String, String) -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState
    val context = LocalContext.current

    val googleSignInOptions = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.web_client_id))
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember {
        GoogleSignIn.getClient(context, googleSignInOptions)
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                viewModel.onGoogleLoginClick(account, openAndPopUp)
            } catch (e: ApiException) {
                SnackbarManager.showMessage(R.string.google_sign_in_failed)
            }
        } else {
            SnackbarManager.showMessage(R.string.google_sign_in_failed)
        }
   }
    fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
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
            onNoAccountClick = { openScreen(DestinationScreen.SignUp.route) },
            onForgotPasswordClick = viewModel::onForgotPasswordClick,
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
    onForgotPasswordClick: () -> Unit,
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

            BasicTextButton(
                text = R.string.forgot_password,
                action = onForgotPasswordClick,
                modifier = Modifier.textButton(),
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
            onNoAccountClick = {},
            onForgotPasswordClick = {}
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
            onNoAccountClick = {},
            onForgotPasswordClick = {}
        )
    }
}