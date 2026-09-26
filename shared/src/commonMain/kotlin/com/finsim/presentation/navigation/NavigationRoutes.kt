package com.finsim.presentation.navigation

object NavigationRoutes {
    // Auth flow
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"

    // Main FinSim tabs
    const val DASHBOARD = "dashboard"
    const val MARKET = "market"
    const val ORDERS = "orders"
    const val PROFILE = "profile"

    // Detail / action screens
    const val ASSET_DETAIL = "asset/{assetId}"
    const val BUY = "buy/{assetId}"
    const val SELL = "sell/{assetId}"

    fun assetDetail(assetId: String) = "asset/$assetId"
    fun buy(assetId: String) = "buy/$assetId"
    fun sell(assetId: String) = "sell/$assetId"
}
