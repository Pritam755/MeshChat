package com.meshchat.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meshchat.android.ui.DirectMessageTransport
import com.meshchat.android.ui.theme.extendedColors

/**
 * Top bar for the Conversation screen.
 *
 * Displays:
 *  - Back button
 *  - Initials avatar (40dp) — triple-tap fires [onAvatarTripleTap] (panic wipe)
 *  - Peer name + mesh status chip (BT/Nostr icon + peer count text)
 *  - Overflow menu icon
 */
@Composable
fun ConversationTopBar(
    peerName:            String,
    conversationId:      String,
    meshStatusText:      String,
    transport:           DirectMessageTransport,
    onBack:              () -> Unit,
    onAvatarTripleTap:   () -> Unit,
    onMoreOptions:       () -> Unit,
    modifier:            Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
            .height(64.dp),
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // ── Back ──────────────────────────────────────────────────────────
            IconButton(onClick = onBack) {
                Icon(
                    imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint               = MaterialTheme.colorScheme.onSurface,
                )
            }

            // ── Avatar (triple-tap = panic wipe) ──────────────────────────────
            TripleTapAvatar(
                name        = peerName,
                seed        = conversationId,
                onTripleTap = onAvatarTripleTap,
            )

            Spacer(Modifier.width(10.dp))

            // ── Name + status ─────────────────────────────────────────────────
            Column(
                modifier            = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text     = peerName,
                    style    = MaterialTheme.typography.titleLarge,
                    color    = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                MeshStatusRow(meshStatusText = meshStatusText, transport = transport)
            }

            // ── Overflow menu ─────────────────────────────────────────────────
            IconButton(onClick = onMoreOptions) {
                Icon(
                    imageVector        = Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

// ── Mesh status row ───────────────────────────────────────────────────────────

@Composable
private fun MeshStatusRow(
    meshStatusText: String,
    transport:      DirectMessageTransport,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        val icon = when (transport) {
            DirectMessageTransport.NOSTR -> Icons.Default.Cloud
            DirectMessageTransport.MESH  -> Icons.Default.Bluetooth
        }
        val tint = when (transport) {
            DirectMessageTransport.NOSTR -> MaterialTheme.extendedColors.nostrBadge
            DirectMessageTransport.MESH  -> MaterialTheme.extendedColors.btBadge
        }
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = tint,
            modifier           = Modifier.size(12.dp),
        )
        Spacer(Modifier.width(3.dp))
        Text(
            text     = meshStatusText,
            style    = MaterialTheme.typography.labelMedium,
            color    = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
        )
    }
}

// ── Triple-tap avatar ─────────────────────────────────────────────────────────

@Composable
private fun TripleTapAvatar(
    name:        String,
    seed:        String,
    onTripleTap: () -> Unit,
) {
    val tapCount    = remember { mutableIntStateOf(0) }
    val lastTapTime = remember { mutableLongStateOf(0L) }

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier.clickable(
            interactionSource = interactionSource,
            indication        = null,
        ) {
            val now = System.currentTimeMillis()
            if (now - lastTapTime.longValue < 600L) {
                tapCount.intValue++
                if (tapCount.intValue >= 3) {
                    tapCount.intValue  = 0
                    lastTapTime.longValue = 0L
                    onTripleTap()
                }
            } else {
                tapCount.intValue = 1
            }
            lastTapTime.longValue = now
        }
    ) {
        InitialsAvatar(name = name, seed = seed, size = 40.dp)
    }
}
