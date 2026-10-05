package com.example.cryptotracker.ui.screens
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.cryptotracker.data.model.Coin
import com.example.cryptotracker.viewmodel.CoinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt
private val CryptoGreen = Color(0xFF22E681)
private val CryptoGreenDark = Color(0xFF16C784)
private val DarkBackground = Color(0xFF07131A)
private val ChartBackground = Color(0xFF101E27)
private val CardBackground = Color(0xFF1A2934)
private val CardBackgroundLight = Color(0xFF263541)
private val NegativeRed = Color(0xFFFF5C5C)
private val TextSecondary = Color(0xFF9EACB8)
@Composable
fun CoinDetailScreen(
    coin: Coin,
    onBack: () -> Unit,
    viewModel: CoinViewModel = viewModel()
) {
    val chartState by viewModel.chartState.collectAsState()
    val chartLoading by viewModel.chartLoading.collectAsState()
    val chartError by viewModel.chartError.collectAsState()
    val favoriteIds by viewModel.favoriteCoinIds.collectAsState(
        initial = emptySet()
    )
    val isFavorite =
        favoriteIds.contains(coin.id)
    var selectedDays by remember {
        mutableIntStateOf(7)
    }
    LaunchedEffect(
        coin.id,
        selectedDays
    ) {
        viewModel.loadCoinChart(
            coinId = coin.id,
            days = selectedDays
        )
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkBackground
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 18.dp,
                bottom = 40.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                DetailTopBar(
                    coin = coin,
                    isFavorite = isFavorite,
                    onBack = onBack,
                    onFavoriteClick = {
                        viewModel.toggleFavorite(coin.id)
                    }
                )
            }
            item {
                PriceHeader(
                    coin = coin
                )
            }
            item {
                TimeRangeSelector(
                    selectedDays = selectedDays,
                    onDaysSelected = {
                        selectedDays = it
                    }
                )
            }
            item {
                ChartCard(
                    prices = chartState?.prices ?: emptyList(),
                    isLoading = chartLoading,
                    errorMessage = chartError
                )
            }
            item {
                StatisticsSection(
                    coin = coin
                )
            }
            item {
                SupplySection(
                    coin = coin
                )
            }
        }
    }
}
@Composable
private fun DetailTopBar(
    coin: Coin,
    isFavorite: Boolean,
    onBack: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
        AsyncImage(
            model = coin.image,
            contentDescription = coin.name,
            modifier = Modifier
                .size(58.dp)
                .clip(CircleShape)
        )
        Spacer(
            modifier = Modifier.width(14.dp)
        )
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = coin.name,
                color = Color.White,
                fontSize = 26.sp,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = coin.symbol.uppercase(Locale.US),
                color = TextSecondary,
                fontSize = 16.sp
            )
        }
        IconButton(
            onClick = onFavoriteClick,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = if (isFavorite) {
                    Icons.Default.Star
                } else {
                    Icons.Outlined.StarBorder
                },
                contentDescription = "Favorite",
                tint = if (isFavorite) {
                    CryptoGreen
                } else {
                    TextSecondary
                },
                modifier = Modifier.size(30.dp)
            )
        }
    }
}
@Composable
private fun PriceHeader(
    coin: Coin
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 8.dp,
                top = 12.dp
            )
    ) {
        Text(
            text = formatPrice(coin.current_price),
            color = Color.White,
            fontSize = 38.sp
        )
        Spacer(
            modifier = Modifier.height(5.dp)
        )
        val change =
            coin.price_change_percentage_24h
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector =
                    if (change >= 0) {
                        Icons.Default.TrendingUp
                    } else {
                        Icons.Default.TrendingDown
                    },
                contentDescription = null,
                tint =
                    if (change >= 0) {
                        CryptoGreen
                    } else {
                        NegativeRed
                    },
                modifier = Modifier.size(20.dp)
            )
            Spacer(
                modifier = Modifier.width(5.dp)
            )
            Text(
                text =
                    "${formatPercentage(change)} (24h)",
                color =
                    if (change >= 0) {
                        CryptoGreen
                    } else {
                        NegativeRed
                    },
                fontSize = 17.sp
            )
        }
    }
}
@Composable
private fun TimeRangeSelector(
    selectedDays: Int,
    onDaysSelected: (Int) -> Unit
) {
    val ranges = listOf(
        1 to "1D",
        7 to "7D",
        30 to "30D",
        90 to "90D",
        365 to "1Y"
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ranges.forEach { range ->

            val days = range.first
            val label = range.second
            val selected = selectedDays == days

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(
                        RoundedCornerShape(10.dp)
                    )
                    .background(
                        if (selected) {
                            CryptoGreen
                        } else {
                            CardBackgroundLight
                        }
                    )
                    .clickable {
                        onDaysSelected(days)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color =
                        if (selected) {
                            Color(0xFF06110B)
                        } else {
                            TextSecondary
                        },
                    fontSize = 13.sp
                )
            }
        }
    }
}
@Composable
private fun ChartCard(
    prices: List<List<Double>>,
    isLoading: Boolean,
    errorMessage: String?
) {
    var selectedIndex by remember {
        mutableIntStateOf(-1)
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Price Chart",
                    color = Color.White,
                    fontSize = 18.sp
                )
                Spacer(
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(CryptoGreen)
                )
                Spacer(
                    modifier = Modifier.width(6.dp)
                )
                Text(
                    text = "Live Data",
                    color = CryptoGreen,
                    fontSize = 13.sp
                )
            }
            Spacer(
                modifier = Modifier.height(10.dp)
            )
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                            .clip(
                                RoundedCornerShape(18.dp)
                            )
                            .background(
                                ChartBackground
                            ),
                        contentAlignment =
                            Alignment.Center
                    ) {
                        Text(
                            text = "Loading chart...",
                            color = TextSecondary
                        )
                    }
                }
                errorMessage != null &&
                        prices.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                            .clip(
                                RoundedCornerShape(18.dp)
                            )
                            .background(
                                ChartBackground
                            ),
                        contentAlignment =
                            Alignment.Center
                    ) {
                        Text(
                            text = errorMessage,
                            color = NegativeRed
                        )
                    }
                }
                prices.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                            .clip(
                                RoundedCornerShape(18.dp)
                            )
                            .background(
                                ChartBackground
                            ),
                        contentAlignment =
                            Alignment.Center
                    ) {
                        Text(
                            text = "No chart data available",
                            color = TextSecondary
                        )
                    }
                }
                else -> {

                    if (
                        selectedIndex >= prices.size
                    ) {
                        selectedIndex = -1
                    }

                    InteractivePriceChart(
                        prices = prices,
                        selectedIndex = selectedIndex,
                        onSelectedIndexChange = {
                            selectedIndex = it
                        }
                    )
                    if (
                        selectedIndex >= 0 &&
                        selectedIndex < prices.size &&
                        prices[selectedIndex].size >= 2
                    ) {
                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )
                        SelectedPriceInfo(
                            point =
                                prices[selectedIndex]
                        )
                    }
                }
            }
        }
    }
}
@Composable
private fun InteractivePriceChart(
    prices: List<List<Double>>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit
) {
    val validPrices =
        prices.filter {
            it.size >= 2
        }
    if (validPrices.isEmpty()) {
        return
    }
    val priceValues =
        validPrices.map {
            it[1]
        }
    val minPrice =
        priceValues.minOrNull() ?: return
    val maxPrice =
        priceValues.maxOrNull() ?: return
    val priceRange =
        if (maxPrice > minPrice) {
            maxPrice - minPrice
        } else {
            1.0
        }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .clip(
                RoundedCornerShape(18.dp)
            )
            .background(
                ChartBackground
            )
            .pointerInput(validPrices.size) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val width =
                            size.width.toFloat()
                        val index =
                            if (width > 0f) {
                                (
                                        offset.x / width *
                                                (validPrices.size - 1)
                                        )
                                    .roundToInt()
                                    .coerceIn(
                                        0,
                                        validPrices.lastIndex
                                    )
                            } else {
                                0
                            }
                        onSelectedIndexChange(index)
                    },
                    onDrag = { change, _ ->
                        val width =
                            size.width.toFloat()
                        val index =
                            if (width > 0f) {
                                (
                                        change.position.x / width *
                                                (validPrices.size - 1)
                                        )
                                    .roundToInt()
                                    .coerceIn(
                                        0,
                                        validPrices.lastIndex
                                    )
                            } else {
                                0
                            }
                        onSelectedIndexChange(index)
                        change.consume()
                    }
                )
            }
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val chartWidth = size.width
            val chartHeight = size.height
            val leftPadding = 14f
            val rightPadding = 14f
            val topPadding = 16f
            val bottomPadding = 32f
            val drawableWidth =
                chartWidth -
                        leftPadding -
                        rightPadding
            val drawableHeight =
                chartHeight -
                        topPadding -
                        bottomPadding
            for (lineIndex in 0..3) {
                val fraction =
                    lineIndex.toFloat() / 3f
                val y =
                    topPadding +
                            drawableHeight * fraction
                drawLine(
                    color = Color(0x33495A66),
                    start =
                        androidx.compose.ui.geometry
                            .Offset(
                                leftPadding,
                                y
                            ),
                    end =
                        androidx.compose.ui.geometry
                            .Offset(
                                chartWidth -
                                        rightPadding,
                                y
                            ),
                    strokeWidth = 1f
                )
            }
            val path = Path()

            validPrices.forEachIndexed {
                    index,
                    point
                ->

                val price = point[1]

                val normalized =
                    (
                            (price - minPrice) /
                                    priceRange
                            )
                        .coerceIn(
                            0.0,
                            1.0
                        )
                        .toFloat()

                val x =
                    if (validPrices.size == 1) {
                        chartWidth / 2f
                    } else {
                        leftPadding +
                                drawableWidth *
                                (
                                        index.toFloat() /
                                                (
                                                        validPrices.size -
                                                                1
                                                        ).toFloat()
                                        )
                    }

                val y =
                    topPadding +
                            drawableHeight *
                            (
                                    1f -
                                            normalized
                                    )

                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }
            val areaPath = Path()
            validPrices.forEachIndexed {
                    index,
                    point
                ->
                val price = point[1]

                val normalized =
                    (
                            (price - minPrice) /
                                    priceRange
                            )
                        .coerceIn(
                            0.0,
                            1.0
                        )
                        .toFloat()

                val x =
                    if (validPrices.size == 1) {
                        chartWidth / 2f
                    } else {
                        leftPadding +
                                drawableWidth *
                                (
                                        index.toFloat() /
                                                (
                                                        validPrices.size -
                                                                1
                                                        ).toFloat()
                                        )
                    }

                val y =
                    topPadding +
                            drawableHeight *
                            (
                                    1f -
                                            normalized
                                    )

                if (index == 0) {
                    areaPath.moveTo(x, y)
                } else {
                    areaPath.lineTo(x, y)
                }
            }

            areaPath.lineTo(
                chartWidth - rightPadding,
                chartHeight - bottomPadding
            )

            areaPath.lineTo(
                leftPadding,
                chartHeight - bottomPadding
            )

            areaPath.close()

            drawPath(
                path = areaPath,
                brush = Brush.verticalGradient(
                    listOf(
                        CryptoGreen.copy(alpha = 0.32f),
                        CryptoGreen.copy(alpha = 0.02f)
                    )
                )
            )
            drawPath(
                path = path,
                color = CryptoGreen,
                style = Stroke(
                    width = 3f
                )
            )
            if (
                selectedIndex >= 0 &&
                selectedIndex < validPrices.size
            ) {

                val selectedPoint =
                    validPrices[selectedIndex]

                val selectedPrice =
                    selectedPoint[1]

                val normalized =
                    (
                            (selectedPrice - minPrice) /
                                    priceRange
                            )
                        .coerceIn(
                            0.0,
                            1.0
                        )
                        .toFloat()

                val selectedX =
                    if (validPrices.size == 1) {
                        chartWidth / 2f
                    } else {
                        leftPadding +
                                drawableWidth *
                                (
                                        selectedIndex.toFloat() /
                                                (
                                                        validPrices.size -
                                                                1
                                                        ).toFloat()
                                        )
                    }

                val selectedY =
                    topPadding +
                            drawableHeight *
                            (
                                    1f -
                                            normalized
                                    )
                drawLine(
                    color =
                        CryptoGreen.copy(
                            alpha = 0.85f
                        ),
                    start =
                        androidx.compose.ui.geometry
                            .Offset(
                                selectedX,
                                topPadding
                            ),
                    end =
                        androidx.compose.ui.geometry
                            .Offset(
                                selectedX,
                                chartHeight -
                                        bottomPadding
                            ),
                    strokeWidth = 1.5f
                )
                drawCircle(
                    color = ChartBackground,
                    radius = 9f,
                    center =
                        androidx.compose.ui.geometry
                            .Offset(
                                selectedX,
                                selectedY
                            )
                )
                drawCircle(
                    color = CryptoGreen,
                    radius = 5f,
                    center =
                        androidx.compose.ui.geometry
                            .Offset(
                                selectedX,
                                selectedY
                            )
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(
                    start = 14.dp,
                    end = 14.dp,
                    bottom = 7.dp
                ),
            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            listOf(
                "00:00",
                "06:00",
                "12:00",
                "18:00"
            ).forEach { label ->

                Text(
                    text = label,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}
@Composable
private fun SelectedPriceInfo(
    point: List<Double>
) {
    if (point.size < 2) {
        return
    }

    val timestamp = point[0]
    val price = point[1]

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(16.dp)
            )
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF102A20),
                        Color(0xFF0D211B)
                    )
                )
            )
            .padding(
                horizontal = 16.dp,
                vertical = 14.dp
            )
    ) {

        Text(
            text = "Selected Price",
            color = TextSecondary,
            fontSize = 12.sp
        )

        Spacer(
            modifier = Modifier.height(3.dp)
        )

        Text(
            text = formatPrice(price),
            color = CryptoGreen,
            fontSize = 25.sp
        )

        Spacer(
            modifier = Modifier.height(2.dp)
        )

        Text(
            text = formatTimestamp(timestamp),
            color = TextSecondary,
            fontSize = 12.sp
        )
    }
}
@Composable
private fun StatisticsSection(
    coin: Coin
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = "Market Statistics",
            color = Color.White,
            fontSize = 22.sp
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        StatisticsCard(
            icon = Icons.Default.BarChart,
            iconColor = TextSecondary,
            title = "Market Cap",
            value =
                formatLargeNumber(
                    coin.market_cap
                )
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        StatisticsCard(
            icon = Icons.Default.BarChart,
            iconColor = TextSecondary,
            title = "24h Volume",
            value =
                formatLargeNumber(
                    coin.total_volume
                )
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        StatisticsCard(
            icon = Icons.Default.EmojiEvents,
            iconColor = TextSecondary,
            title = "Market Rank",
            value =
                coin.market_cap_rank?.let {
                    "#$it"
                } ?: "N/A"
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        StatisticsCard(
            icon = Icons.Default.TrendingUp,
            iconColor = CryptoGreen,
            title = "24h High",
            value =
                formatPrice(
                    coin.high_24h
                )
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        StatisticsCard(
            icon = Icons.Default.TrendingDown,
            iconColor = NegativeRed,
            title = "24h Low",
            value =
                formatPrice(
                    coin.low_24h
                )
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        StatisticsCard(
            icon = Icons.Default.TrendingUp,
            iconColor = CryptoGreen,
            title = "All Time High",
            value =
                formatPrice(
                    coin.ath
                )
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        StatisticsCard(
            icon = Icons.Default.TrendingDown,
            iconColor = NegativeRed,
            title = "ATH Change",
            value =
                formatPercentage(
                    coin.ath_change_percentage
                )
        )
    }
}
@Composable
private fun SupplySection(
    coin: Coin
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = "Supply",
            color = Color.White,
            fontSize = 22.sp
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        StatisticsCard(
            icon = Icons.Default.BarChart,
            iconColor = TextSecondary,
            title = "Circulating Supply",
            value =
                formatSupply(
                    coin.circulating_supply
                )
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        StatisticsCard(
            icon = Icons.Default.BarChart,
            iconColor = TextSecondary,
            title = "Total Supply",
            value =
                coin.total_supply?.let {
                    formatSupply(it)
                } ?: "Unlimited / N/A"
        )
    }
}
@Composable
private fun StatisticsCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    title: String,
    value: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 14.dp,
                    vertical = 14.dp
                ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(25.dp)
            )

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Text(
                text = title,
                color = TextSecondary,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = value,
                color = Color.White,
                fontSize = 15.sp
            )
        }
    }
}
private fun formatPrice(
    value: Double
): String {

    return when {

        value >= 1_000_000 -> {
            "$" + String.format(
                Locale.US,
                "%,.2f",
                value
            )
        }

        value >= 1.0 -> {
            "$" + String.format(
                Locale.US,
                "%,.2f",
                value
            )
        }

        value >= 0.01 -> {
            "$" + String.format(
                Locale.US,
                "%.4f",
                value
            )
        }

        else -> {
            "$" + String.format(
                Locale.US,
                "%.8f",
                value
            )
        }
    }
}

private fun formatPercentage(
    value: Double
): String {

    val sign =
        if (value >= 0.0) {
            "+"
        } else {
            ""
        }

    return sign + String.format(
        Locale.US,
        "%.2f%%",
        value
    )
}

private fun formatLargeNumber(
    value: Double
): String {

    return when {

        value >= 1_000_000_000_000 -> {
            "$" + String.format(
                Locale.US,
                "%.2fT",
                value / 1_000_000_000_000
            )
        }

        value >= 1_000_000_000 -> {
            "$" + String.format(
                Locale.US,
                "%.2fB",
                value / 1_000_000_000
            )
        }

        value >= 1_000_000 -> {
            "$" + String.format(
                Locale.US,
                "%.2fM",
                value / 1_000_000
            )
        }

        value >= 1_000 -> {
            "$" + String.format(
                Locale.US,
                "%.2fK",
                value / 1_000
            )
        }

        else -> {
            "$" + String.format(
                Locale.US,
                "%.2f",
                value
            )
        }
    }
}

private fun formatSupply(
    value: Double
): String {
    return when {

        value >= 1_000_000_000_000 -> {
            String.format(
                Locale.US,
                "%.2fT",
                value / 1_000_000_000_000
            )
        }

        value >= 1_000_000_000 -> {
            String.format(
                Locale.US,
                "%.2fB",
                value / 1_000_000_000
            )
        }

        value >= 1_000_000 -> {
            String.format(
                Locale.US,
                "%.2fM",
                value / 1_000_000
            )
        }

        value >= 1_000 -> {
            String.format(
                Locale.US,
                "%.2fK",
                value / 1_000
            )
        }

        else -> {
            String.format(
                Locale.US,
                "%.2f",
                value
            )
        }
    }
}

private fun formatTimestamp(
    timestamp: Double
): String {

    val formatter =
        SimpleDateFormat(
            "dd MMM, HH:mm",
            Locale.US
        )

    return formatter.format(
        Date(timestamp.toLong())
    )
}