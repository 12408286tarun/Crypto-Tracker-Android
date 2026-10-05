package com.example.cryptotracker.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
private val Context.favoriteDataStore by preferencesDataStore(
    name = "favorite_coins"
)
class FavoriteDataStore(
    appContext: Context
) {
    private val context = appContext.applicationContext
    private object PreferencesKeys {
        val FAVORITE_COIN_IDS =
            stringSetPreferencesKey("favorite_coin_ids")
    }
    val favoriteCoinIds: Flow<Set<String>> =
        context.favoriteDataStore.data.map { preferences ->

            preferences[PreferencesKeys.FAVORITE_COIN_IDS]
                ?: emptySet()
        }
    suspend fun saveFavoriteCoinIds(
        favoriteIds: Set<String>
    ) {
        context.favoriteDataStore.edit { preferences ->
            preferences[PreferencesKeys.FAVORITE_COIN_IDS] =
                favoriteIds
        }
    }
}