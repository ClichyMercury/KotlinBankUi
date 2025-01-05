package com.example.kotlinbankui.data

import androidx.compose.ui.graphics.Brush

data class CardItem(
    val cardType: String,
    val cardNumber: String,
    val cardName: String,
    val balance: Double,
    val color: Brush

)
