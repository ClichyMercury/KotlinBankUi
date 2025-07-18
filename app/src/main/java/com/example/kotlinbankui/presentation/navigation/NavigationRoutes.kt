package com.example.kotlinbankui.presentation.navigation

object NavigationRoutes {
    const val HOME = "home"
    const val WALLET = "wallet"
    const val TRANSACTIONS = "transactions"
    const val NOTIFICATIONS = "notifications"
    const val PROFILE = "profile"

    // Detail screens
    const val TRANSACTION_DETAIL = "transaction_detail/{transactionId}"
    const val CARD_DETAIL = "card_detail/{cardId}"
    const val NOTIFICATION_DETAIL = "notification_detail/{notificationId}"

    // Helper functions to create routes with parameters
    fun transactionDetail(transactionId: String) = "transaction_detail/$transactionId"
    fun cardDetail(cardId: String) = "card_detail/$cardId"
    fun notificationDetail(notificationId: String) = "notification_detail/$notificationId"
}