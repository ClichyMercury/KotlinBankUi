package com.finsim.di

import com.finsim.data.auth.AuthRepository
import com.finsim.data.auth.SessionManager
import com.finsim.data.market.MarketRepository
import com.finsim.data.network.ApiClient
import com.finsim.data.orders.OrderRepository
import com.finsim.data.portfolio.PortfolioRepository
import com.finsim.presentation.screens.auth.LoginViewModel
import com.finsim.presentation.screens.auth.RegisterViewModel
import com.finsim.presentation.screens.dashboard.DashboardViewModel
import com.finsim.presentation.screens.market.AssetDetailViewModel
import com.finsim.presentation.screens.market.MarketViewModel
import com.finsim.presentation.screens.orders.OrdersViewModel
import com.finsim.presentation.screens.profile.ProfileViewModel
import com.finsim.presentation.screens.splash.SplashViewModel
import com.finsim.presentation.screens.trading.BuyViewModel
import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Cross-platform DI definitions. Platform code provides:
 * - `TokenStore` (Android: AndroidTokenStore via DataStore, iOS: IosTokenStore via NSUserDefaults)
 * - `ThemePreferenceStore` (Android: DataStore-backed, iOS: NSUserDefaults-backed)
 * - `ApiClientConfig` (base URL + debug flag) so we don't hard-code BuildConfig refs in :shared
 */
val sharedModule: Module = module {
    single<HttpClient> {
        val config: ApiClientConfig = get()
        ApiClient.create(baseUrl = config.baseUrl, debug = config.debug)
    }

    single { SessionManager(tokenStore = get()) }
    single { AuthRepository(client = get(), tokenStore = get(), sessionManager = get()) }
    single { MarketRepository(client = get()) }
    single { OrderRepository(client = get(), tokenStore = get(), sessionManager = get()) }
    single { PortfolioRepository(client = get(), tokenStore = get(), sessionManager = get()) }

    viewModel { SplashViewModel(authRepository = get()) }
    viewModel { LoginViewModel(authRepository = get()) }
    viewModel { RegisterViewModel(authRepository = get()) }
    viewModel { DashboardViewModel(portfolioRepository = get(), authRepository = get(), marketRepository = get()) }
    viewModel { MarketViewModel(marketRepository = get()) }
    viewModel { AssetDetailViewModel(marketRepository = get()) }
    viewModel { OrdersViewModel(orderRepository = get(), marketRepository = get()) }
    viewModel { ProfileViewModel(authRepository = get(), themePreferenceStore = get()) }
    viewModel { BuyViewModel(marketRepository = get(), orderRepository = get(), portfolioRepository = get()) }
}

data class ApiClientConfig(
    val baseUrl: String,
    val debug: Boolean
)
