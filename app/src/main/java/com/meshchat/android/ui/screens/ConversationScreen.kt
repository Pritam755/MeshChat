package com.meshchat.android.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.meshchat.android.ui.ChatViewModel
import com.meshchat.android.ui.DirectMessageTransport
import com.meshchat.android.ui.components.ConversationTopBar
import com.meshchat.android.ui.components.MessageInputBar
import com.meshchat.android.ui.components.MessageList

/**
 * Full-screen conversation view.
 *
 * - [ConversationTopBar]: back, avatar (triple-tap = panic wipe), name, mesh status
 * - [MessageList]: bottom-anchored scrollable bubbles with grouping + date separators
 * - [MessageInputBar]: attachment, expanding text field, send/mic button
 *
 * This composable is stateless — all state is derived from [viewModel] via
 * collectAsState(). The panic-wipe triple-tap delegates to the existing
 * ViewModel method without any new logic.
 */
@Composable
fun ConversationScreen(
    conversationId: String,
    viewModel:      ChatViewModel,
    onBack:         () -> Unit,
    modifier:       Modifier = Modifier,
) {
    // ── State from ViewModel ──────────────────────────────────────────────────
    val privateChats      by viewModel.privateChats.collectAsState()
    val nickname          by viewModel.nickname.collectAsState()
    val connectedPeers    by viewModel.connectedPeers.collectAsState()
    val meshStatusText    by viewModel.meshStatusText.collectAsState()
    val conversations     by viewModel.conversations.collectAsState()

    // Determine transport for this conversation
    val summary = remember(conversations, conversationId) {
        conversations.firstOrNull { it.conversationID == conversationId }
    }
    val transport = summary?.transport ?: DirectMessageTransport.MESH
    val displayName = summary?.displayName ?: conversationId.take(12)

    // Messages for this conversation
    val messages = remember(privateChats, conversationId) {
        privateChats[conversationId]
            ?: privateChats.entries
                .firstOrNull { it.key.equals(conversationId, ignoreCase = true) }
                ?.value
            ?: emptyList()
    }

    // Input bar state
    var inputText by remember { mutableStateOf("") }

    // ── Mark as read on open ──────────────────────────────────────────────────
    androidx.compose.runtime.LaunchedEffect(conversationId) {
        viewModel.markConversationRead(conversationId)
        viewModel.selectPrivateChat(conversationId)
    }

    // ── UI ────────────────────────────────────────────────────────────────────
    Scaffold(
        modifier       = modifier.imePadding(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ConversationTopBar(
                peerName          = displayName,
                conversationId    = conversationId,
                meshStatusText    = meshStatusText,
                transport         = transport,
                onBack            = {
                    viewModel.clearPrivateChatSelection()
                    onBack()
                },
                onAvatarTripleTap = { viewModel.handlePanicWipe() },
                onMoreOptions     = { /* future: show bottom sheet */ },
            )
        },
        bottomBar = {
            Column {
                Divider(
                    color     = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 0.5.dp,
                )
                MessageInputBar(
                    inputText     = inputText,
                    onInputChange = { inputText = it },
                    onSendClick   = {
                        val text = inputText.trim()
                        if (text.isNotEmpty()) {
                            viewModel.sendPrivateMessage(
                                recipientPeerIDOrAlias = conversationId,
                                message                = text,
                            )
                            inputText = ""
                        }
                    },
                    onAttachClick = { /* future: launch media picker */ },
                    onMicClick    = { /* future: start voice note */ },
                )
            }
        },
    ) { innerPadding ->
        MessageList(
            messages   = messages,
            myNickname = nickname,
            modifier   = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        )
    }
}
