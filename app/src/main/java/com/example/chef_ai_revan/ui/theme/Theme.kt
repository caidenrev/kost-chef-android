package com.example.chef_ai_revan.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = NeoPrimary,
    secondary = NeoGreen,
    tertiary = NeoPink,
    background = NeoBlack,
    surface = NeoBlack,
    onPrimary = NeoWhite,
    onSecondary = NeoBlack,
    onTertiary = NeoWhite,
    onBackground = NeoWhite,
    onSurface = NeoWhite
)

private val LightColorScheme = lightColorScheme(
    primary = NeoPrimary,
    secondary = NeoGreen,
    tertiary = NeoPink,
    background = NeoBackground,
    surface = NeoWhite,
    onPrimary = NeoWhite,
    onSecondary = NeoBlack,
    onTertiary = NeoWhite,
    onBackground = NeoBlack,
    onSurface = NeoBlack
)

@Composable
fun ChefairevanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
