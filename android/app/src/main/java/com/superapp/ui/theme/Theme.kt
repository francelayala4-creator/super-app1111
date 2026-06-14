package com.superapp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = Purple600,            onPrimary = Cream50,
    primaryContainer = Purple100,   onPrimaryContainer = Purple900,
    secondary = SaveTeal,           onSecondary = Cream50,
    secondaryContainer = Color(0xFFCDF5EA), onSecondaryContainer = Color(0xFF003F33),
    tertiary = Champagne,           onTertiary = Purple900,
    tertiaryContainer = Color(0xFFFFF1CC), onTertiaryContainer = Color(0xFF4D3300),
    background = Cream100,          onBackground = InkDark,
    surface = Cream50,              onSurface = InkDark,
    surfaceVariant = Purple50,      onSurfaceVariant = InkSoft,
    surfaceTint = Purple600,
    outline = Purple200,            outlineVariant = Cream200,
    error = Color(0xFFD93A4A),      onError = Cream50,
)

private val DarkColors = darkColorScheme(
    primary = Purple400,            onPrimary = Purple950,
    primaryContainer = Purple800,   onPrimaryContainer = Purple100,
    secondary = SaveTeal,           onSecondary = Night900,
    secondaryContainer = Color(0xFF004D40), onSecondaryContainer = Color(0xFF7DEFD8),
    tertiary = Champagne,           onTertiary = Night900,
    tertiaryContainer = Color(0xFF665100), onTertiaryContainer = Color(0xFFFFE8A3),
    background = Night900,          onBackground = InkOnDark,
    surface = Night800,             onSurface = InkOnDark,
    surfaceVariant = Night700,      onSurfaceVariant = Purple300,
    surfaceTint = Purple400,
    outline = Purple700,            outlineVariant = Night700,
    error = Color(0xFFFF6B7A),      onError = Night900,
)

@Composable
fun SuperAppTheme(dark: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val scheme = if (dark) DarkColors else LightColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !dark
        }
    }
    MaterialTheme(
        colorScheme = scheme,
        typography = SuperTypography,
        shapes = SuperShapes,
        content = content,
    )
}
