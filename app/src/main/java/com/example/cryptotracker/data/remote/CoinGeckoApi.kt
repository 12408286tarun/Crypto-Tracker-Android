package com.example.cryptotracker.data.remote
import com.example.cryptotracker.data.model.Coin
import com.example.cryptotracker.data.model.CoinMarketChart
import com.example.cryptotracker.data.model.GlobalMarketResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
interface CoinGeckoApi {
    @GET("coins/markets")
    suspend fun getCoins(
        @Query("vs_currency") currency: String = "usd",
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") perPage: Int = 20,
        @Query("page") page: Int = 1,
        @Query("price_change_percentage") priceChangePercentage: String = "24h"
    ): List<Coin>
    @GET("global")
    suspend fun getGlobalMarketData(): GlobalMarketResponse
    @GET("coins/{id}/market_chart")
    suspend fun getCoinMarketChart(
        @Path("id") coinId: String,
        @Query("vs_currency") currency: String = "usd",
        @Query("days") days: Int = 7
    ): CoinMarketChart
}