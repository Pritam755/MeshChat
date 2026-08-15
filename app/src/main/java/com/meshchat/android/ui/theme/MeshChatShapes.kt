package com.meshchat.android.ui.theme

import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape

/**
 * MeshChat shape system — 8dp base grid.
 *
 * Material3 shape roles mapped to our UI:
 *  extraSmall  → transport/status badges, chips
 *  small       → input field corners, small buttons
 *  medium      → message bubbles (overridden per-bubble for tails)
 *  large       → bottom sheets, dialogs
 *  extraLarge  → full-screen bottom sheets
 */
val MeshChatShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small      = RoundedCornerShape(8.dp),
    medium     = RoundedCornerShape(18.dp),   // base bubble radius
    large      = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
)

// ─── Named shape constants for direct use in composables ─────────────────────

/** Full pill — avatars, badges, FAB */
val ShapePill = RoundedCornerShape(50)

/** Chat input bar */
val ShapeInput = RoundedCornerShape(24.dp)

/** Sent message bubble — tail at bottom-right → flatten that corner */
val ShapeSentBubble = RoundedCornerShape(
    topStart    = 18.dp,
    topEnd      = 18.dp,
    bottomStart = 18.dp,
    bottomEnd   = 4.dp,   // tail side
)

/** Received message bubble — tail at bottom-left */
val ShapeReceivedBubble = RoundedCornerShape(
    topStart    = 18.dp,
    topEnd      = 18.dp,
    bottomStart = 4.dp,   // tail side
    bottomEnd   = 18.dp,
)

/** Sent bubble when grouped (no tail — middle or top of group) */
val ShapeSentBubbleGrouped = RoundedCornerShape(18.dp)

/** Received bubble when grouped */
val ShapeReceivedBubbleGrouped = RoundedCornerShape(18.dp)

/** Conversation list row avatar */
val ShapeAvatar = ShapePill

/** Swipe action reveal button */
val ShapeSwipeAction = RoundedCornerShape(12.dp)
