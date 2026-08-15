package com.meshchat.android.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Swipe-to-reveal row wrapper for chat list items.
 *
 * - Swipe **start-to-end** (right) → Archive (indigo)
 * - Swipe **end-to-start** (left)  → Delete  (red)
 *
 * The mute action is surfaced via a long-press sheet instead of a swipe
 * direction to avoid cluttering the 2-direction model.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeActionsRow(
    onArchive: () -> Unit,
    onDelete:  () -> Unit,
    modifier:  Modifier = Modifier,
    content:   @Composable RowScope.() -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.StartToEnd -> { onArchive(); false }
                SwipeToDismissBoxValue.EndToStart -> { onDelete();  false }
                SwipeToDismissBoxValue.Settled    -> false
            }
        },
        positionalThreshold = { totalDistance -> totalDistance * 0.35f },
    )

    SwipeToDismissBox(
        state            = dismissState,
        modifier         = modifier,
        backgroundContent = { SwipeBackground(dismissState) },
        content          = content,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeBackground(state: SwipeToDismissBoxState) {
    val direction = state.dismissDirection

    val (bgColor, icon, label, alignment) = when (direction) {
        SwipeToDismissBoxValue.StartToEnd -> SwipeAction(
            color     = MaterialTheme.colorScheme.primaryContainer,
            icon      = Icons.Default.Archive,
            label     = "Archive",
            alignment = Alignment.CenterStart,
        )
        SwipeToDismissBoxValue.EndToStart -> SwipeAction(
            color     = Color(0xFF8B1A1A),
            icon      = Icons.Default.Delete,
            label     = "Delete",
            alignment = Alignment.CenterEnd,
        )
        SwipeToDismissBoxValue.Settled -> SwipeAction(
            color     = Color.Transparent,
            icon      = Icons.Default.Archive,
            label     = "",
            alignment = Alignment.CenterStart,
        )
    }

    val animatedBg by animateColorAsState(
        targetValue   = bgColor,
        animationSpec = tween(150),
        label         = "swipeBg",
    )

    Box(
        modifier          = Modifier
            .fillMaxSize()
            .background(animatedBg)
            .padding(horizontal = 20.dp),
        contentAlignment  = alignment,
    ) {
        if (label.isNotEmpty()) {
            Row(
                verticalAlignment    = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    imageVector        = icon,
                    contentDescription = label,
                    tint               = Color.White,
                    modifier           = Modifier.size(22.dp),
                )
                Text(text = label, color = Color.White, fontSize = 13.sp)
            }
        }
    }
}

private data class SwipeAction(
    val color:     Color,
    val icon:      ImageVector,
    val label:     String,
    val alignment: Alignment,
)
