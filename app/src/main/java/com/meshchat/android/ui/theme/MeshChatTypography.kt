package com.meshchat.android.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * MeshChat typography — Material3 type scale mapped to chat UI roles.
 * Uses system Roboto (FontFamily.Default) — no custom font download needed.
 */
val MeshChatTypography = Typography(
    // Large display — not used in chat UI
    displayLarge  = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Light,   fontSize = 57.sp, lineHeight = 64.sp, letterSpacing = (-0.25).sp),
    displayMedium = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Light,   fontSize = 45.sp, lineHeight = 52.sp),
    displaySmall  = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal,  fontSize = 36.sp, lineHeight = 44.sp),

    // Headlines — screen titles, top bar
    headlineLarge  = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 28.sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 24.sp),
    headlineSmall  = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 22.sp),

    // Titles — conversation name in top bar (16sp bold), section headers
    titleLarge  = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 22.sp),
    titleMedium = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium,   fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp),
    titleSmall  = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium,   fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.1.sp),

    // Body — chat list peer name (16sp bold), message content (14sp)
    bodyLarge  = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.25.sp),
    bodyMedium = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.25.sp),
    bodySmall  = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.4.sp),

    // Labels — timestamps (12sp), delivery status (11sp), badges
    labelLarge  = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium,  fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.1.sp),
    labelMedium = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium,  fontSize = 11.sp, lineHeight = 14.sp, letterSpacing = 0.5.sp),
    labelSmall  = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Medium,  fontSize = 10.sp, lineHeight = 14.sp, letterSpacing = 0.5.sp),
)
