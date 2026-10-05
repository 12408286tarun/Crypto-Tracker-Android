package com.example.cryptotracker.data.repository
import com.example.cryptotracker.data.model.Coin
import com.example.cryptotracker.data.model.CoinMarketChart
import com.example.cryptotracker.data.model.GlobalMarketData
import com.example.cryptotracker.data.remote.CoinGeckoApi
class CoinRepository(
    private val api: CoinGeckoApi
) {
    suspend fun getCoins(): List<Coin> {
        return api.getCoins()
    }
    suspend fun getGlobalMarketData(): GlobalMarketData {
        return api.getGlobalMarketData().data
    }
    suspend fun getCoinMarketChart(
        coinId: String,
        days: Int = 7
    ): CoinMarketChart {
        return api.getCoinMarketChart(
            coinId = coinId,
            days = days
        )
    }
}