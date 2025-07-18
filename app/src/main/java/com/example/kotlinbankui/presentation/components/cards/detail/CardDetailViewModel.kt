package com.example.kotlinbankui.presentation.screens.cards.detail

import androidx.lifecycle.ViewModel
import com.example.kotlinbankui.presentation.components.cards.detail.CardDetail
import com.example.kotlinbankui.presentation.components.cards.detail.CardTransaction
import com.example.kotlinbankui.presentation.components.cards.detail.CardType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CardDetailUiState(
    val card: CardDetail? = null,
    val recentTransactions: List<CardTransaction> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class CardDetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CardDetailUiState())
    val uiState: StateFlow<CardDetailUiState> = _uiState.asStateFlow()

    fun loadCardDetail(cardId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        try {
            // Simuler un appel API avec les données mockées
            val card = getMockCard(cardId)
            val transactions = getMockCardTransactions(cardId)

            if (card != null) {
                _uiState.value = _uiState.value.copy(
                    card = card,
                    recentTransactions = transactions,
                    isLoading = false
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    error = "Card not found",
                    isLoading = false
                )
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                error = "Failed to load card details",
                isLoading = false
            )
        }
    }

    fun refreshCard(cardId: String) {
        loadCardDetail(cardId)
    }

    private fun getMockCard(cardId: String): CardDetail? {
        val mockCards = listOf(
            CardDetail(
                id = "1",
                cardName = "JOHN DOE",
                cardNumber = "4532123456789012",
                expiryDate = "12/26",
                cvv = "123",
                cardType = CardType.VISA,
                balance = "$3,245.50",
                availableCredit = "$6,754.50",
                isActive = true,
                isBlocked = false
            ),
            CardDetail(
                id = "2",
                cardName = "JOHN DOE",
                cardNumber = "5555123456789012",
                expiryDate = "08/27",
                cvv = "456",
                cardType = CardType.MASTERCARD,
                balance = "$1,890.25",
                availableCredit = "$8,109.75",
                isActive = true,
                isBlocked = false
            ),
            CardDetail(
                id = "3",
                cardName = "JOHN DOE",
                cardNumber = "378234567890123",
                expiryDate = "03/28",
                cvv = "7890",
                cardType = CardType.AMERICAN_EXPRESS,
                balance = "$567.80",
                availableCredit = "$4,432.20",
                isActive = false,
                isBlocked = true
            )
        )

        return mockCards.find { it.id == cardId }
    }

    private fun getMockCardTransactions(cardId: String): List<CardTransaction> {
        return listOf(
            CardTransaction(
                id = "1",
                merchant = "Amazon",
                amount = "-$67.85",
                date = "Today",
                time = "3:45 PM",
                category = "Shopping",
                isIncome = false
            ),
            CardTransaction(
                id = "2",
                merchant = "Netflix",
                amount = "-$15.99",
                date = "Today",
                time = "2:30 PM",
                category = "Entertainment",
                isIncome = false
            ),
            CardTransaction(
                id = "3",
                merchant = "Starbucks",
                amount = "-$5.75",
                date = "Yesterday",
                time = "8:15 AM",
                category = "Food",
                isIncome = false
            ),
            CardTransaction(
                id = "4",
                merchant = "Salary Deposit",
                amount = "+$3,450.00",
                date = "Yesterday",
                time = "9:00 AM",
                category = "Income",
                isIncome = true
            ),
            CardTransaction(
                id = "5",
                merchant = "Uber",
                amount = "-$12.50",
                date = "2 days ago",
                time = "10:15 AM",
                category = "Transport",
                isIncome = false
            ),
            CardTransaction(
                id = "6",
                merchant = "Target",
                amount = "-$89.23",
                date = "2 days ago",
                time = "2:45 PM",
                category = "Shopping",
                isIncome = false
            ),
            CardTransaction(
                id = "7",
                merchant = "McDonald's",
                amount = "-$8.99",
                date = "3 days ago",
                time = "12:30 PM",
                category = "Food",
                isIncome = false
            ),
            CardTransaction(
                id = "8",
                merchant = "Gas Station",
                amount = "-$45.67",
                date = "3 days ago",
                time = "7:20 AM",
                category = "Transport",
                isIncome = false
            )
        )
    }
}