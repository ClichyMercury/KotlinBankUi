package com.example.kotlinbankui.presentation.screens.transactions.detail

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.kotlinbankui.presentation.screens.transactions.TransactionDetail
import com.example.kotlinbankui.presentation.screens.transactions.getTransactions

data class TransactionDetailUiState(
    val transaction: TransactionDetail? = null,
    val similarTransactions: List<TransactionDetail> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class TransactionDetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionDetailUiState())
    val uiState: StateFlow<TransactionDetailUiState> = _uiState.asStateFlow()

    fun loadTransactionDetail(transactionId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        try {
            // Simuler un appel API avec les données mockées
            val allTransactions = getTransactions()
            val transaction = allTransactions.find { it.id == transactionId }

            if (transaction != null) {
                val similarTransactions = findSimilarTransactions(transaction, allTransactions)

                _uiState.value = _uiState.value.copy(
                    transaction = transaction,
                    similarTransactions = similarTransactions,
                    isLoading = false
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    error = "Transaction not found",
                    isLoading = false
                )
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                error = "Failed to load transaction details",
                isLoading = false
            )
        }
    }

    private fun findSimilarTransactions(
        currentTransaction: TransactionDetail,
        allTransactions: List<TransactionDetail>
    ): List<TransactionDetail> {
        return allTransactions
            .filter { it.id != currentTransaction.id }
            .filter {
                it.category == currentTransaction.category ||
                        it.title.contains(currentTransaction.title.split(" ").first(), ignoreCase = true)
            }
            .take(5)
    }

    fun refreshTransaction(transactionId: String) {
        loadTransactionDetail(transactionId)
    }
}