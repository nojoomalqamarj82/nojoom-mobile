package com.nojoom.mobile.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Brand palette: deep navy + gold, mirrors Nojoom Manager desktop dashboard
val NojoomNavy = Color(0xFF0B1229)
val NojoomNavyLight = Color(0xFF162044)
val NojoomGold = Color(0xFFD4AF37)
val NojoomGoldLight = Color(0xFFF1D77E)
val NojoomEmerald = Color(0xFF0E4B3B)
val NojoomCosmicPurple = Color(0xFF2E1A47)
val NojoomSurface = Color(0xFF111832)
val NojoomTextPrimary = Color(0xFFF5F0E1)
val NojoomTextSecondary = Color(0xFFB8BCC8)
val NojoomDanger = Color(0xFFE05252)
val NojoomSuccess = Color(0xFF3FB27F)

private val NojoomColorScheme = darkColorScheme(
    primary = NojoomGold,
    onPrimary = NojoomNavy,
    secondary = NojoomGoldLight,
    background = NojoomNavy,
    onBackground = NojoomTextPrimary,
    surface = NojoomSurface,
    onSurface = NojoomTextPrimary,
    surfaceVariant = NojoomNavyLight,
    onSurfaceVariant = NojoomTextSecondary,
    error = NojoomDanger,
)

@Composable
fun NojoomMobileTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NojoomColorScheme,
        typography = NojoomTypography,
        content = content
    )
}
