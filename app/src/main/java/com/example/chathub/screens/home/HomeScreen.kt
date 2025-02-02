package com.example.chathub.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.chathub.R
import com.example.chathub.navigation.Routes
import com.example.chathub.ext.toolbarActions
import com.example.chathub.model.ChatList
import com.example.chathub.model.Profile
import com.example.chathub.ui.theme.ChatHubTheme

@Composable
fun HomeScreen(
    openScreen: (Routes) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val chatList = viewModel.chatList.collectAsStateWithLifecycle(emptyList(), lifecycleOwner.lifecycle)
    val userList by viewModel.userList.collectAsStateWithLifecycle(emptyList(), lifecycleOwner.lifecycle)
    val profile by viewModel.profiles.collectAsStateWithLifecycle(emptyList(), lifecycleOwner.lifecycle)
    val unreadCount by viewModel.unreadMessageCounts.collectAsStateWithLifecycle(emptyMap(), lifecycleOwner.lifecycle)
    
    val uiState by viewModel.uiState
    Scaffold(
        topBar = {
            AppBar(
                uiState = uiState,
                onSearch = viewModel::onSearch,
                onSettingsClick = { viewModel.onSettingsClick(openScreen) },
                onSearchClick = viewModel::onSearchClick
            )
        }
    ) { innerPadding ->
        if(!uiState.isSearchBarVisible){
            HomeScreenContent(
                uiState = uiState,
                chatList = chatList.value,
                onChatClick = viewModel::onChatClick,
                profile = profile,
                unreadCount = unreadCount,
                openScreen = openScreen,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            )
        } else {
            HomeScreenContent(
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
fun HomeScreenContent(
    uiState: HomeUiState,
    openScreen: (Routes) -> Unit,
    modifier: Modifier = Modifier,
    userList: List<Profile> = emptyList(),
    chatList: List<ChatList> = emptyList(),
    onChatClick: (String, (Routes) -> Unit) -> Unit = { _, _ ->},
    unreadCount: Map<String,Int> = emptyMap(),
    profile: List<Profile> = emptyList(),
    onUserClick: (String, (Routes) -> Unit) -> Unit = { _, _ ->},
) {
    Box(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.surface)
        ) {
        if(!uiState.isSearchBarVisible){
            LazyColumn {
                items(chatList) { chat ->
                    val profiles =
                        if (chat.user1Id != uiState.currentUserId) profile.find { it.userId == chat.user1Id } ?: Profile()
                        else profile.find { it.userId == chat.user2Id } ?: Profile()
                    UserItem(
                        uiState = uiState,
                        chatId = chat.chatId,
                        unreadCount = unreadCount[chat.chatId] ?: 0,
                        onChatClick = onChatClick,
                        profile = profiles,
                        openScreen = openScreen
                    )
                }
            }
        } else {
            LazyColumn {
                items(userList) { profiles ->
                    if (profiles.userId != uiState.currentUserId) {
                        UserItem(
                            uiState = uiState,
                            profile = profiles,
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
fun UserItem(
    uiState: HomeUiState,
    chatId: String = "",
    profile: Profile,
    openScreen: (Routes) -> Unit,
    unreadCount: Int = 0,
    onUserClick: (String, (Routes) -> Unit) -> Unit = { _, _ ->},
    onChatClick: (String, (Routes) -> Unit) -> Unit = { _, _ ->},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    if (uiState.isSearchBarVisible) {
                        onUserClick(profile.userId, openScreen)
                    } else {
                        onChatClick(chatId, openScreen)
                    }
                }
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProfileImage(profile.imageUrl, size = 56.dp)
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = profile.name, style = MaterialTheme.typography.bodyLarge)
            Text(text = profile.email, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(modifier = Modifier.weight(1f))
        if (unreadCount > 0) {
            Text(
                text = unreadCount.toString(),
                color = Color.Black,
                modifier = Modifier
                    .background(color = Color.Green, shape = CircleShape)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun ProfileImage(imageUrl: String, size: Dp) {
    if (imageUrl != ""){
        AsyncImage(
            model = imageUrl,
            contentDescription = stringResource(id = R.string.profile_image),
            modifier = Modifier
                .size(size)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    } else {
        Image(
            painter = painterResource(id = R.drawable.user),
            contentDescription = stringResource(id = R.string.profile_image),
            modifier = Modifier
                .size(size)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun SearchBar(
    uiState: HomeUiState,
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit,
    onCloseClick: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    OutlinedTextField(
        value = uiState.query,
        singleLine = true,
        onValueChange = { onSearch(it) },
        placeholder = { Text(text = stringResource(id = R.string.search)) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        trailingIcon = {
            IconButton(onClick = onCloseClick) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(id = R.string.search)
                )
            }
        },
        shape = RoundedCornerShape(50),
        modifier = modifier
            .focusRequester(focusRequester)
            .padding(8.dp),
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
    uiState: HomeUiState,
    onSettingsClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSearch: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()){
        Column(
            Modifier
                .background(MaterialTheme.colorScheme.primary)
                .padding(bottom = 5.dp)){
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name), color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
                actions = {
                    Box(Modifier.toolbarActions()) {
                        IconButton(
                            onClick = onSearchClick,
                            colors = IconButtonDefaults.filledIconButtonColors(
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.search),
                                contentDescription = stringResource(id = R.string.search)
                            )
                        }
                    }
                    Box(Modifier.toolbarActions()) {
                        IconButton(
                            onClick = onSettingsClick,
                            colors = IconButtonDefaults.filledIconButtonColors(
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_settings),
                                contentDescription = stringResource(id = R.string.settings)
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
            chatId = "121",
            user1Id = "",
            user2Id = ""
        ),
        ChatList(
            chatId = "120",
            user1Id = "",
            user2Id = ""
        )
    )
    val unreadCount: Map<String, Int> = mapOf(
        "121" to 1,
        "120" to 0
    )
    ChatHubTheme {
        HomeScreenContent(
            chatList = chatList,
            uiState = HomeUiState(),
            openScreen = {},
            unreadCount = unreadCount
        )
    }
}

@Preview
@Composable
fun ChatListScreenDarkPreview() {
    val chatList = listOf(
        ChatList(
            chatId = "121",
            user1Id = "",
            user2Id = ""
        ),
        ChatList(
            chatId = "120",
            user1Id = "",
            user2Id = ""
        )
    )
    val unreadCount: Map<String, Int> = mapOf(
        "121" to 1,
        "120" to 0
    )
    ChatHubTheme(darkTheme = true) {
        HomeScreenContent(
            chatList = chatList,
            uiState = HomeUiState(),
            openScreen = {},
            unreadCount = unreadCount
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    ChatHubTheme {
        SearchBar(
            uiState = HomeUiState(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            onSearch = {},
            onCloseClick = {},
        )
    }
}