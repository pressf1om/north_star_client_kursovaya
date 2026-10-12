package com.northstar.app.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
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

@Composable
fun Modifier.northStarSurfaceCard(borderWidth: Dp = 1.dp): Modifier = this
    .background(MaterialTheme.colorScheme.surface)
    .border(borderWidth, MaterialTheme.colorScheme.outline)

@Composable
fun northStarOutlinedFieldColors(): TextFieldColors = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.outline,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    cursorColor = MaterialTheme.colorScheme.onSurface,
    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
)
