package com.example.chathub_android.common

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.chathub_android.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicToolBar(
    @StringRes title: Int,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    CenterAlignedTopAppBar(
        title = { Text(stringResource(title), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary) },
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(
                    onClick = navigateUp,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back_button))

                }
            }
        },
        colors = toolbarColor()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun toolbarColor(): TopAppBarColors {
    return TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
}