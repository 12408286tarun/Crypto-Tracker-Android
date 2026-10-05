package com.example.cryptotracker.viewmodel
import com.example.cryptotracker.data.model.Coin
import com.example.cryptotracker.data.model.GlobalMarketData
sealed interface CoinUiState {
    data object Loading : CoinUiState
    data class Success(
        val coins: List<Coin>,
        val marketData: GlobalMarketData?
    ) : CoinUiState
    data class Error(
        val message: String
    ) : CoinUiState
}