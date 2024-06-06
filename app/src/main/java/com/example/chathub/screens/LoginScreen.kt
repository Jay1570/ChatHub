package com.example.chathub.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chathub.R
import com.example.chathub.common.BasicButton
import com.example.chathub.common.BasicTextButton
import com.example.chathub.common.BasicToolBar
import com.example.chathub.common.EmailField
import com.example.chathub.common.PasswordField
import com.example.chathub.ext.basicButton
import com.example.chathub.ext.fieldModifier
import com.example.chathub.ext.textButton
import com.example.chathub.ui.theme.ChatHubTheme

@Composable
fun LoginScreen() {
    LoginScreenContent(
        onEmailChange = { /* TODO */ },
        onPasswordChange = { /* TODO */ },
        onGoogleLoginClick = { /* TODO */ },
        onLoginClick = { /*TODO*/ },
        onNoAccountCLick = { /* TODO */ },
        onForgotPasswordClick = { /* TODO */ },
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    )
}

@Composable
fun LoginScreenContent(
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    onNoAccountCLick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val fieldModifier = Modifier.fieldModifier()

    BasicToolBar(title = R.string.login_title, canNavigateBack = false)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OutlinedButton(
            onClick = onGoogleLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 20.dp),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.google_login),
                contentDescription = "Google Login",
                modifier = Modifier
                    .wrapContentHeight()
                    .size(ButtonDefaults.IconSize)
            )
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = "Login With Google")
        }

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        )

        EmailField(
            value = "",
            onNewValue = onEmailChange,
            modifier = fieldModifier,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )

        PasswordField(
            value = "",
            onNewValue = onPasswordChange,
            modifier = fieldModifier,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            )
        )

        BasicTextButton(
            text = R.string.no_account,
            action = onNoAccountCLick,
            modifier = Modifier.basicButton()
        )

        BasicButton(
            text = R.string.login,
            action = onLoginClick,
            modifier = Modifier.basicButton()
        )

        BasicTextButton(
            text = R.string.forgot_password,
            action = onForgotPasswordClick,
            modifier = Modifier.textButton()
        )
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
        Box( // Right divider
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
        LoginScreen()
    }
}

@Preview
@Composable
fun LoginScreenDarkPreview() {
    ChatHubTheme(darkTheme = true) {
        LoginScreen()
    }
}