package com.meshchat.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meshchat.android.ui.theme.extendedColors
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Single row in the chat list.
 *
 * Layout:
 *   [Avatar 44dp]  [Name (bold 16sp)]        [Timestamp 12sp]
 *                  [Last message preview]     [Unread badge]
 *                  [Pin icon if pinned]
 */
@Composable
fun ConversationRow(
    displayName:   String,
    conversationId: String,
    lastMessage:   String,
    timestamp:     Long,
    unreadCount:   Int,
    isConnected:   Boolean,
    isPinned:      Boolean,
    isMuted:       Boolean,
    onClick:       () -> Unit,
    modifier:      Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // ── Avatar ────────────────────────────────────────────────────────────
        Box {
            InitialsAvatar(
                name = displayName,
                seed = conversationId,
                size = 44.dp,
            )
            // Online indicator dot
            if (isConnected) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.extendedColors.onlineIndicator)
                        .align(Alignment.BottomEnd),
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        // ── Text column ───────────────────────────────────────────────────────
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier            = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment   = Alignment.CenterVertically,
            ) {
                // Name + optional pin icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier          = Modifier.weight(1f, fill = false),
                ) {
                    if (isPinned) {
                        Icon(
                            imageVector        = Icons.Default.PushPin,
                            contentDescription = "Pinned",
                            tint               = MaterialTheme.colorScheme.primary,
                            modifier           = Modifier.size(13.dp),
                        )
                        Spacer(Modifier.width(3.dp))
                    }
                    Text(
                        text       = displayName,
                        style      = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 16.sp,
                        color      = MaterialTheme.colorScheme.onSurface,
                        maxLines   = 1,
                        overflow   = TextOverflow.Ellipsis,
                    )
                }

                // Timestamp
                Text(
                    text  = formatTimestamp(timestamp),
                    style = MaterialTheme.typography.labelLarge,
                    color = if (unreadCount > 0 && !isMuted)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.extendedColors.mutedText,
                    fontSize = 12.sp,
                )
            }

            Row(
                modifier            = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment   = Alignment.CenterVertically,
            ) {
                // Last message preview
                Text(
                    text     = lastMessage,
                    style    = MaterialTheme.typography.bodyMedium,
                    color    = MaterialTheme.extendedColors.mutedText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )

                // Unread badge
                if (unreadCount > 0) {
                    val badgeColor = if (isMuted)
                        MaterialTheme.colorScheme.outline
                    else
                        MaterialTheme.extendedColors.unreadBadge

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(badgeColor)
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    ) {
                        Text(
                            text     = if (unreadCount > 99) "99+" else unreadCount.toString(),
                            fontSize = 11.sp,
                            color    = Color.White,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}

// ─── Timestamp formatting ─────────────────────────────────────────────────────

private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
private val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
private val dayFormat  = SimpleDateFormat("EEE", Locale.getDefault())

private fun formatTimestamp(epochMs: Long): String {
    if (epochMs == 0L) return ""
    val date = Date(epochMs)
    val cal  = Calendar.getInstance().apply { time = date }
    val now  = Calendar.getInstance()

    return when {
        // Today → show time
        cal.get(Calendar.DATE) == now.get(Calendar.DATE) &&
        cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) ->
            timeFormat.format(date)

        // This week → show day name
        now.timeInMillis - epochMs < 7 * 24 * 60 * 60 * 1000L ->
            dayFormat.format(date)

        // Older → show date
        else -> dateFormat.format(date)
    }
}
