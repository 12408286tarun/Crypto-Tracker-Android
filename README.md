# 📱 Crypto Tracker Android App

A modern Android cryptocurrency tracking application built with **Kotlin and Jetpack Compose**.  
The app fetches real-time cryptocurrency market data using the **CoinGecko API** and provides users with coin prices, market information, price changes, detailed market charts, and favorite coin functionality.

## 📌 Project Overview

Crypto Tracker is an Android application designed to provide users with an easy and clean interface for monitoring cryptocurrency market data.

The application follows the **MVVM (Model-View-ViewModel)** architecture and uses modern Android development technologies such as:

- Kotlin
- Jetpack Compose
- Retrofit
- CoinGecko API
- StateFlow
- DataStore
- Coroutines

The project is structured to keep the UI, business logic, data access, and local storage separated and maintainable.

## ✨ Features

- 📊 Display cryptocurrency market data
- 💰 Show current cryptocurrency prices
- 📈 Display 24-hour price changes
- 📉 View cryptocurrency market charts
- 🌎 Display global cryptocurrency market information
- ❤️ Add/remove coins from favorites
- 💾 Store favorite coins locally using DataStore
- 🔄 Fetch cryptocurrency data from CoinGecko API
- ⚡ Reactive UI updates using StateFlow
- 🎨 Modern UI using Jetpack Compose
- 🏗️ Clean MVVM-based project structure

---

## 🛠️ Tech Stack

| Technology | Usage |
|---|---|
| Kotlin | Primary programming language |
| Jetpack Compose | UI development |
| MVVM | Application architecture |
| Retrofit | REST API communication |
| CoinGecko API | Cryptocurrency market data |
| Kotlin Coroutines | Asynchronous operations |
| StateFlow | Reactive state management |
| DataStore | Local favorite data storage |
| Gson | JSON serialization/deserialization |
| Android Studio | Development environment |
| Gradle Kotlin DSL | Build configuration |

---

# 🏗️ Architecture — MVVM

The application follows the **Model-View-ViewModel (MVVM)** architecture.

                    ┌─────────────────────┐
                    │     Jetpack         │
                    │      Compose        │
                    │        UI           │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │     ViewModel       │
                    │   CoinViewModel     │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │     Repository      │
                    │   CoinRepository    │
                    └──────────┬──────────┘
                               │
                  ┌────────────┴────────────┐
                  ▼                         ▼
        ┌──────────────────┐      ┌──────────────────┐
        │   Retrofit API   │      │     DataStore    │
        │   CoinGecko API  │      │    Favorites     │
        └──────────────────┘      └──────────────────┘

  # PROJECT STRUCTURE

com.example.cryptotracker
│
├── data
│   ├── local
│   │   └── FavoriteDataStore.kt
│   │
│   ├── model
│   │   ├── Coin.kt
│   │   ├── CoinMarketChart.kt
│   │   └── GlobalMarketData.kt
│   │
│   ├── remote
│   │   ├── CoinGeckoApi.kt
│   │   └── RetrofitInstance.kt
│   │
│   └── repository
│       └── CoinRepository.kt
│
├── ui
│   ├── screens
│   │   ├── HomeScreen.kt
│   │   └── CoinDetailScreen.kt
│   │
│   └── theme
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
│
├── viewmodel
│   ├── CoinUiState.kt
│   └── CoinViewModel.kt
│
└── MainActivity.kt

# 🌐 Retrofit + CoinGecko API

The application uses **Retrofit** to communicate with the CoinGecko REST API.

The API integration is implemented through:

- `CoinGeckoApi.kt`
- `RetrofitInstance.kt`
- `CoinRepository.kt`

The application fetches:

- Cryptocurrency market data
- Global market data
- Individual coin market chart data

Data flow:

API → Retrofit → Repository → ViewModel → StateFlow → Compose UI

# 🔄 StateFlow

The application uses **StateFlow** for reactive UI state management.

`CoinViewModel` manages the application state and exposes it through `StateFlow`.

When the data changes, the Jetpack Compose UI automatically reacts to the updated state.

API
 ↓
Repository
 ↓
ViewModel
 ↓
StateFlow
 ↓
Compose

# ❤️ DataStore / Favorites

The application uses **DataStore** for storing favorite cryptocurrency information locally.

The implementation is handled by:

`FavoriteDataStore.kt`

This allows users to save and manage their favorite cryptocurrencies locally.

# 📱 Screenshots

### Home Screen
![Home Screen 1](https://github.com/user-attachments/assets/f66a5202-e459-455b-b07e-bf077315205f)

![Home Screen 1](https://github.com/user-attachments/assets/2f0d89a6-e9f8-42c8-a59b-85f37b21aad2)

![Home Screen 2](https://github.com/user-attachments/assets/97c764b5-7c14-4662-90bd-6fba38b9c907)

![Home Screen 3](https://github.com/user-attachments/assets/e4cc65e9-89f0-426f-80c4-2a59df6335be)

### Coin Detail Screen

![Coin Detail Screen 1](https://github.com/user-attachments/assets/10231667-728e-44db-ac6b-8b78da6a100e)

![Coin Detail Screen 2](https://github.com/user-attachments/assets/b4691579-ff0a-43d0-9e30-95ca98016c12)

![Coin Detail Screen 3](https://github.com/user-attachments/assets/17820010-5644-449f-9ff9-c67c984d353b)



---

# 🚀 How to Run

### Prerequisites

- Android Studio
- JDK 17 or compatible version
- Android SDK
- Internet connection

### Steps

1. Clone the repository:

```bash
git clone https://github.com/12408286tarun/Crypto-Tracker-Android.git
```

2. Open the project in Android Studio.

3. Allow Gradle to sync and download the required dependencies.

4. Connect an Android device or start an Android Emulator.

5. Make sure the device has an active internet connection.

6. Click **Run ▶** in Android Studio.

The application will build and launch on the connected Android device or emulator.


# 🔐 API Configuration

This project uses the **CoinGecko API** for cryptocurrency market data.

The current implementation uses the CoinGecko API base URL:

'''https://api.coingecko.com/api/v3/'''
No API key is required for the endpoints currently used by this project.

# 📚 Learning & Implementation

This project was built to practice and demonstrate:

- Modern Android development
- Kotlin programming
- Jetpack Compose UI
- MVVM architecture
- REST API integration
- Retrofit
- Kotlin Coroutines
- StateFlow
- Repository pattern
- DataStore
- Local persistence
- Reactive UI development

# 👨‍💻 Author

**Tarun Joshi**

B.Tech Computer Science Engineering

GitHub: [12408286tarun](https://github.com/12408286tarun)
