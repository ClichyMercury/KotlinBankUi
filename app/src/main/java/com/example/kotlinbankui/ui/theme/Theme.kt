package com.example.kotlinbankui.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val FinSimLight = lightColorScheme(
    primary = LightPalette.Primary,
    onPrimary = LightPalette.Surface,
    primaryContainer = LightPalette.PrimaryContainer,
    onPrimaryContainer = LightPalette.OnPrimaryContainer,

    secondary = LightPalette.Secondary,
    onSecondary = LightPalette.Surface,
    secondaryContainer = LightPalette.SecondaryContainer,
    onSecondaryContainer = LightPalette.OnSecondaryContainer,

    tertiary = LightPalette.Tertiary,
    onTertiary = LightPalette.Surface,
    tertiaryContainer = LightPalette.TertiaryContainer,
    onTertiaryContainer = LightPalette.OnTertiaryContainer,

    error = LightPalette.Error,
    onError = LightPalette.Surface,
    errorContainer = LightPalette.ErrorContainer,
    onErrorContainer = LightPalette.OnErrorContainer,

    background = LightPalette.Bg,
    onBackground = LightPalette.OnBg,

    surface = LightPalette.Surface,
    onSurface = LightPalette.OnSurface,
    surfaceVariant = LightPalette.SurfaceVariant,
    onSurfaceVariant = LightPalette.OnSurfaceVariant,

    outline = LightPalette.Outline,
    outlineVariant = LightPalette.OutlineVariant
)

private val FinSimDark = darkColorScheme(
    primary = DarkPalette.Primary,
    onPrimary = DarkPalette.OnBg,
    primaryContainer = DarkPalette.PrimaryContainer,
    onPrimaryContainer = DarkPalette.OnPrimaryContainer,

    secondary = DarkPalette.Secondary,
    onSecondary = DarkPalette.OnBg,
    secondaryContainer = DarkPalette.SecondaryContainer,
    onSecondaryContainer = DarkPalette.OnSecondaryContainer,

    tertiary = DarkPalette.Tertiary,
    onTertiary = DarkPalette.OnBg,
    tertiaryContainer = DarkPalette.TertiaryContainer,
    onTertiaryContainer = DarkPalette.OnTertiaryContainer,

    error = DarkPalette.Error,
    onError = DarkPalette.OnBg,
    errorContainer = DarkPalette.ErrorContainer,
    onErrorContainer = DarkPalette.OnErrorContainer,

    background = DarkPalette.Bg,
    onBackground = DarkPalette.OnBg,

    surface = DarkPalette.Surface,
    onSurface = DarkPalette.OnSurface,
    surfaceVariant = DarkPalette.SurfaceVariant,
    onSurfaceVariant = DarkPalette.OnSurfaceVariant,

    outline = DarkPalette.Outline,
    outlineVariant = DarkPalette.OutlineVariant
)

@Composable
fun FinSimTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) FinSimDark else FinSimLight,
        typography = FinSimTypography,
        content = content
    )
}

@Composable
fun KotlinBankUITheme(content: @Composable () -> Unit) = FinSimTheme(content = content)
