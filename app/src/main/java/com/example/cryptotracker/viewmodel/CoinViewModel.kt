package com.example.cryptotracker.viewmodel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptotracker.data.local.FavoriteDataStore
import com.example.cryptotracker.data.model.Coin
import com.example.cryptotracker.data.model.CoinMarketChart
import com.example.cryptotracker.data.model.GlobalMarketData
import com.example.cryptotracker.data.remote.RetrofitInstance
import com.example.cryptotracker.data.repository.CoinRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
class CoinViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val repository =
        CoinRepository(RetrofitInstance.api)
    private val favoriteDataStore =
        FavoriteDataStore(application)
    private val _uiState =
        MutableStateFlow<CoinUiState>(CoinUiState.Loading)
    val uiState: StateFlow<CoinUiState> =
        _uiState
    private var cachedCoins: List<Coin>? = null
    private var coinsFetchedAt = 0L
    private var cachedMarketData: GlobalMarketData? = null
    private var marketDataFetchedAt = 0L
    private val cacheDuration =
        10 * 60 * 1000L
    private val minimumRefreshInterval =
        30 * 1000L
    private var lastCoinsRequestAt = 0L
    private val _chartState =
        MutableStateFlow<CoinMarketChart?>(null)
    val chartState: StateFlow<CoinMarketChart?> =
        _chartState
    private val _chartLoading =
        MutableStateFlow(false)
    val chartLoading: StateFlow<Boolean> =
        _chartLoading
    private val _chartError =
        MutableStateFlow<String?>(null)

    val chartError: StateFlow<String?> =
        _chartError
    private data class ChartCacheEntry(
        val chart: CoinMarketChart,
        val fetchedAt: Long
    )

    private val chartCache =
        mutableMapOf<String, ChartCacheEntry>()
    private val _favoriteCoinIds =
        MutableStateFlow<Set<String>>(emptySet())

    val favoriteCoinIds: StateFlow<Set<String>> =
        _favoriteCoinIds

    init {

        viewModelScope.launch {

            favoriteDataStore.favoriteCoinIds.collect { ids ->

                _favoriteCoinIds.value = ids
            }
        }
    }

    fun toggleFavorite(coinId: String) {

        val currentFavorites =
            _favoriteCoinIds.value.toMutableSet()

        if (currentFavorites.contains(coinId)) {

            currentFavorites.remove(coinId)

        } else {

            currentFavorites.add(coinId)
        }

        val updatedFavorites =
            currentFavorites.toSet()

        _favoriteCoinIds.value =
            updatedFavorites

        viewModelScope.launch {

            favoriteDataStore.saveFavoriteCoinIds(
                updatedFavorites
            )
        }
    }

    fun isFavorite(coinId: String): Boolean {

        return _favoriteCoinIds.value.contains(coinId)
    }
    fun loadCoins(
        forceRefresh: Boolean = false
    ) {
        viewModelScope.launch {

            val currentTime =
                System.currentTimeMillis()
            if (
                forceRefresh &&
                currentTime - lastCoinsRequestAt <
                minimumRefreshInterval &&
                cachedCoins != null
            ) {
                _uiState.value =
                    CoinUiState.Success(
                        coins = cachedCoins!!,
                        marketData = cachedMarketData
                    )
                return@launch
            }
            if (
                !forceRefresh &&
                cachedCoins != null &&
                currentTime - coinsFetchedAt <
                cacheDuration
            ) {
                _uiState.value =
                    CoinUiState.Success(
                        coins = cachedCoins!!,
                        marketData = cachedMarketData
                    )
                return@launch
            }
            _uiState.value =
                CoinUiState.Loading

            lastCoinsRequestAt =
                currentTime

            try {
                val coins =
                    repository.getCoins()
                cachedCoins =
                    coins
                coinsFetchedAt =
                    currentTime
                val marketData =
                    if (
                        cachedMarketData != null &&
                        currentTime - marketDataFetchedAt <
                        cacheDuration
                    ) {
                        cachedMarketData

                    } else {

                        try {

                            val freshMarketData =
                                repository.getGlobalMarketData()

                            cachedMarketData =
                                freshMarketData

                            marketDataFetchedAt =
                                currentTime

                            freshMarketData

                        } catch (e: Exception) {

                            cachedMarketData
                        }
                    }

                _uiState.value =
                    CoinUiState.Success(
                        coins = coins,
                        marketData = marketData
                    )

            } catch (e: HttpException) {

                val message =
                    if (e.code() == 429) {

                        "CoinGecko rate limit reached. Please wait a little and try again."

                    } else {

                        "Server error: HTTP ${e.code()}"
                    }

                // If network fails but cached data exists,
                // continue showing the cached data.
                if (cachedCoins != null) {

                    _uiState.value =
                        CoinUiState.Success(
                            coins = cachedCoins!!,
                            marketData = cachedMarketData
                        )

                } else {

                    _uiState.value =
                        CoinUiState.Error(message)
                }

            } catch (e: Exception) {

                if (cachedCoins != null) {

                    _uiState.value =
                        CoinUiState.Success(
                            coins = cachedCoins!!,
                            marketData = cachedMarketData
                        )

                } else {

                    _uiState.value =
                        CoinUiState.Error(
                            e.message
                                ?: "Something went wrong"
                        )
                }
            }
        }
    }
    fun loadCoinChart(
        coinId: String,
        days: Int = 7
    ) {
        viewModelScope.launch {
            _chartError.value = null
            val currentTime =
                System.currentTimeMillis()
            val cacheKey =
                "$coinId-$days"
            val cachedChart =
                chartCache[cacheKey]
            if (
                cachedChart != null &&
                currentTime - cachedChart.fetchedAt <
                cacheDuration
            ) {
                _chartState.value =
                    cachedChart.chart

                _chartLoading.value =
                    false
                return@launch
            }
            _chartLoading.value = true
            try {
                val chart =
                    repository.getCoinMarketChart(
                        coinId = coinId,
                        days = days
                    )
                chartCache[cacheKey] =
                    ChartCacheEntry(
                        chart = chart,
                        fetchedAt = currentTime
                    )
                _chartState.value =
                    chart
            } catch (e: HttpException) {
                _chartError.value =
                    if (e.code() == 429) {

                        "Too many requests. Please try again later."

                    } else {

                        "Server error: HTTP ${e.code()}"
                    }
                if (cachedChart != null) {

                    _chartState.value =
                        cachedChart.chart
                }

            } catch (e: Exception) {

                _chartError.value =
                    e.message
                        ?: "Unable to load chart data."

                if (cachedChart != null) {

                    _chartState.value =
                        cachedChart.chart
                }

            } finally {

                _chartLoading.value =
                    false
            }
        }
    }
    fun clearChart() {

        _chartState.value =
            null

        _chartError.value =
            null

        _chartLoading.value =
            false
    }
}