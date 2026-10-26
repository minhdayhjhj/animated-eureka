package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val OledEmeraldColorScheme = darkColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color(0xFF021F14),
    primaryContainer = EmeraldContainer,
    onPrimaryContainer = EmeraldBright,
    secondary = CyberCyan,
    onSecondary = Color(0xFF022026),
    secondaryContainer = CyberCyanContainer,
    onSecondaryContainer = CyberCyanBright,
    tertiary = AmberWarning,
    onTertiary = Color(0xFF241700),
    tertiaryContainer = AmberContainer,
    onTertiaryContainer = AmberWarning,
    error = CrimsonDanger,
    onError = Color.White,
    errorContainer = CrimsonContainer,
    onErrorContainer = CrimsonDanger,
    background = OledBlack,
    onBackground = TextPrimary,
    surface = OledSurface,
    onSurface = TextPrimary,
    surfaceVariant = OledSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = OledCardBorder
)

val AppShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Force OLED Dark Emerald palette for maximum battery saving as requested by user
    MaterialTheme(
        colorScheme = OledEmeraldColorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}
