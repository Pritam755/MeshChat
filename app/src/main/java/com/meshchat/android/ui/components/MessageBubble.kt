package com.meshchat.android.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meshchat.android.model.BitchatMessage
import com.meshchat.android.model.DeliveryStatus
import com.meshchat.android.ui.theme.ShapeReceivedBubble
import com.meshchat.android.ui.theme.ShapeReceivedBubbleGrouped
import com.meshchat.android.ui.theme.ShapeSentBubble
import com.meshchat.android.ui.theme.ShapeSentBubbleGrouped
import com.meshchat.android.ui.theme.extendedColors
import java.text.SimpleDateFormat
import java.util.Locale

private val bubbleTimeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

/**
 * Individual message bubble.
 *
 * - **Sent** (isOutgoing=true): right-aligned, accent-filled, tail on bottom-right
 * - **Received** (isOutgoing=false): left-aligned, surface-filled, tail on bottom-left
 *
 * Grouping: when [isGrouped] is true (consecutive sender within 60s), the tail
 * shape is suppressed for a cleaner stacked look.
 *
 * Footer shows timestamp + delivery status + transport badge.
 */
@Composable
fun MessageBubble(
    message:   BitchatMessage,
    isOutgoing: Boolean,
    isGrouped:  Boolean,
    myNickname: String,
    modifier:   Modifier = Modifier,
) {
    val isNostr = message.senderNostrPubkey != null ||
        message.senderPeerID?.startsWith("nostr") == true

    val shape: RoundedCornerShape = when {
        isOutgoing && isGrouped  -> ShapeSentBubbleGrouped
        isOutgoing               -> ShapeSentBubble
        isGrouped                -> ShapeReceivedBubbleGrouped
        else                     -> ShapeReceivedBubble
    }

    val bubbleColor = if (isOutgoing)
        MaterialTheme.extendedColors.sentBubble
    else
        MaterialTheme.extendedColors.receivedBubble

    val textColor = if (isOutgoing)
        MaterialTheme.colorScheme.onPrimary
    else
        MaterialTheme.colorScheme.onSurface

    val timeText = remember(message.timestamp) { bubbleTimeFormat.format(message.timestamp) }

    Row(
        modifier            = modifier.fillMaxWidth(),
        horizontalArrangement = if (isOutgoing) Arrangement.End else Arrangement.Start,
    ) {
        // Received: show sender name label for the first message in a group
        Column(
            horizontalAlignment = if (isOutgoing) Alignment.End else Alignment.Start,
        ) {
            if (!isOutgoing && !isGrouped && message.sender.isNotBlank()) {
                Text(
                    text      = message.sender,
                    fontSize  = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color     = MaterialTheme.colorScheme.primary,
                    modifier  = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                )
            }

            Surface(
                shape  = shape,
                color  = bubbleColor,
                modifier = Modifier.widthIn(min = 72.dp, max = 280.dp),
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    // Message text
                    Text(
                        text  = message.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor,
                        lineHeight = 20.sp,
                    )

                    Spacer(Modifier.height(4.dp))

                    // Footer: time + delivery/transport
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment     = Alignment.CenterVertically,
                        modifier              = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text     = timeText,
                            fontSize = 11.sp,
                            color    = textColor.copy(alpha = 0.6f),
                        )

                        DeliveryStatusIcon(
                            deliveryStatus    = message.deliveryStatus,
                            isRelayedViaMesh  = message.isRelay && !isNostr,
                            isRelayedViaNostr = isNostr,
                            isOutgoing        = isOutgoing,
                            modifier          = Modifier.padding(start = 4.dp),
                        )
                    }
                }
            }
        }
    }
}
