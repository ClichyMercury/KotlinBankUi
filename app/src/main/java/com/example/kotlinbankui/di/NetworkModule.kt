package com.example.kotlinbankui.di

import android.content.Context
import com.example.kotlinbankui.BuildConfig
import com.finsim.data.auth.AndroidTokenStore
import com.finsim.data.auth.AuthRepository
import com.finsim.data.auth.SessionManager
import com.finsim.data.auth.TokenStore
import com.finsim.data.market.MarketRepository
import com.finsim.data.network.ApiClient
import com.finsim.data.orders.OrderRepository
import com.finsim.data.portfolio.PortfolioRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient = ApiClient.create(
        baseUrl = BuildConfig.API_BASE_URL,
        debug = BuildConfig.DEBUG
    )

    @Provides
    @Singleton
    fun provideTokenStore(@ApplicationContext context: Context): TokenStore =
        AndroidTokenStore(context)

    @Provides
    @Singleton
    fun provideSessionManager(tokenStore: TokenStore): SessionManager =
        SessionManager(tokenStore)

    @Provides
    @Singleton
    fun provideAuthRepository(
        client: HttpClient,
        tokenStore: TokenStore,
        sessionManager: SessionManager
    ): AuthRepository = AuthRepository(client, tokenStore, sessionManager)

    @Provides
    @Singleton
    fun provideMarketRepository(client: HttpClient): MarketRepository =
        MarketRepository(client)

    @Provides
    @Singleton
    fun provideOrderRepository(
        client: HttpClient,
        tokenStore: TokenStore,
        sessionManager: SessionManager
    ): OrderRepository = OrderRepository(client, tokenStore, sessionManager)

    @Provides
    @Singleton
    fun providePortfolioRepository(
        client: HttpClient,
        tokenStore: TokenStore,
        sessionManager: SessionManager
    ): PortfolioRepository = PortfolioRepository(client, tokenStore, sessionManager)
}
