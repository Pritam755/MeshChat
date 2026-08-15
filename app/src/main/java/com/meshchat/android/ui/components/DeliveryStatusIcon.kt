package com.meshchat.android.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meshchat.android.model.DeliveryStatus
import com.meshchat.android.ui.theme.extendedColors

/**
 * Shows the delivery/transport state at the bottom-right of a sent bubble:
 *   Sending  → hourglass
 *   Sent     → single circle
 *   Relayed  → relay arrows (distinct from delivered)
 *   Delivered→ double-tick
 *   Read     → double-tick (accent color)
 *   Failed   → ! in red
 *
 * Also shows a transport badge (BT mesh / Nostr globe) when [isRelayedViaNostr]
 * or [isRelayedViaMesh] flags are set — this is a MeshChat differentiator.
 */
@Composable
fun DeliveryStatusIcon(
    deliveryStatus:     DeliveryStatus?,
    isRelayedViaMesh:   Boolean,
    isRelayedViaNostr:  Boolean,
    isOutgoing:         Boolean,
    modifier:           Modifier = Modifier,
) {
    if (!isOutgoing) return

    Row(
        modifier             = modifier,
        horizontalArrangement = Arrangement.End,
        verticalAlignment    = Alignment.CenterVertically,
    ) {
        // ── Transport badge ────────────────────────────────────────────────
        TransportBadge(
            isNostr = isRelayedViaNostr,
            isMesh  = isRelayedViaMesh,
        )

        Spacer(Modifier.width(3.dp))

        // ── Delivery state icon ────────────────────────────────────────────
        when (deliveryStatus) {
            is DeliveryStatus.Sending -> Icon(
                imageVector        = Icons.Default.HourglassBottom,
                contentDescription = "Sending",
                tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier           = Modifier.size(12.dp),
            )
            is DeliveryStatus.Sent -> Icon(
                imageVector        = Icons.Default.RadioButtonUnchecked,
                contentDescription = "Sent",
                tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier           = Modifier.size(12.dp),
            )
            is DeliveryStatus.Delivered,
            is DeliveryStatus.PartiallyDelivered -> Icon(
                imageVector        = Icons.Default.DoneAll,
                contentDescription = "Delivered",
                tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier           = Modifier.size(12.dp),
            )
            is DeliveryStatus.Read -> Icon(
                imageVector        = Icons.Default.DoneAll,
                contentDescription = "Read",
                tint               = MaterialTheme.colorScheme.primary,
                modifier           = Modifier.size(12.dp),
            )
            is DeliveryStatus.Failed -> Text(
                text     = "!",
                fontSize = 12.sp,
                color    = MaterialTheme.colorScheme.error,
            )
            null -> {
                // Relay indicator (no explicit delivery status but relayed)
                if (isRelayedViaMesh) Icon(
                    imageVector        = Icons.Default.Share,
                    contentDescription = "Relayed via mesh",
                    tint               = MaterialTheme.extendedColors.btBadge,
                    modifier           = Modifier.size(11.dp),
                )
            }
        }
    }
}

// ─── Transport badge ──────────────────────────────────────────────────────────

@Composable
fun TransportBadge(
    isNostr:  Boolean,
    isMesh:   Boolean,
    modifier: Modifier = Modifier,
) {
    val (icon, tint, desc) = when {
        isNostr -> Triple(Icons.Default.Cloud,     MaterialTheme.extendedColors.nostrBadge, "Via Nostr")
        isMesh  -> Triple(Icons.Default.Bluetooth, MaterialTheme.extendedColors.btBadge,   "Via Bluetooth mesh")
        else    -> return
    }
    Icon(
        imageVector        = icon,
        contentDescription = desc,
        tint               = tint,
        modifier           = modifier.size(11.dp),
    )
}
