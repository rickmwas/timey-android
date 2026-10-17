package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class AppThemeMode(val displayName: String, val description: String) {
    LIGHTS_OUT("Lights Out", "Pure AMOLED black (#000000)"),
    DIM("Dim", "Midnight navy slate (#15202B)"),
    LIGHT("Pure Light", "Crisp white minimalist (#FFFFFF)")
}

// 1. X "Lights Out" (Pure Black AMOLED)
private val XLightsOutColorScheme = darkColorScheme(
    primary = XBlue,
    onPrimary = XWhite,
    primaryContainer = XBlueContainer,
    onPrimaryContainer = XWhite,
    secondary = XGreen,
    onSecondary = XWhite,
    secondaryContainer = XGreenContainer,
    onSecondaryContainer = XWhite,
    tertiary = XYellow,
    onTertiary = XLightsOutBackground,
    tertiaryContainer = XYellowContainer,
    onTertiaryContainer = XWhite,
    error = XRed,
    onError = XWhite,
    errorContainer = XRedContainer,
    onErrorContainer = XWhite,
    background = XLightsOutBackground,
    onBackground = XLightsOutTextPrimary,
    surface = XLightsOutBackground,
    onSurface = XLightsOutTextPrimary,
    surfaceVariant = XLightsOutCard,
    onSurfaceVariant = XLightsOutTextSecondary,
    outline = XLightsOutBorder,
    outlineVariant = XLightsOutBorderSubtle
)

// 2. X "Dim" (Iconic Twitter Midnight Slate)
private val XDimColorScheme = darkColorScheme(
    primary = XBlue,
    onPrimary = XWhite,
    primaryContainer = Color(0xFF1D3953),
    onPrimaryContainer = XWhite,
    secondary = XGreen,
    onSecondary = XWhite,
    secondaryContainer = Color(0xFF133E31),
    onSecondaryContainer = XWhite,
    tertiary = XYellow,
    onTertiary = XDimBackground,
    tertiaryContainer = Color(0xFF3E3614),
    onTertiaryContainer = XWhite,
    error = XRed,
    onError = XWhite,
    errorContainer = Color(0xFF4A181C),
    onErrorContainer = XWhite,
    background = XDimBackground,
    onBackground = XDimTextPrimary,
    surface = XDimBackground,
    onSurface = XDimTextPrimary,
    surfaceVariant = XDimCard,
    onSurfaceVariant = XDimTextSecondary,
    outline = XDimBorder,
    outlineVariant = XDimBorderSubtle
)

// 3. X "Light" (Clean Crisp Pure White)
private val XLightColorScheme = lightColorScheme(
    primary = XBlue,
    onPrimary = XWhite,
    primaryContainer = Color(0xFFE8F5FD),
    onPrimaryContainer = XBlue,
    secondary = XGreen,
    onSecondary = XWhite,
    secondaryContainer = Color(0xFFE6F9F2),
    onSecondaryContainer = XGreen,
    tertiary = Color(0xFFD97706),
    onTertiary = XWhite,
    tertiaryContainer = Color(0xFFFEF3C7),
    onTertiaryContainer = Color(0xFF92400E),
    error = XRed,
    onError = XWhite,
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = XRed,
    background = XLightBackground,
    onBackground = XLightTextPrimary,
    surface = XLightBackground,
    onSurface = XLightTextPrimary,
    surfaceVariant = XLightSurfaceVariant,
    onSurfaceVariant = XLightTextSecondary,
    outline = XLightBorderStrong,
    outlineVariant = XLightBorder
)

@Composable
fun TimyTheme(
    themeMode: AppThemeMode = AppThemeMode.LIGHTS_OUT,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        AppThemeMode.LIGHTS_OUT -> XLightsOutColorScheme
        AppThemeMode.DIM -> XDimColorScheme
        AppThemeMode.LIGHT -> XLightColorScheme
    }

    val isDark = themeMode != AppThemeMode.LIGHT

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !isDark
                insetsController.isAppearanceLightNavigationBars = !isDark
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Backward-compatible overload for Boolean darkTheme
@Composable
fun TimyTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    TimyTheme(
        themeMode = if (darkTheme) AppThemeMode.LIGHTS_OUT else AppThemeMode.LIGHT,
        content = content
    )
}
