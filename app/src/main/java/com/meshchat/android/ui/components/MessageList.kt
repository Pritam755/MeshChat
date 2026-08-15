package com.meshchat.android.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.meshchat.android.model.BitchatMessage
import com.meshchat.android.model.BitchatMessageType

private const val GROUP_INTERVAL_MS = 60_000L // 60 seconds

/**
 * Scrollable message list — bottom-anchored (newest at bottom).
 *
 * Consecutive messages from the same sender within [GROUP_INTERVAL_MS] are
 * visually grouped: no sender-name repetition and tighter 2dp spacing.
 *
 * The list auto-scrolls to the last item when a new message arrives.
 */
@Composable
fun MessageList(
    messages:   List<BitchatMessage>,
    myNickname: String,
    modifier:   Modifier = Modifier,
    listState:  LazyListState = rememberLazyListState(),
) {
    // Auto-scroll to bottom when messages are added
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    LazyColumn(
        state          = listState,
        modifier       = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        reverseLayout  = false,
    ) {
        itemsIndexed(
            items = messages,
            key   = { _, msg -> msg.id },
        ) { index, message ->
            val prevMessage = if (index > 0) messages[index - 1] else null

            val isOutgoing = message.sender.equals(myNickname, ignoreCase = true) ||
                             message.senderPeerID?.let { it == "me" } == true

            val prevSenderId = prevMessage?.senderPeerID ?: prevMessage?.sender
            val thisSenderId = message.senderPeerID ?: message.sender
            val prevTimestamp = prevMessage?.timestamp?.time ?: 0L

            val isGrouped = prevMessage != null &&
                prevSenderId == thisSenderId &&
                (message.timestamp.time - prevTimestamp) < GROUP_INTERVAL_MS

            val verticalSpacing = if (isGrouped) 2.dp else 8.dp

            // Date separator when day changes
            val prevDate = prevMessage?.timestamp?.let {
                java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.getDefault()).format(it)
            }
            val thisDate = java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.getDefault())
                .format(message.timestamp)

            if (prevDate != thisDate) {
                DateSeparator(date = message.timestamp)
                Spacer(Modifier.height(8.dp))
            } else {
                Spacer(Modifier.height(verticalSpacing))
            }

            MessageBubble(
                message    = message,
                isOutgoing = isOutgoing,
                isGrouped  = isGrouped,
                myNickname = myNickname,
                modifier   = Modifier.padding(
                    start = if (isOutgoing) 48.dp else 0.dp,
                    end   = if (isOutgoing) 0.dp  else 48.dp,
                ),
            )
        }
    }
}

// ── Date separator ────────────────────────────────────────────────────────────

@Composable
private fun DateSeparator(date: java.util.Date) {
    val label = remember(date) {
        val sdf = java.text.SimpleDateFormat("EEEE, d MMMM", java.util.Locale.getDefault())
        val today = java.util.Calendar.getInstance()
        val msgCal = java.util.Calendar.getInstance().apply { time = date }
        when {
            msgCal.get(java.util.Calendar.DATE) == today.get(java.util.Calendar.DATE) &&
            msgCal.get(java.util.Calendar.YEAR) == today.get(java.util.Calendar.YEAR) -> "Today"
            today.timeInMillis - date.time < 2 * 24 * 60 * 60 * 1000L -> "Yesterday"
            else -> sdf.format(date)
        }
    }
    androidx.compose.foundation.layout.Box(
        contentAlignment = androidx.compose.ui.Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
    ) {
        androidx.compose.material3.Text(
            text  = label,
            style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
