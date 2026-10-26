package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = NeoYellow,
    onPrimary = NeoBlack,
    primaryContainer = NeoYellow,
    onPrimaryContainer = NeoBlack,
    secondary = NeoPink,
    onSecondary = NeoBlack,
    tertiary = NeoGreen,
    onTertiary = NeoBlack,
    background = NeoDarkBackground,
    onBackground = NeoDarkText,
    surface = NeoDarkSurface,
    onSurface = NeoDarkText,
    surfaceVariant = NeoDarkSurfaceVariant,
    onSurfaceVariant = NeoDarkText,
    outline = NeoDarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = NeoYellow,
    onPrimary = NeoBlack,
    primaryContainer = NeoYellow,
    onPrimaryContainer = NeoBlack,
    secondary = NeoPink,
    onSecondary = NeoBlack,
    tertiary = NeoGreen,
    onTertiary = NeoBlack,
    background = NeoBackground,
    onBackground = NeoText,
    surface = NeoSurface,
    onSurface = NeoText,
    surfaceVariant = NeoSurfaceVariant,
    onSurfaceVariant = NeoText,
    outline = NeoBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
