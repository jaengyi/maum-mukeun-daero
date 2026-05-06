package com.mmd.core.design.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = MmdGreenPrimary,
    onPrimary = MmdGreenOnPrimary,
    surface = MmdSurfaceLight,
)

private val DarkColors = darkColorScheme(
    primary = MmdGreenPrimaryDark,
    onPrimary = MmdGreenOnPrimaryDark,
    surface = MmdSurfaceDark,
)

@Composable
fun MmdTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = MmdTypography,
        content = content,
    )
}
