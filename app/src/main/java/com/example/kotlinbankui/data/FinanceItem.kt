package com.example.kotlinbankui.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class FinanceItem(
    val icon: ImageVector,
    val name: String,
    val background: Color
)
