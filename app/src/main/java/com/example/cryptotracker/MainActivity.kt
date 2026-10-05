package com.example.cryptotracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.cryptotracker.ui.screens.HomeScreen
import com.example.cryptotracker.ui.theme.CryptoTrackerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            CryptoTrackerTheme {
                HomeScreen()
            }
        }
    }
}