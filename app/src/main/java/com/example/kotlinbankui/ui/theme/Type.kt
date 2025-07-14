package com.example.kotlinbankui.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

/*package com.example.kotlinbankui.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.kotlinbankui.R

val MichromaFontFamily = FontFamily(
    Font(R.font.michroma_regular, FontWeight.Normal),
    Font(R.font.michroma_regular, FontWeight.Medium),
    Font(R.font.michroma_regular, FontWeight.SemiBold),
    Font(R.font.michroma_regular, FontWeight.Bold)
)

val Typography = Typography(
    // Headers - Michroma pour les titres importants
    headlineLarge = Typography().headlineLarge.copy(
        fontFamily = MichromaFontFamily
    ),
    headlineMedium = Typography().headlineMedium.copy(
        fontFamily = MichromaFontFamily
    ),
    headlineSmall = Typography().headlineSmall.copy(
        fontFamily = MichromaFontFamily
    ),

    // Titles - Michroma pour les titres
    titleLarge = Typography().titleLarge.copy(
        fontFamily = MichromaFontFamily
    ),
    titleMedium = Typography().titleMedium.copy(
        fontFamily = MichromaFontFamily
    ),
    titleSmall = Typography().titleSmall.copy(
        fontFamily = MichromaFontFamily
    ),

    // Body text - Garde la police système pour la lisibilité
    bodyLarge = Typography().bodyLarge.copy(
        fontFamily = FontFamily.Default
    ),
    bodyMedium = Typography().bodyMedium.copy(
        fontFamily = FontFamily.Default
    ),
    bodySmall = Typography().bodySmall.copy(
        fontFamily = FontFamily.Default
    ),

    // Labels - Michroma pour les labels importants
    labelLarge = Typography().labelLarge.copy(
        fontFamily = MichromaFontFamily
    ),
    labelMedium = Typography().labelMedium.copy(
        fontFamily = FontFamily.Default // Police système pour les petits textes
    ),
    labelSmall = Typography().labelSmall.copy(
        fontFamily = FontFamily.Default
    )
)*/