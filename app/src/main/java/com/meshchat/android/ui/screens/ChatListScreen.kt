package com.meshchat.android.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.meshchat.android.ui.ChatViewModel
import com.meshchat.android.ui.components.ConversationRow
import com.meshchat.android.ui.components.MeshChatTopBar
import com.meshchat.android.ui.components.SwipeActionsRow
import kotlinx.coroutines.launch

/**
 * Chat list (home) screen.
 *
 * Features:
 *  - Inline expandable search bar in top bar
 *  - LazyColumn of conversations, pinned first
 *  - SwipeToDismissBox per row (archive / delete)
 *  - FAB to start new chat / scan nearby peers
 *  - Empty state when no conversations
 */
@Composable
fun ChatListScreen(
    viewModel:           ChatViewModel,
    onConversationClick: (conversationId: String) -> Unit,
    onSettingsClick:     () -> Unit,
    modifier:            Modifier = Modifier,
) {
    val conversations by viewModel.conversations.collectAsState()
    val peerCount     by viewModel.peerCount.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Filter by search query (name or last message preview)
    val filtered = remember(conversations, searchQuery) {
        if (searchQuery.isBlank()) conversations
        else conversations.filter { c ->
            c.displayName.contains(searchQuery, ignoreCase = true) ||
            c.latestMessagePreview.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        modifier        = modifier,
        snackbarHost    = { SnackbarHost(snackbarHostState) },
        topBar          = {
            MeshChatTopBar(
                searchQuery       = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onSettingsClick   = onSettingsClick,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick            = {
                    scope.launch {
                        snackbarHostState.showSnackbar("Scanning for nearby peers…")
                    }
                },
                containerColor     = MaterialTheme.colorScheme.primary,
                contentColor       = MaterialTheme.colorScheme.onPrimary,
                modifier           = Modifier.navigationBarsPadding(),
            ) {
                Icon(
                    imageVector        = Icons.Default.PersonAdd,
                    contentDescription = "New chat / scan peers",
                )
            }
        },
        containerColor  = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        if (filtered.isEmpty()) {
            // ── Empty state ──────────────────────────────────────────────────
            Box(
                contentAlignment = Alignment.Center,
                modifier         = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(32.dp),
            ) {
                Text(
                    text      = if (searchQuery.isNotBlank()) "No conversations match \"$searchQuery\""
                                else if (peerCount == 0) "No peers nearby yet.\nMake sure Bluetooth is enabled."
                                else "No conversations yet.\nTap + to start one.",
                    style     = MaterialTheme.typography.bodyLarge,
                    color     = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            LazyColumn(
                modifier      = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(bottom = 80.dp), // space for FAB
            ) {
                items(
                    items = filtered,
                    key   = { it.conversationID },
                ) { conversation ->
                    SwipeActionsRow(
                        onArchive = {
                            scope.launch {
                                snackbarHostState.showSnackbar("Archived ${conversation.displayName}")
                            }
                        },
                        onDelete  = {
                            viewModel.deleteConversation(conversation.conversationID)
                        },
                    ) {
                        ConversationRow(
                            displayName    = conversation.displayName,
                            conversationId = conversation.conversationID,
                            lastMessage    = conversation.latestMessagePreview,
                            timestamp      = conversation.latestMessageAt,
                            unreadCount    = conversation.unreadCount,
                            isConnected    = conversation.isConnected,
                            isPinned       = conversation.isPinned,
                            isMuted        = conversation.isMuted,
                            onClick        = { onConversationClick(conversation.conversationID) },
                        )
                    }
                    Divider(
                        color     = MaterialTheme.colorScheme.outlineVariant,
                        thickness = 0.5.dp,
                        modifier  = Modifier.padding(start = 72.dp), // indent past avatar
                    )
                }
            }
        }
    }
}
