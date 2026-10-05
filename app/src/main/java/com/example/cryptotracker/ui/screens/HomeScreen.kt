package com.example.cryptotracker.ui.screens
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.cryptotracker.data.model.Coin
import com.example.cryptotracker.data.model.GlobalMarketData
import com.example.cryptotracker.viewmodel.CoinUiState
import com.example.cryptotracker.viewmodel.CoinViewModel
import java.util.Locale
import kotlin.math.abs

private val BackgroundColor = Color(0xFF0B0F14)
private val CardColor = Color(0xFF151B23)
private val SecondaryCardColor = Color(0xFF111720)
private val BorderColor = Color(0xFF252E38)
private val PrimaryGreen = Color(0xFF4CAF50)
private val NegativeRed = Color(0xFFF44336)
private val FavoriteYellow = Color(0xFFFFC107)
private val PrimaryText = Color.White
private val SecondaryText = Color(0xFF8B95A1)

@Composable
fun HomeScreen(
    viewModel: CoinViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val favoriteCoinIds by viewModel.favoriteCoinIds.collectAsState()

    var searchQuery by remember {
        mutableStateOf("")
    }

    var showFavoritesOnly by remember {
        mutableStateOf(false)
    }

    var selectedCoin by remember {
        mutableStateOf<Coin?>(null)
    }

    LaunchedEffect(Unit) {
        viewModel.loadCoins()
    }

    if (selectedCoin != null) {

        BackHandler {
            selectedCoin = null
        }

        CoinDetailScreen(
            coin = selectedCoin!!,
            onBack = {
                selectedCoin = null
            }
        )

        return
    }

    when (val state = uiState) {

        is CoinUiState.Loading -> {
            LoadingScreen()
        }

        is CoinUiState.Error -> {
            ErrorScreen(
                message = state.message,
                onRetry = {
                    viewModel.loadCoins()
                }
            )
        }

        is CoinUiState.Success -> {

            val filteredCoins =
                state.coins.filter { coin ->

                    val matchesSearch =
                        coin.name.contains(
                            searchQuery,
                            ignoreCase = true
                        ) ||
                                coin.symbol.contains(
                                    searchQuery,
                                    ignoreCase = true
                                )

                    val matchesFavorite =
                        !showFavoritesOnly ||
                                favoriteCoinIds.contains(coin.id)

                    matchesSearch && matchesFavorite
                }

            DashboardScreen(
                coins = filteredCoins,
                marketData = state.marketData,
                searchQuery = searchQuery,
                showFavoritesOnly = showFavoritesOnly,
                favoriteCoinIds = favoriteCoinIds,
                onSearchChange = {
                    searchQuery = it
                },
                onFavoriteClick = {
                    viewModel.toggleFavorite(it)
                },
                onFavoritesFilterClick = {
                    showFavoritesOnly = it
                },
                onRefresh = {
                    viewModel.loadCoins(
                        forceRefresh = true
                    )
                },
                onCoinClick = { coin ->
                    selectedCoin = coin
                }
            )
        }
    }
}

@Composable
private fun LoadingScreen() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            CircularProgressIndicator(
                modifier = Modifier.size(44.dp),
                color = PrimaryGreen,
                strokeWidth = 4.dp
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Text(
                text = "Loading market data",
                color = PrimaryText,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Fetching the latest crypto prices...",
                color = SecondaryText,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun ErrorScreen(
    message: String,
    onRetry: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(
                        RoundedCornerShape(20.dp)
                    )
                    .background(CardColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "!",
                    color = NegativeRed,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Text(
                text = "Unable to load market",
                color = PrimaryText,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = message,
                color = SecondaryText,
                fontSize = 14.sp
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            TextButton(
                onClick = onRetry
            ) {
                Text(
                    text = "Try Again",
                    color = PrimaryGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun DashboardScreen(
    coins: List<Coin>,
    marketData: GlobalMarketData?,
    searchQuery: String,
    showFavoritesOnly: Boolean,
    favoriteCoinIds: Set<String>,
    onSearchChange: (String) -> Unit,
    onFavoriteClick: (String) -> Unit,
    onFavoritesFilterClick: (Boolean) -> Unit,
    onRefresh: () -> Unit,
    onCoinClick: (Coin) -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor),

        contentPadding = PaddingValues(
            start = 16.dp,
            top = 42.dp,
            end = 16.dp,
            bottom = 28.dp
        ),

        verticalArrangement =
            Arrangement.spacedBy(14.dp)
    ) {
        item {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Crypto Tracker",
                        color = PrimaryText,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )

                    Text(
                        text = "Live cryptocurrency market",
                        color = SecondaryText,
                        fontSize = 14.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(
                            RoundedCornerShape(14.dp)
                        )
                        .background(CardColor)
                        .clickable {
                            onRefresh()
                        },
                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text = "↻",
                        color = PrimaryText,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        item {

            MarketOverviewCard(
                marketData = marketData
            )
        }
        item {

            SearchBar(
                query = searchQuery,
                onQueryChange = onSearchChange
            )
        }
        item {

            FilterRow(
                showFavoritesOnly =
                    showFavoritesOnly,
                onFavoritesFilterClick =
                    onFavoritesFilterClick
            )
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text =
                            if (showFavoritesOnly) {
                                "Your Watchlist"
                            } else {
                                "Top Cryptocurrencies"
                            },
                        color = PrimaryText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Text(
                        text =
                            if (showFavoritesOnly) {
                                "Your saved assets"
                            } else {
                                "Market ranked by capitalization"
                            },
                        color = SecondaryText,
                        fontSize = 12.sp
                    )
                }

                Text(
                    text = "${coins.size} assets",
                    color = SecondaryText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        if (coins.isEmpty()) {

            item {

                EmptyState(
                    showFavoritesOnly =
                        showFavoritesOnly
                )
            }

        } else {
            items(
                items = coins,
                key = { coin ->
                    coin.id
                }
            ) { coin ->

                CoinCard(
                    coin = coin,
                    isFavorite =
                        favoriteCoinIds.contains(coin.id),
                    onClick = {
                        onCoinClick(coin)
                    },
                    onFavoriteClick = {
                        onFavoriteClick(coin.id)
                    }
                )
            }
        }
    }
}

@Composable
private fun FilterRow(
    showFavoritesOnly: Boolean,
    onFavoritesFilterClick: (Boolean) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(14.dp)
            )
            .background(SecondaryCardColor)
            .padding(4.dp),
        horizontalArrangement =
            Arrangement.spacedBy(4.dp)
    ) {

        FilterButton(
            text = "ALL",
            selected = !showFavoritesOnly,
            onClick = {
                onFavoritesFilterClick(false)
            },
            modifier = Modifier.weight(1f)
        )

        FilterButton(
            text = "★ FAVORITES",
            selected = showFavoritesOnly,
            onClick = {
                onFavoritesFilterClick(true)
            },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun FilterButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier
) {

    Box(
        modifier = modifier
            .clip(
                RoundedCornerShape(11.dp)
            )
            .background(
                if (selected) {
                    CardColor
                } else {
                    Color.Transparent
                }
            )
            .clickable {
                onClick()
            }
            .padding(vertical = 11.dp),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = text,
            color =
                if (selected) {
                    if (text.contains("FAVORITES")) {
                        FavoriteYellow
                    } else {
                        PrimaryGreen
                    }
                } else {
                    SecondaryText
                },
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun MarketOverviewCard(
    marketData: GlobalMarketData?
) {

    val marketCap =
        marketData
            ?.total_market_cap
            ?.get("usd")
            ?: 0.0

    val change =
        marketData
            ?.market_cap_change_percentage_24h_usd
            ?: 0.0

    val changeColor =
        if (change >= 0) {
            PrimaryGreen
        } else {
            NegativeRed
        }

    val changeSymbol =
        if (change >= 0) {
            "↗"
        } else {
            "↘"
        }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(22.dp)
            )
            .background(CardColor)
            .padding(20.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment =
                Alignment.Top
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "TOTAL MARKET CAP",
                    color = SecondaryText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = formatMarketCap(marketCap),
                    color = PrimaryText,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(12.dp)
                    )
                    .background(
                        changeColor.copy(alpha = 0.12f)
                    )
                    .padding(
                        horizontal = 10.dp,
                        vertical = 7.dp
                    )
            ) {

                Text(
                    text =
                        "$changeSymbol ${
                            String.format(
                                Locale.US,
                                "%.2f",
                                abs(change)
                            )
                        }%",
                    color = changeColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "24h market movement",
            color = SecondaryText,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(17.dp),

        placeholder = {
            Text(
                text = "Search cryptocurrencies...",
                color = SecondaryText
            )
        },

        leadingIcon = {
            Text(
                text = "⌕",
                color = SecondaryText,
                fontSize = 23.sp
            )
        }
    )
}

@Composable
private fun EmptyState(
    showFavoritesOnly: Boolean
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(20.dp)
            )
            .background(CardColor)
            .padding(
                horizontal = 20.dp,
                vertical = 42.dp
            ),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text =
                    if (showFavoritesOnly) {
                        "☆"
                    } else {
                        "⌕"
                    },
                color =
                    if (showFavoritesOnly) {
                        FavoriteYellow
                    } else {
                        SecondaryText
                    },
                fontSize = 38.sp
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text =
                    if (showFavoritesOnly) {
                        "No favorites yet"
                    } else {
                        "No cryptocurrency found"
                    },
                color = PrimaryText,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text =
                    if (showFavoritesOnly) {
                        "Tap the star on a coin to add it here."
                    } else {
                        "Try searching with another name or symbol."
                    },
                color = SecondaryText,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun CoinCard(
    coin: Coin,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {

    val isPositive =
        coin.price_change_percentage_24h >= 0

    val changeColor =
        if (isPositive) {
            PrimaryGreen
        } else {
            NegativeRed
        }

    val changeSymbol =
        if (isPositive) {
            "▲"
        } else {
            "▼"
        }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(19.dp)
            )
            .background(CardColor)
            .clickable {
                onClick()
            }
            .padding(15.dp),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        AsyncImage(
            model = coin.image,
            contentDescription = coin.name,
            modifier = Modifier
                .size(46.dp)
                .clip(
                    RoundedCornerShape(23.dp)
                )
        )

        Spacer(
            modifier = Modifier.width(13.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = coin.name,
                color = PrimaryText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text = coin.symbol.uppercase(Locale.US),
                color = SecondaryText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )

            if (coin.market_cap_rank != null) {

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text = "#${coin.market_cap_rank} market rank",
                    color = SecondaryText,
                    fontSize = 10.sp
                )
            }
        }

        Column(
            horizontalAlignment =
                Alignment.End
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text = formatPrice(
                        coin.current_price
                    ),
                    color = PrimaryText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(
                            RoundedCornerShape(10.dp)
                        )
                        .background(
                            if (isFavorite) {
                                FavoriteYellow.copy(
                                    alpha = 0.12f
                                )
                            } else {
                                Color(0xFF202833)
                            }
                        )
                        .clickable {
                            onFavoriteClick()
                        },
                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text =
                            if (isFavorite) {
                                "★"
                            } else {
                                "☆"
                            },
                        color =
                            if (isFavorite) {
                                FavoriteYellow
                            } else {
                                SecondaryText
                            },
                        fontSize = 21.sp
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text =
                    "$changeSymbol ${
                        String.format(
                            Locale.US,
                            "%.2f",
                            abs(
                                coin.price_change_percentage_24h
                            )
                        )
                    }%",
                color = changeColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

private fun formatPrice(
    price: Double
): String {

    return when {

        price >= 1.0 -> {
            "$" + String.format(
                Locale.US,
                "%,.2f",
                price
            )
        }

        price >= 0.01 -> {
            "$" + String.format(
                Locale.US,
                "%.4f",
                price
            )
        }

        else -> {
            "$" + String.format(
                Locale.US,
                "%.6f",
                price
            )
        }
    }
}

private fun formatMarketCap(
    marketCap: Double
): String {

    return when {

        marketCap >= 1_000_000_000_000 -> {
            "$" + String.format(
                Locale.US,
                "%.2fT",
                marketCap / 1_000_000_000_000
            )
        }

        marketCap >= 1_000_000_000 -> {
            "$" + String.format(
                Locale.US,
                "%.2fB",
                marketCap / 1_000_000_000
            )
        }

        marketCap >= 1_000_000 -> {
            "$" + String.format(
                Locale.US,
                "%.2fM",
                marketCap / 1_000_000
            )
        }

        else -> {
            "$" + String.format(
                Locale.US,
                "%,.0f",
                marketCap
            )
        }
    }
}