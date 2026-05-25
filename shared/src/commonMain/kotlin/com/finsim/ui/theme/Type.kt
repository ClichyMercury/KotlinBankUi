package com.finsim.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.finsim.resources.Res
import com.finsim.resources.inter
import com.finsim.resources.roboto_mono
import org.jetbrains.compose.resources.Font

@Composable
private fun rememberInterFamily(): FontFamily {
    val r400 = Font(Res.font.inter, weight = FontWeight.Normal, variationSettings = FontVariation.Settings(FontVariation.weight(400)))
    val r500 = Font(Res.font.inter, weight = FontWeight.Medium, variationSettings = FontVariation.Settings(FontVariation.weight(500)))
    val r600 = Font(Res.font.inter, weight = FontWeight.SemiBold, variationSettings = FontVariation.Settings(FontVariation.weight(600)))
    val r700 = Font(Res.font.inter, weight = FontWeight.Bold, variationSettings = FontVariation.Settings(FontVariation.weight(700)))
    val r800 = Font(Res.font.inter, weight = FontWeight.ExtraBold, variationSettings = FontVariation.Settings(FontVariation.weight(800)))
    return remember { FontFamily(r400, r500, r600, r700, r800) }
}

@Composable
private fun rememberRobotoMonoFamily(): FontFamily {
    val r400 = Font(Res.font.roboto_mono, weight = FontWeight.Normal, variationSettings = FontVariation.Settings(FontVariation.weight(400)))
    val r500 = Font(Res.font.roboto_mono, weight = FontWeight.Medium, variationSettings = FontVariation.Settings(FontVariation.weight(500)))
    val r600 = Font(Res.font.roboto_mono, weight = FontWeight.SemiBold, variationSettings = FontVariation.Settings(FontVariation.weight(600)))
    val r700 = Font(Res.font.roboto_mono, weight = FontWeight.Bold, variationSettings = FontVariation.Settings(FontVariation.weight(700)))
    return remember { FontFamily(r400, r500, r600, r700) }
}

val Inter: FontFamily
    @Composable get() = rememberInterFamily()

val RobotoMono: FontFamily
    @Composable get() = rememberRobotoMonoFamily()

@Composable
fun finSimTypography(): Typography {
    val inter = Inter
    return Typography(
        displayLarge = TextStyle(fontFamily = inter, fontWeight = FontWeight.ExtraBold, fontSize = 44.sp, lineHeight = 52.sp, letterSpacing = (-1).sp),
        displayMedium = TextStyle(fontFamily = inter, fontWeight = FontWeight.Bold, fontSize = 34.sp, lineHeight = 42.sp, letterSpacing = (-0.5).sp),
        displaySmall = TextStyle(fontFamily = inter, fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 36.sp),

        headlineLarge = TextStyle(fontFamily = inter, fontWeight = FontWeight.Bold, fontSize = 30.sp, lineHeight = 38.sp, letterSpacing = (-0.5).sp),
        headlineMedium = TextStyle(fontFamily = inter, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 28.sp),
        headlineSmall = TextStyle(fontFamily = inter, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 24.sp),

        titleLarge = TextStyle(fontFamily = inter, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 24.sp),
        titleMedium = TextStyle(fontFamily = inter, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 22.sp),
        titleSmall = TextStyle(fontFamily = inter, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp),

        bodyLarge = TextStyle(fontFamily = inter, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
        bodyMedium = TextStyle(fontFamily = inter, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
        bodySmall = TextStyle(fontFamily = inter, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp),

        labelLarge = TextStyle(fontFamily = inter, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.2.sp),
        labelMedium = TextStyle(fontFamily = inter, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.3.sp),
        labelSmall = TextStyle(fontFamily = inter, fontWeight = FontWeight.Medium, fontSize = 10.sp, lineHeight = 14.sp, letterSpacing = 0.5.sp)
    )
}

// Monospaced styles dedicated to financial figures (Roboto Mono).
// Property-with-@Composable-getter pattern preserves the legacy call-site API: `style = MoneyText`.
val MoneySmall: TextStyle
    @Composable get() = TextStyle(fontFamily = RobotoMono, fontWeight = FontWeight.Medium, fontSize = 13.sp, lineHeight = 18.sp)

val MoneyText: TextStyle
    @Composable get() = TextStyle(fontFamily = RobotoMono, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 20.sp)

val MoneyLarge: TextStyle
    @Composable get() = TextStyle(fontFamily = RobotoMono, fontWeight = FontWeight.SemiBold, fontSize = 28.sp, lineHeight = 36.sp, letterSpacing = (-0.5).sp)

val MoneyHuge: TextStyle
    @Composable get() = TextStyle(fontFamily = RobotoMono, fontWeight = FontWeight.Bold, fontSize = 38.sp, lineHeight = 46.sp, letterSpacing = (-0.8).sp)

val MoneyHero: TextStyle
    @Composable get() = TextStyle(fontFamily = RobotoMono, fontWeight = FontWeight.Bold, fontSize = 48.sp, lineHeight = 56.sp, letterSpacing = (-1).sp)
