package com.northstar.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Navy,
    onPrimary = CreamText,
    secondary = Amber,
    onSecondary = Ink,
    background = CreamPhone,
    surface = CreamInput,
    onBackground = Ink,
    onSurface = Ink,
    onSurfaceVariant = MutedBrown,
    outline = Navy,
)

private val DarkColorScheme = darkColorScheme(
    primary = Amber,
    onPrimary = Ink,
    secondary = Navy,
    onSecondary = CreamText,
    background = DarkPhone,
    surface = DarkSurface,
    onBackground = CreamText,
    onSurface = CreamText,
    onSurfaceVariant = DarkMuted,
    outline = Amber,
)

@Composable
fun NorthStarTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content,
    )
}
