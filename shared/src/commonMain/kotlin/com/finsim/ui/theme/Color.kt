package com.finsim.ui.theme

import androidx.compose.ui.graphics.Color

// ==================================================================
// LIGHT PALETTE — premium fintech (Revolut/Wise inspired)
// ==================================================================
object LightPalette {
    val Bg = Color(0xFFFAFAFC)
    val Surface = Color(0xFFFFFFFF)
    val SurfaceVariant = Color(0xFFF3F4F6)
    val SurfaceElevated = Color(0xFFFFFFFF)

    val Primary = Color(0xFF0F4FB8)
    val PrimaryDark = Color(0xFF0A3B8A)
    val PrimaryContainer = Color(0xFFDBEAFE)
    val OnPrimaryContainer = Color(0xFF0A2B5C)

    val Secondary = Color(0xFF10B981)
    val SecondaryContainer = Color(0xFFD1FAE5)
    val OnSecondaryContainer = Color(0xFF04543B)

    val Tertiary = Color(0xFFF59E0B)
    val TertiaryContainer = Color(0xFFFEF3C7)
    val OnTertiaryContainer = Color(0xFF7C2D12)

    val Error = Color(0xFFEF4444)
    val ErrorContainer = Color(0xFFFEE2E2)
    val OnErrorContainer = Color(0xFF7F1D1D)

    val OnBg = Color(0xFF0F172A)
    val OnSurface = Color(0xFF0F172A)
    val OnSurfaceVariant = Color(0xFF64748B)
    val Outline = Color(0xFFE5E7EB)
    val OutlineVariant = Color(0xFFF3F4F6)
}

// ==================================================================
// DARK PALETTE — pro trading (TradingView/Robinhood inspired)
// ==================================================================
object DarkPalette {
    val Bg = Color(0xFF0A0E1A)
    val Surface = Color(0xFF111827)
    val SurfaceVariant = Color(0xFF1F2937)
    val SurfaceElevated = Color(0xFF1A2233)

    val Primary = Color(0xFF3B82F6)
    val PrimaryDark = Color(0xFF2563EB)
    val PrimaryContainer = Color(0xFF1E3A8A)
    val OnPrimaryContainer = Color(0xFFDBEAFE)

    val Secondary = Color(0xFF10B981)
    val SecondaryContainer = Color(0xFF064E3B)
    val OnSecondaryContainer = Color(0xFFA7F3D0)

    val Tertiary = Color(0xFFF59E0B)
    val TertiaryContainer = Color(0xFF78350F)
    val OnTertiaryContainer = Color(0xFFFEF3C7)

    val Error = Color(0xFFEF4444)
    val ErrorContainer = Color(0xFF7F1D1D)
    val OnErrorContainer = Color(0xFFFEE2E2)

    val OnBg = Color(0xFFF9FAFB)
    val OnSurface = Color(0xFFF9FAFB)
    val OnSurfaceVariant = Color(0xFF9CA3AF)
    val Outline = Color(0xFF1F2937)
    val OutlineVariant = Color(0xFF374151)
}

// Trend helpers (consistent across both themes — money up/down is universal)
val TrendUp = Color(0xFF10B981)
val TrendDown = Color(0xFFEF4444)
val TrendNeutral = Color(0xFF9CA3AF)

// Brand gradients
val BrandGradientStart = Color(0xFF0F4FB8)
val BrandGradientEnd = Color(0xFF1E3A8A)
val NightGradientStart = Color(0xFF0A0E1A)
val NightGradientEnd = Color(0xFF1F2937)

// Aliases for source compat with legacy components
val FinSimBlue = LightPalette.Primary
val FinSimBlueDark = LightPalette.PrimaryDark
val FinSimBlueLight = LightPalette.PrimaryContainer
val FinSimBlueOnContainer = LightPalette.OnPrimaryContainer
val FinSimGreen = LightPalette.Secondary
val FinSimGreenLight = LightPalette.SecondaryContainer
val FinSimGreenOnContainer = LightPalette.OnSecondaryContainer
val FinSimRed = LightPalette.Error
val FinSimRedLight = LightPalette.ErrorContainer
val FinSimRedOnContainer = LightPalette.OnErrorContainer
val FinSimGold = LightPalette.Tertiary
val FinSimGoldLight = LightPalette.TertiaryContainer
val FinSimGoldOnContainer = LightPalette.OnTertiaryContainer
val FinSimBg = LightPalette.Bg
val FinSimSurface = LightPalette.Surface
val FinSimSurfaceVariant = LightPalette.SurfaceVariant
val FinSimTextPrimary = LightPalette.OnBg
val FinSimTextSecondary = LightPalette.OnSurfaceVariant
val FinSimOutline = LightPalette.Outline
val FinSimOutlineVariant = LightPalette.OutlineVariant
