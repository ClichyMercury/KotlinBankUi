package com.example.kotlinbankui.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val FinSimLightColors = lightColorScheme(
    primary = FinSimBlue,
    onPrimary = FinSimSurface,
    primaryContainer = FinSimBlueLight,
    onPrimaryContainer = FinSimBlueOnContainer,

    secondary = FinSimGreen,
    onSecondary = FinSimSurface,
    secondaryContainer = FinSimGreenLight,
    onSecondaryContainer = FinSimGreenOnContainer,

    tertiary = FinSimGold,
    onTertiary = FinSimSurface,
    tertiaryContainer = FinSimGoldLight,
    onTertiaryContainer = FinSimGoldOnContainer,

    error = FinSimRed,
    onError = FinSimSurface,
    errorContainer = FinSimRedLight,
    onErrorContainer = FinSimRedOnContainer,

    background = FinSimBg,
    onBackground = FinSimTextPrimary,

    surface = FinSimSurface,
    onSurface = FinSimTextPrimary,
    surfaceVariant = FinSimSurfaceVariant,
    onSurfaceVariant = FinSimTextSecondary,

    outline = FinSimOutline,
    outlineVariant = FinSimOutlineVariant
)

@Composable
fun FinSimTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = FinSimLightColors,
        typography = FinSimTypography,
        content = content
    )
}

// Kept as alias for compatibility during the refactor
@Composable
fun KotlinBankUITheme(content: @Composable () -> Unit) = FinSimTheme(content)
