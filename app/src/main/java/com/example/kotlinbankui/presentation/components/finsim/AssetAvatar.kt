package com.example.kotlinbankui.presentation.components.finsim

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val gradients = listOf(
    listOf(Color(0xFF1A56A0), Color(0xFF3F8FE7)),
    listOf(Color(0xFF00C896), Color(0xFF3FD9B5)),
    listOf(Color(0xFFF0A500), Color(0xFFFFC857)),
    listOf(Color(0xFF6C5CE7), Color(0xFFA29BFE)),
    listOf(Color(0xFFE74C3C), Color(0xFFFF8475)),
    listOf(Color(0xFF0984E3), Color(0xFF74B9FF))
)

@Composable
fun AssetAvatar(
    ticker: String,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp
) {
    val grad = gradients[(ticker.hashCode().rem(gradients.size).let { if (it < 0) it + gradients.size else it })]
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Brush.linearGradient(grad)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = ticker.take(3),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = (size.value / 3).sp
        )
    }
}
