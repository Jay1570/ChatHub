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
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.example.chathub.R
import com.example.chathub.ext.toolbarActions
import com.example.chathub.model.ChatList
import com.example.chathub.model.ChatUser
import com.example.chathub.ui.theme.ChatHubTheme
import com.example.chathub.viewmodels.ChatListUiState
import com.example.chathub.viewmodels.ChatListViewModel

@Composable
fun ChatListScreen(
    openScreen: (String) -> Unit,
    viewModel: ChatListViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val chatList = viewModel.chatList.collectAsStateWithLifecycle(emptyList(), lifecycleOwner.lifecycle)
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
                chatList = chatList.value,
                onChatClick = { },
                openScreen = openScreen,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            )
        } else {
            ChatListContent(
                uiState = uiState,
                userList = userList,
                onUserClick = viewModel::onUserClick,
                openScreen = openScreen,
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
    openScreen: (String) -> Unit,
    modifier: Modifier = Modifier,
    userList: List<ChatUser> = emptyList(),
    chatList: List<ChatList> = emptyList(),
    onChatClick: () -> Unit = {},
    onUserClick: (ChatUser, (String) -> Unit) -> Unit = {_,_ ->},
) {
    Box(modifier = modifier) {
        if(!uiState.isSearchBarVisible){
            LazyColumn {
                items(chatList) { chat ->
                    ChatListItem(
                        chat = if (chat.user1.userId != uiState.currentUserId) chat.user1 else chat.user2,
                        openScreen = openScreen
                    )
                }
            }
        } else {
            LazyColumn {
                items(userList) { chat ->
                    if (chat.userId != uiState.currentUserId) {
                        ChatListItem(
                            chat = chat,
                            onUserClick = onUserClick,
                            openScreen = openScreen
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatListItem(chat: ChatUser, openScreen: (String) -> Unit, onUserClick: (ChatUser, (String) -> Unit) -> Unit = {_,_ ->}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { onUserClick(chat, openScreen) })
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
        when (imageUrl == "") {
            true -> ImageRequest.Builder(LocalContext.current)
                .placeholder(R.drawable.user)
                .apply(block = fun ImageRequest.Builder.() {
                    transformations(CircleCropTransformation())
                })
                .build()
            false -> ImageRequest.Builder(LocalContext.current)
                .placeholder(R.drawable.user)
                .data(data = imageUrl)
                .apply(block = fun ImageRequest.Builder.() {
                    transformations(CircleCropTransformation())
                })
                .build()
        }
    )
    Image(
        painter = painter,
        contentDescription = "Profile Image",
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
    )
}

@Composable
fun SearchBar(
    uiState: ChatListUiState,
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit,
    onCloseClick: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    TextField(
        value = uiState.query,
        onValueChange = { onSearch(it) },
        placeholder = { Text(text = "Search") },
        trailingIcon = {
            IconButton(onClick = onCloseClick) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Search"
                )
            }
        },
        modifier = modifier
            .clip(RoundedCornerShape(50.dp))
            .focusRequester(focusRequester),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedContainerColor = Color.White,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        ),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        )
    )
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
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
                    uiState = uiState,
                    onSearch = onSearch,
                    onCloseClick = onSearchClick,
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
        ChatList(
            chatId = "",
            user2 =  ChatUser(
                name = "Jay",
                email = "jay@test.com",
                imageUrl = ""
            )
        ),
        ChatList(
            chatId = "",
            user2 =  ChatUser(
                name = "Jay",
                email = "jay@test.com",
                imageUrl = ""
            )
        )
    )
    ChatHubTheme {
        ChatListContent(
            chatList = chatList,
            uiState = ChatListUiState(),
            openScreen = {}
        )
    }
}

@Preview
@Composable
fun ChatListScreenDarkPreview() {
    val chatList = listOf(
        ChatList(
            chatId = "",
            user2 =  ChatUser(
                name = "Jay",
                email = "jay@test.com",
                imageUrl = ""
            )
        )
    )
    ChatHubTheme(darkTheme = true) {
        ChatListContent(
            chatList = chatList,
            uiState = ChatListUiState(),
            openScreen = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    ChatHubTheme {
        SearchBar(
            uiState = ChatListUiState(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            onSearch = {},
            onCloseClick = {},
        )
    }
}