package com.example.kotlinbankui.presentation.components.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlinbankui.R
import com.example.kotlinbankui.domain.models.CardItem
import com.example.kotlinbankui.ui.theme.BlueEnd
import com.example.kotlinbankui.ui.theme.BlueStart
import com.example.kotlinbankui.ui.theme.GreenEnd
import com.example.kotlinbankui.ui.theme.GreenStart
import com.example.kotlinbankui.ui.theme.OrangeEnd
import com.example.kotlinbankui.ui.theme.OrangeStart
import com.example.kotlinbankui.ui.theme.PurpleEnd
import com.example.kotlinbankui.ui.theme.PurpleStart

val cards = listOf(
    CardItem(
        id = "1",
        cardType = "VISA",
        cardNumber = "7856 5694 2641 5154",
        cardName = "Business",
        balance = 48.674,
        color = getGradient(PurpleStart, PurpleEnd)
    ),

    CardItem(
        id = "2",
        cardType = "MASTER CARD",
        cardNumber = "8964 2545 5887 1586",
        cardName = "Savings",
        balance = 8.359,
        color = getGradient(BlueStart, BlueEnd)
    ),

    CardItem(
        id = "3",
        cardType = "VISA",
        cardNumber = "4526 1758 3624 9651",
        cardName = "School",
        balance = 9.165,
        color = getGradient(OrangeStart, OrangeEnd)
    ),

    CardItem(
        id = "4",
        cardType = "MASTER CARD",
        cardNumber = "7856 5694 2641 5154",
        cardName = "Trips",
        balance = 26.047,
        color = getGradient(GreenStart, GreenEnd)
    ),
)

fun getGradient(
    startColor : Color,
    endColor : Color,
) : Brush {
    return Brush.horizontalGradient(
        colors = listOf(startColor, endColor)
    )
}

@Preview
@Composable
fun CardsSection(
    onCardClick: (String) -> Unit = {} // Ajouter callback pour navigation
) {
    LazyRow {
        items(cards.size) { index ->
            CardComposable(
                index = index,
                onCardClick = onCardClick
            )
        }
    }
}

@Composable
fun CardComposable(
    index : Int,
    onCardClick: (String) -> Unit = {} // Ajouter callback
) {
    val card = cards[index]
    var lastItemPadding = 0.dp
    if (index == cards.size - 1) {
        lastItemPadding = 16.dp
    }

    var image = painterResource(id = R.drawable.ic_visa)
    if (card.cardType == "MASTER CARD") {
        image = painterResource(id = R.drawable.ic_mastercard)
    }

    Box(
        modifier = Modifier
            .padding(start = 16.dp, end = lastItemPadding)
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(25.dp))
                .background(card.color)
                .width(250.dp)
                .height(160.dp)
                .clickable { onCardClick(card.id) } // Utiliser le callback
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = image,
                contentDescription = card.cardName,
                modifier = Modifier.width(60.dp)
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = card.cardName,
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold

            )

            Text(
                text = "$ ${card.balance}",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold

            )

            Text(
                text = card.cardNumber,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold

            )
        }
    }
}