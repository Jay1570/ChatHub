package com.example.chathub.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chathub.model.Chat
import com.example.chathub.model.Profile
import com.example.chathub.ui.theme.ChatHubTheme
import com.example.chathub.viewmodels.ChatUiState
import com.example.chathub.viewmodels.ChatViewModel


@Composable
fun ChatScreen(
    onNavigateBack: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.value
    val lifecycleOwner = LocalLifecycleOwner.current
    val chats = viewModel.chats.collectAsStateWithLifecycle(emptyList(), lifecycleOwner.lifecycle)
    val profile = viewModel.profile

    Scaffold(
        topBar = {
            AppBar(onNavigateBack = onNavigateBack, profile = profile)
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            ChatScreenContent(
                chats = chats.value,
                uiState = uiState,
                onValueChange = viewModel::onMessageChange,
                onSend = viewModel::sendMessage
            )
        }
    }
}

@Composable
fun ChatScreenContent(
    chats: List<Chat>,
    uiState: ChatUiState,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(modifier = modifier
        .fillMaxSize()
        .imePadding()
    ) {

        ChatMessages(chats = chats, modifier = Modifier
            .weight(1f))

        ChatInput(
            uiState = uiState,
            onValueChange = onValueChange,
            onSend = onSend
        )
    }
}
@Composable
fun ChatMessages(chats: List<Chat>,modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        reverseLayout = true
    ) {
        items(chats) { chat ->
            ChatMessageItem(chat)
        }
    }
}

@Composable
fun ChatMessageItem(chat: Chat) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = chat.message, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = chat.timestamp, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun ChatInput(
    uiState: ChatUiState,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = uiState.message,
            minLines = 1,
            onValueChange = onValueChange,
            placeholder = { Text(text = "Message") },
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 5.dp, end = 8.dp)
                .align(Alignment.CenterVertically),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.LightGray,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                unfocusedContainerColor = Color.LightGray,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedPlaceholderColor = Color.DarkGray,
                unfocusedPlaceholderColor = Color.DarkGray
            ),
            shape = RoundedCornerShape(100)
        )
        IconButton(
            onClick = onSend,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .size(TextFieldDefaults.MinHeight),
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    onNavigateBack: () -> Unit,
    profile: Profile
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                ProfileImage(imageUrl = profile.imageUrl, size = 50.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = profile.name, modifier = Modifier.padding())
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    val chats = listOf(
        Chat(
            message = "Hi",
            isRead = true
        ),
        Chat(
            message = "Hi",
            isRead = true
        ),
        Chat(
            message = "Hi",
            isRead = true
        )
    )
    ChatHubTheme {
        ChatScreenContent(chats = chats, uiState = ChatUiState(), onValueChange = {}, onSend = {})
    }
}

@Preview(showBackground = true)
@Composable
fun AppBarPreview() {
    ChatHubTheme {
        AppBar(onNavigateBack = {  }, profile = Profile(imageUrl = "", name = "Jay"))
    }
}