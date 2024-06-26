package com.example.chathub.common


import androidx.annotation.StringRes
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.chathub.R

@Composable
fun BasicField(
    @StringRes text: Int,
    value: String,
    onNewValue: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    enabled: Boolean = true
) {
    OutlinedTextField(
        singleLine = true,
        modifier = modifier,
        value = value,
        onValueChange = { onNewValue(it) },
        placeholder = { Text(stringResource(text)) },
        keyboardOptions = keyboardOptions,
        enabled = enabled
    )
}

@Composable
fun EmailField(
    value: String,
    onNewValue: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    enabled: Boolean = true
) {
    OutlinedTextField(
        singleLine = true,
        modifier = modifier,
        value = value,
        onValueChange = { onNewValue(it) },
        placeholder = { Text(stringResource(id = R.string.enter_email)) },
        leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "Email") },
        keyboardOptions = keyboardOptions,
        enabled = enabled
    )
}

@Composable
fun PasswordField(
    value: String,
    onNewValue: (String) -> Unit,
    modifier: Modifier = Modifier,
    @StringRes placeholder: Int = R.string.enter_password,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    enabled: Boolean = true
) {
    Password(
        value = value,
        placeholder = placeholder,
        onNewValue = onNewValue,
        modifier = modifier,
        keyboardOptions = keyboardOptions,
        enabled = enabled
    )
}

@Composable
fun RepeatPasswordField(
    value: String,
    onNewValue: (String) -> Unit,
    modifier: Modifier = Modifier,
    @StringRes placeholder: Int = R.string.confirm_password,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    enabled: Boolean = true
) {
    Password(
        value = value,
        placeholder = placeholder,
        onNewValue = onNewValue,
        modifier = modifier,
        keyboardOptions = keyboardOptions,
        enabled = enabled
    )
}

@Composable
private fun Password(
    value: String,
    @StringRes placeholder: Int,
    onNewValue: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    enabled: Boolean
) {
    var isVisible by remember { mutableStateOf(false) }

    val icon =
        if (isVisible) painterResource(id = R.drawable.ic_visibility_on)
        else painterResource(id = R.drawable.ic_visibility_off)

    val visualTransformation =
        if (isVisible) VisualTransformation.None else PasswordVisualTransformation()


    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = { onNewValue(it) },
        placeholder = { Text(text = stringResource(id = placeholder)) },
        leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Lock") },
        trailingIcon = {
            IconButton(onClick = { isVisible = !isVisible }) {
                Icon(painter = icon, contentDescription = "Visibility")
            }
        },
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        enabled = enabled
    )
}