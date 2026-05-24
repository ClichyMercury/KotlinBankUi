package com.example.kotlinbankui.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.example.kotlinbankui.R

private val googleFontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

private val interGoogle = GoogleFont("Inter")
private val robotoMonoGoogle = GoogleFont("Roboto Mono")

val Inter = FontFamily(
    Font(googleFont = interGoogle, fontProvider = googleFontProvider, weight = FontWeight.Normal),
    Font(googleFont = interGoogle, fontProvider = googleFontProvider, weight = FontWeight.Medium),
    Font(googleFont = interGoogle, fontProvider = googleFontProvider, weight = FontWeight.SemiBold),
    Font(googleFont = interGoogle, fontProvider = googleFontProvider, weight = FontWeight.Bold),
    Font(googleFont = interGoogle, fontProvider = googleFontProvider, weight = FontWeight.ExtraBold)
)

val RobotoMono = FontFamily(
    Font(googleFont = robotoMonoGoogle, fontProvider = googleFontProvider, weight = FontWeight.Normal),
    Font(googleFont = robotoMonoGoogle, fontProvider = googleFontProvider, weight = FontWeight.Medium),
    Font(googleFont = robotoMonoGoogle, fontProvider = googleFontProvider, weight = FontWeight.SemiBold),
    Font(googleFont = robotoMonoGoogle, fontProvider = googleFontProvider, weight = FontWeight.Bold)
)

val FinSimTypography = Typography(
    displayLarge = TextStyle(fontFamily = Inter, fontWeight = FontWeight.ExtraBold, fontSize = 44.sp, lineHeight = 52.sp, letterSpacing = (-1).sp),
    displayMedium = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Bold, fontSize = 34.sp, lineHeight = 42.sp, letterSpacing = (-0.5).sp),
    displaySmall = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 36.sp),

    headlineLarge = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Bold, fontSize = 30.sp, lineHeight = 38.sp, letterSpacing = (-0.5).sp),
    headlineMedium = TextStyle(fontFamily = Inter, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 28.sp),
    headlineSmall = TextStyle(fontFamily = Inter, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 24.sp),

    titleLarge = TextStyle(fontFamily = Inter, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 24.sp),
    titleMedium = TextStyle(fontFamily = Inter, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 22.sp),
    titleSmall = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp),

    bodyLarge = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp),

    labelLarge = TextStyle(fontFamily = Inter, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.2.sp),
    labelMedium = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.3.sp),
    labelSmall = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 10.sp, lineHeight = 14.sp, letterSpacing = 0.5.sp)
)

// Monospaced styles dedicated to financial figures (Roboto Mono, vertical alignment)
val MoneySmall = TextStyle(fontFamily = RobotoMono, fontWeight = FontWeight.Medium, fontSize = 13.sp, lineHeight = 18.sp)
val MoneyText = TextStyle(fontFamily = RobotoMono, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 20.sp)
val MoneyLarge = TextStyle(fontFamily = RobotoMono, fontWeight = FontWeight.SemiBold, fontSize = 28.sp, lineHeight = 36.sp, letterSpacing = (-0.5).sp)
val MoneyHuge = TextStyle(fontFamily = RobotoMono, fontWeight = FontWeight.Bold, fontSize = 38.sp, lineHeight = 46.sp, letterSpacing = (-0.8).sp)
val MoneyHero = TextStyle(fontFamily = RobotoMono, fontWeight = FontWeight.Bold, fontSize = 48.sp, lineHeight = 56.sp, letterSpacing = (-1).sp)
