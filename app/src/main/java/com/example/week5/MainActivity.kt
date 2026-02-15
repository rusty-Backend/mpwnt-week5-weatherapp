package com.example.week5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.week5.ui.WeatherScreen
import com.example.week5.ui.theme.Week5Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Week5Theme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    WeatherScreen()
                }
            }
        }
    }
}