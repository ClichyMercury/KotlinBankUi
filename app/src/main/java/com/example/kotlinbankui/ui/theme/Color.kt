package com.example.kotlinbankui.ui.theme

import androidx.compose.ui.graphics.Color

// FinSim brand palette (cf. plan_frontend_finsim.pdf §6.1)
val FinSimBlue = Color(0xFF1A56A0)          // primary — finance, confiance
val FinSimBlueDark = Color(0xFF103E78)
val FinSimBlueLight = Color(0xFFE3EDF8)
val FinSimBlueOnContainer = Color(0xFF0A2B57)

val FinSimGreen = Color(0xFF00C896)         // secondary — hausse, success
val FinSimGreenLight = Color(0xFFD9F7EE)
val FinSimGreenOnContainer = Color(0xFF00513D)

val FinSimRed = Color(0xFFE74C3C)           // error — baisse
val FinSimRedLight = Color(0xFFFDE7E5)
val FinSimRedOnContainer = Color(0xFF7A1F18)

val FinSimGold = Color(0xFFF0A500)          // tertiary — gamification
val FinSimGoldLight = Color(0xFFFFF1D6)
val FinSimGoldOnContainer = Color(0xFF4A3000)

// Neutrals
val FinSimBg = Color(0xFFF5F7FA)
val FinSimSurface = Color(0xFFFFFFFF)
val FinSimSurfaceVariant = Color(0xFFEDF0F5)
val FinSimTextPrimary = Color(0xFF1A1A2E)
val FinSimTextSecondary = Color(0xFF5C5C72)
val FinSimOutline = Color(0xFFD4D8E0)
val FinSimOutlineVariant = Color(0xFFE5E8EE)

// Helpers for trend colors (UI sugar — keep aligned with secondary/error)
val TrendUp = FinSimGreen
val TrendDown = FinSimRed
val TrendNeutral = FinSimTextSecondary
