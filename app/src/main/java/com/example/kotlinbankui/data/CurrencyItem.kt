package com.example.kotlinbankui.data

import androidx.compose.ui.graphics.vector.ImageVector

data class CurrencyItem(
    val name: String,
    val buy: Float,
    val sell: Float,
    val icon: ImageVector
)
