package com.example.chathub.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.example.chathub.R
import com.example.chathub.ext.toolbarActions
import com.example.chathub.model.Chat
import com.example.chathub.model.ChatUser
import com.example.chathub.ui.theme.ChatHubTheme
import com.example.chathub.viewmodels.ChatListUiState
import com.example.chathub.viewmodels.ChatListViewModel

@Composable
fun ChatListScreen(
    openScreen: (String) -> Unit,
    viewModel: ChatListViewModel = hiltViewModel()
) {

    val chatList by viewModel.chatList.collectAsState()
    val userList by viewModel.userList.collectAsState()

    val uiState by viewModel.uiState
    Scaffold(
        topBar = {
            AppBar(
                uiState = uiState,
                onSearch = viewModel::onSearch,
                onSettingsClick = { /*TODO*/ },
                onSearchClick = viewModel::onSearchClick
            )
        }
    ) { innerPadding ->
        if(!uiState.isSearchBarVisible){
            ChatListContent(
                uiState = uiState,
                chatList = chatList,
                onClick = { },
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            )
        } else {
            ChatListContent(
                uiState = uiState,
                userList = userList,
                onClick = { },
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            )
        }
    }
}

@Composable
fun ChatListContent(
    uiState: ChatListUiState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    userList: List<ChatUser> = emptyList(),
    chatList: List<Chat> = emptyList()
) {
    Box(modifier = modifier) {
        if(!uiState.isSearchBarVisible){
            LazyColumn {
                items(chatList) { chat ->
                    ChatListItem(
                        chat = if (chat.user1.userId != uiState.currentUserId) chat.user1 else chat.user2,
                        onClick = onClick
                    )
                }
            }
        } else {
            LazyColumn {
                items(userList) { chat ->
                    if (chat.userId != uiState.currentUserId) {
                        ChatListItem(
                            chat = chat,
                            onClick = onClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatListItem(chat: ChatUser, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProfileImage(chat.imageUrl)
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = chat.name, style = MaterialTheme.typography.bodyLarge)
            Text(text = chat.email, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun ProfileImage(imageUrl: String) {
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current).data(data = imageUrl )
            .apply(block = fun ImageRequest.Builder.() {
                transformations(CircleCropTransformation())
            }).build()
    )
    Image(
        painter = painter,
        contentDescription = "Profile Image",
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    TextField(
        value = query,
        onValueChange = {
            query = it
            onSearch(query)
        },
        placeholder = { Text(text = "Search") },
        trailingIcon = {
            IconButton(onClick = { onSearch(query)  }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Search"
                )
            }
        },
        modifier = modifier.clip(RoundedCornerShape(50.dp)),
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(
    uiState: ChatListUiState,
    onSettingsClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSearch: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()){
        Column(
            Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(bottom = 5.dp)){
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                actions = {
                    Box(Modifier.toolbarActions()) {
                        IconButton(onClick = onSearchClick) {
                            Icon(
                                painter = painterResource(id = R.drawable.search),
                                contentDescription = "Search"
                            )
                        }
                    }
                    Box(Modifier.toolbarActions()) {
                        IconButton(onClick = onSettingsClick) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_settings),
                                contentDescription = "Settings"
                            )
                        }
                    }
                }
            )
            if (uiState.isSearchBarVisible) {
                SearchBar(
                    onSearch = onSearch,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatListScreenPreview() {
    val chatList = listOf(
        Chat(
            chatId = "",
            user2 =  ChatUser(
                name = "Jay",
                email = "jay@test.com",
                imageUrl = "https://github.com/Jay1570/ChatHub/blob/Jay1570-user-png/user.png"
            )
        ),
        Chat(
            chatId = "",
            user2 =  ChatUser(
                name = "Jay",
                email = "jay@test.com",
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/chathub-cc672.appspot.com/o/icons8-user-50.png?alt=media&token=7ae8302b-538b-41c6-8f58-fe25a90f35a4"
            )
        )
    )
    ChatHubTheme {
        ChatListContent(
            chatList = chatList,
            onClick = {},
            uiState = ChatListUiState()
        )
    }
}

@Preview
@Composable
fun ChatListScreenDarkPreview() {
    val chatList = listOf(
        Chat(
            chatId = "",
            user2 =  ChatUser(
                name = "Jay",
                email = "jay@test.com",
                imageUrl = "https://firebasestorage.googleapis.com/v0/b/chathub-cc672.appspot.com/o/icons8-user-50.png?alt=media&token=7ae8302b-538b-41c6-8f58-fe25a90f35a4"
            )
        )
    )
    ChatHubTheme(darkTheme = true) {
        ChatListContent(
            chatList = chatList,
            onClick = {},
            uiState = ChatListUiState()
        )
    }
}