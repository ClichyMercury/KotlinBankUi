package com.finsim.presentation.components

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.finsim.ui.theme.MoneyText as MoneyTextStyle

@Composable
fun MoneyText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MoneyTextStyle,
    color: Color = LocalContentColor.current
) {
    Text(
        text = text,
        modifier = modifier,
        style = style,
        color = color
    )
}
