package com.meshchat.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ─── Palette ─────────────────────────────────────────────────────────────────

// Primary accent — indigo-violet (not Telegram #0088CC, not WhatsApp #25D366)
val Indigo500     = Color(0xFF6C5CE7)
val Indigo300     = Color(0xFFA29BFE)
val IndigoDeep    = Color(0xFF3D3480)
val IndigoLight   = Color(0xFF5A4BD1)

// Surfaces — deep navy dark theme
val Navy950       = Color(0xFF0A0A14)
val Navy900       = Color(0xFF0F0F1A)
val Navy800       = Color(0xFF1E1E2E)
val Navy700       = Color(0xFF2A2A42)
val Navy600       = Color(0xFF3A3A5C)

// Neutrals for light theme
val GhostWhite    = Color(0xFFFAFAFE)
val LavenderFaint = Color(0xFFF5F5FF)
val LavenderMid   = Color(0xFFEEEEF8)
val LavenderBorder= Color(0xFFC5C4D8)

// Status / accent shades
val ErrorRed      = Color(0xFFFF6B6B)
val ErrorRedLight = Color(0xFFD32F2F)
val SuccessGreen  = Color(0xFF00B894)
val MutedText     = Color(0xFF8888AA)

// ─── Color Schemes ───────────────────────────────────────────────────────────

val MeshChatDarkColorScheme = darkColorScheme(
    primary            = Indigo500,
    onPrimary          = Color.White,
    primaryContainer   = IndigoDeep,
    onPrimaryContainer = Indigo300,
    secondary          = Indigo300,
    onSecondary        = Navy900,
    secondaryContainer = Navy700,
    onSecondaryContainer = Indigo300,
    tertiary           = SuccessGreen,
    onTertiary         = Color.White,
    background         = Navy950,
    onBackground       = Color(0xFFE8E8F8),
    surface            = Navy900,
    onSurface          = Color(0xFFE8E8F8),
    surfaceVariant     = Navy800,
    onSurfaceVariant   = Color(0xFFB0B0CC),
    outline            = Navy600,
    outlineVariant     = Navy700,
    error              = ErrorRed,
    onError            = Color.White,
    errorContainer     = Color(0xFF4A1A1A),
    onErrorContainer   = ErrorRed,
    inverseSurface     = Color(0xFFE8E8F8),
    inverseOnSurface   = Navy900,
    inversePrimary     = IndigoLight,
    scrim              = Color(0xCC000000),
    surfaceTint        = Indigo500,
)

val MeshChatLightColorScheme = lightColorScheme(
    primary            = IndigoLight,
    onPrimary          = Color.White,
    primaryContainer   = Color(0xFFE8E4FF),
    onPrimaryContainer = IndigoDeep,
    secondary          = Indigo500,
    onSecondary        = Color.White,
    secondaryContainer = Color(0xFFEDE8FF),
    onSecondaryContainer = IndigoDeep,
    tertiary           = Color(0xFF00897B),
    onTertiary         = Color.White,
    background         = GhostWhite,
    onBackground       = Color(0xFF1A1A2E),
    surface            = LavenderFaint,
    onSurface          = Color(0xFF1A1A2E),
    surfaceVariant     = LavenderMid,
    onSurfaceVariant   = Color(0xFF44446A),
    outline            = LavenderBorder,
    outlineVariant     = Color(0xFFDDDDF0),
    error              = ErrorRedLight,
    onError            = Color.White,
    errorContainer     = Color(0xFFFFDAD6),
    onErrorContainer   = Color(0xFF410002),
    inverseSurface     = Navy800,
    inverseOnSurface   = GhostWhite,
    inversePrimary     = Indigo300,
    scrim              = Color(0x80000000),
    surfaceTint        = IndigoLight,
)

// ─── Extra semantic colors not in M3 ─────────────────────────────────────────

data class MeshChatExtendedColors(
    /** Sent-message bubble background */
    val sentBubble: Color,
    /** Received-message bubble background */
    val receivedBubble: Color,
    /** Muted/secondary text (timestamps, captions) */
    val mutedText: Color,
    /** Nostr transport badge tint */
    val nostrBadge: Color,
    /** Bluetooth transport badge tint */
    val btBadge: Color,
    /** Unread count badge */
    val unreadBadge: Color,
    /** Online / active indicator */
    val onlineIndicator: Color,
)

val DarkExtendedColors = MeshChatExtendedColors(
    sentBubble      = IndigoDeep,
    receivedBubble  = Navy800,
    mutedText       = MutedText,
    nostrBadge      = Color(0xFF6EC6FF),
    btBadge         = Indigo300,
    unreadBadge     = Indigo500,
    onlineIndicator = SuccessGreen,
)

val LightExtendedColors = MeshChatExtendedColors(
    sentBubble      = Color(0xFFE8E4FF),
    receivedBubble  = LavenderMid,
    mutedText       = Color(0xFF7777A0),
    nostrBadge      = Color(0xFF1565C0),
    btBadge         = IndigoLight,
    unreadBadge     = IndigoLight,
    onlineIndicator = Color(0xFF2E7D32),
)

val LocalMeshChatExtendedColors = staticCompositionLocalOf { DarkExtendedColors }

// ─── Theme Composable ─────────────────────────────────────────────────────────

@Composable
fun MeshChatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) MeshChatDarkColorScheme else MeshChatLightColorScheme
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    CompositionLocalProvider(LocalMeshChatExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = MeshChatTypography,
            shapes      = MeshChatShapes,
            content     = content,
        )
    }
}

/** Shortcut to access extended colors inside composables */
val MaterialTheme.extendedColors: MeshChatExtendedColors
    @Composable get() = LocalMeshChatExtendedColors.current
