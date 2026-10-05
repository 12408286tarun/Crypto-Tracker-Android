package com.example.cryptotracker.data.model
data class GlobalMarketResponse(
    val data: GlobalMarketData
)
data class GlobalMarketData(
    val total_market_cap: Map<String, Double>,
    val market_cap_change_percentage_24h_usd: Double
)