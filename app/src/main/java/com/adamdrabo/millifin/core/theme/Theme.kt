package com.adamdrabo.millifin.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    secondary = LightGold,
    secondaryContainer = LightGoldSoft,
    onSecondaryContainer = LightText,
    background = LightBackground,
    onBackground = LightText,
    surface = LightSurface,
    onSurface = LightText,
    surfaceVariant = LightGoldSoft,
    onSurfaceVariant = LightMuted,
    outline = LightBorder,
    outlineVariant = LightBorder,
    error = LightError,
    errorContainer = LightErrorSoft
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    secondary = DarkPrimary,
    secondaryContainer = DarkGoldSoft,
    onSecondaryContainer = DarkText,
    background = DarkBackground,
    onBackground = DarkText,
    surface = DarkSurface,
    onSurface = DarkText,
    surfaceVariant = DarkGoldSoft,
    onSurfaceVariant = DarkMuted,
    outline = DarkBorder,
    outlineVariant = DarkBorder,
    error = DarkError,
    errorContainer = DarkErrorSoft
)

@Immutable
data class AnswerColors(
    val success: Color,
    val successContainer: Color,
    val error: Color,
    val errorContainer: Color
)

private val LightAnswerColors = AnswerColors(
    success = LightSuccess,
    successContainer = LightSuccessSoft,
    error = LightError,
    errorContainer = LightErrorSoft
)

private val DarkAnswerColors = AnswerColors(
    success = DarkSuccess,
    successContainer = DarkSuccessSoft,
    error = DarkError,
    errorContainer = DarkErrorSoft
)

val LocalAnswerColors = staticCompositionLocalOf { LightAnswerColors }

@Composable
fun MillifinTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalAnswerColors provides if (darkTheme) DarkAnswerColors else LightAnswerColors
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
            typography = Typography,
            content = content
        )
    }
}
