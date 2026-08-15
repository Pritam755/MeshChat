package com.meshchat.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.absoluteValue

/**
 * Circular avatar generated from initials + a deterministic color derived
 * from [seed] (typically the peer ID or conversation ID).
 *
 * The same seed always produces the same hue, making avatars stable across
 * sessions without storing any image.
 */
@Composable
fun InitialsAvatar(
    name: String,
    seed: String,
    size: Dp = 44.dp,
    modifier: Modifier = Modifier,
) {
    val initials = remember(name) { name.toInitials() }
    val bgColor  = remember(seed) { seed.toAvatarColor() }
    val textSize = (size.value * 0.36f).sp

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(bgColor),
    ) {
        Text(
            text       = initials,
            fontSize   = textSize,
            fontWeight = FontWeight.SemiBold,
            color      = Color.White,
            maxLines   = 1,
        )
    }
}

// ─── Helpers ─────────────────────────────────────────────────────────────────

private fun String.toInitials(): String {
    val parts = trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
    return when {
        parts.size >= 2 -> "${parts[0].first().uppercaseChar()}${parts[1].first().uppercaseChar()}"
        parts.size == 1 -> parts[0].take(2).uppercase()
        else            -> "?"
    }
}

/**
 * Maps an arbitrary string seed to one of 12 curated hues.
 * Keeps saturation/brightness in a range that reads well over white text in
 * both dark and light mode.
 */
private val AVATAR_HUES = listOf(
    Color(0xFF6C5CE7), // indigo-violet  (primary accent family)
    Color(0xFF0984E3), // sky blue
    Color(0xFF00B894), // teal
    Color(0xFF00CEC9), // seafoam
    Color(0xFFE17055), // terracotta
    Color(0xFFD63031), // crimson
    Color(0xFFE84393), // magenta-pink
    Color(0xFF6D4C41), // brown
    Color(0xFF43A047), // forest green
    Color(0xFFFF7043), // deep orange
    Color(0xFF5C6BC0), // slate-indigo
    Color(0xFF8E44AD), // purple
)

private fun String.toAvatarColor(): Color {
    val hash = this.fold(0) { acc, c -> acc * 31 + c.code }
    return AVATAR_HUES[hash.absoluteValue % AVATAR_HUES.size]
}
