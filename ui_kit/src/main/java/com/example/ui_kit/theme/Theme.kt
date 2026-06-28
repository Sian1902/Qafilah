package com.example.ui_kit.theme


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary      = Primary,
    secondary    = Secondary_Light,
    tertiary     = Tertiary,
    background   = Background_Light,
    surface      = Surface_Light,
    onPrimary    = White,
    onSecondary  = White,
    onBackground = TextBody_Light,
    onSurface    = TextBody_Light,
    error        = Danger_Light
)

private val DarkColorScheme = darkColorScheme(
    primary      = Primary,
    secondary    = Secondary_Dark,
    tertiary     = Tertiary,
    background   = Background_Dark,
    surface      = Surface_Dark,
    onPrimary    = White,
    onSecondary  = White,
    onBackground = TextBody_Dark,
    onSurface    = TextBody_Dark,
    error        = Danger_Dark
)

@Composable
fun QafilahTheme(
    darkTheme: Boolean = true ,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = QafilahTypography,
        content     = content
    )
}