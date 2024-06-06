package com.example.chathub.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chathub.R
import com.example.chathub.common.BasicButton
import com.example.chathub.common.BasicField
import com.example.chathub.common.BasicToolBar
import com.example.chathub.common.EmailField
import com.example.chathub.common.PasswordField
import com.example.chathub.common.RepeatPasswordField
import com.example.chathub.ext.fieldModifier
import com.example.chathub.ui.theme.ChatHubTheme

@Composable
fun SignUpScreen() {
    SignUpScreenContent(
        onNameChange = { /*TODO*/ },
        onEmailChange = { /*TODO*/ },
        onPasswordChange = { /*TODO*/ },
        onRepeatPasswordChange = { /*TODO*/ },
        onCreateAccountClick = { /*TODO*/ },
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    )
}

@Composable
fun SignUpScreenContent(
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRepeatPasswordChange: (String) -> Unit,
    onCreateAccountClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val fieldModifier = Modifier.fieldModifier()

    BasicToolBar(title = R.string.signup, canNavigateBack = true)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = stringResource(id = R.string.signup_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.padding(20.dp))

        BasicField(
            text = R.string.enter_name,
            value = "",
            onNewValue = onNameChange,
            modifier = Modifier.fieldModifier(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
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
                imeAction = ImeAction.Next
            )
        )

        RepeatPasswordField(
            value = "",
            onNewValue = onRepeatPasswordChange,
            modifier = fieldModifier,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            )
        )

        BasicButton(
            text = R.string.create_account,
            action = onCreateAccountClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 40.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    ChatHubTheme {
        SignUpScreen()
    }
}

@Preview
@Composable
fun SignUpScreenDarkPreview() {
    ChatHubTheme(darkTheme = true) {
        SignUpScreen()
    }
}