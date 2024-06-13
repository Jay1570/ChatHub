package com.example.chathub.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chathub.R
import com.example.chathub.ext.formatTime
import com.example.chathub.model.Chat
import com.example.chathub.model.Profile
import com.example.chathub.ui.theme.ChatHubTheme
import com.example.chathub.viewmodels.ChatUiState
import com.example.chathub.viewmodels.ChatViewModel
import com.google.firebase.Timestamp


@Composable
fun ChatScreen(
    onNavigateBack: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.value
    val lifecycleOwner = LocalLifecycleOwner.current
    val chats = viewModel.chats.collectAsStateWithLifecycle(emptyList(), lifecycleOwner.lifecycle)
    val profile = viewModel.profile


    LaunchedEffect(chats.value) {
        viewModel.markMessagesAsRead()
    }

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
        .background(MaterialTheme.colorScheme.surface)
    ) {

        ChatMessages(
            chats = chats,
            modifier = Modifier
                .weight(1f),
            uiState = uiState
        )

        ChatInput(
            uiState = uiState,
            onValueChange = onValueChange,
            onSend = onSend
        )
    }
}
@Composable
fun ChatMessages(chats: List<Chat>,modifier: Modifier = Modifier, uiState: ChatUiState) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        reverseLayout = true
    ) {
        items(chats) { chat ->
            ChatMessageItem(chat, isFromMe = (uiState.currentUserId == chat.senderId))
        }
    }
}

@Composable
fun ChatMessageItem(chat: Chat, isFromMe: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = when(isFromMe) {
            true -> Alignment.End
            false -> Alignment.Start
        },
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 200.dp, min = 70.dp),
            shape = RoundedCornerShape(
                topStart = 48f,
                topEnd = 48f,
                bottomStart = if (isFromMe) 48f else 0f,
                bottomEnd = if (isFromMe) 0f else 48f
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isFromMe) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = if (isFromMe) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onTertiaryContainer,
            ),
        ) {
            Text(
                modifier = Modifier
                    .padding(8.dp),
                text = chat.message,
                color = if (isFromMe) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onTertiaryContainer,
            )
            Box(modifier = Modifier
                .align(Alignment.End),
            ){
                Row{
                    if (isFromMe) {
                        Image(
                            painter =
                            if (chat.read) {
                                painterResource(id = R.drawable.read)
                            } else {
                                painterResource(id = R.drawable.delivered)
                            },
                            contentDescription = "",
                            modifier = Modifier
                                .size(ButtonDefaults.IconSize),
                        )
                    }
                }
            }
        }
        Text(text = formatTime(chat.timestamp), fontSize = 10.sp)
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
        OutlinedTextField(
            value = uiState.message,
            minLines = 1,
            maxLines = 3,
            onValueChange = onValueChange,
            placeholder = { Text(text = "Message") },
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
                .align(Alignment.CenterVertically),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = if (!isSystemInDarkTheme()) Color.White else Color.DarkGray,
                unfocusedContainerColor = if (!isSystemInDarkTheme()) Color.White else Color.DarkGray,
            )

        )
        IconButton(
            onClick = onSend,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .size(OutlinedTextFieldDefaults.MinHeight),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
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
                Text(text = profile.name, modifier = Modifier.padding(), color = MaterialTheme.colorScheme.onPrimary)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
        navigationIcon = {
            IconButton(
                onClick = onNavigateBack,
                colors = IconButtonDefaults.filledIconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
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
            message = "What is your name?",
            read = false,
            senderId = "1223",
            timestamp = Timestamp.now()
        ),
        Chat(
            message = "I am fine",
            read = true,
            senderId = "1223",
            timestamp = Timestamp.now()
        ),
        Chat(
            message = "How are you?",
            read = true,
            senderId = "0",
            timestamp = Timestamp.now()
        ),
        Chat(
            message = "Hi",
            read = true,
            senderId = "0",
            timestamp = Timestamp.now()
        )
    )
    ChatHubTheme {
        ChatScreenContent(chats = chats, uiState = ChatUiState(currentUserId = "1223"), onValueChange = {}, onSend = {})
    }
}

@Preview
@Composable
fun ChatScreenDarkPreview() {
    val chats = listOf(
        Chat(
            message = "I am fine",
            read = true,
            senderId = "1223"
        ),
        Chat(
            message = "How are you?",
            read = true,
            senderId = "0"
        )
    )
    ChatHubTheme(darkTheme = true) {
        ChatScreenContent(chats = chats, uiState = ChatUiState(currentUserId = "1223"), onValueChange = {}, onSend = {})
    }
}

@Preview(showBackground = true)
@Composable
fun AppBarPreview() {
    ChatHubTheme {
        AppBar(onNavigateBack = {  }, profile = Profile(imageUrl = "", name = "Jay"))
    }
}