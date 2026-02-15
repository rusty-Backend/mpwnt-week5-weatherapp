package com.example.week5.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.week5.ViewModel.WeatherViewModel

/**
 * Main weather screen composable
 *
 * This is the UI that users see and interact with
 */
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = viewModel()
) {
    // Collect the UI state from ViewModel
    // collectAsState() makes Compose watch the StateFlow
    // Whenever uiState changes, this Composable will recompose (update)
    val uiState by viewModel.uiState.collectAsState()

    // Remember the text field value
    // This survives recomposition (when UI updates)
    var cityInput by remember { mutableStateOf("") }

    // Main container
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        // Title
        Text(
            text = "Sääsovellus",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // City input field
        OutlinedTextField(
            value = cityInput,
            onValueChange = { newValue ->
                cityInput = newValue
                viewModel.updateCity(newValue)
            },
            label = { Text("Kaupunki") },
            placeholder = { Text("Esim. Helsinki") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Search button
        Button(
            onClick = { viewModel.fetchWeather() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Hae sää")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Show different UI based on state
        when {
            // State 1: Loading
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(50.dp)
                )
            }

            // State 2: Error
            uiState.errorMessage != null -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = uiState.errorMessage ?: "Unknown error",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // State 3: Success - show weather data
            uiState.weatherData != null -> {
                WeatherResultSection(
                    weatherResponse = uiState.weatherData!!
                )
            }
        }
    }
}

/**
 * Composable that displays the weather results
 *
 * Separated into its own function to keep code organized
 */
@Composable
fun WeatherResultSection(
    weatherResponse: com.example.week5.data.model.WeatherResponse
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Location name
            Text(
                text = weatherResponse.location.name,
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "${weatherResponse.location.region}, ${weatherResponse.location.country}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Temperature
            Text(
                text = "${weatherResponse.current.tempC}°C",
                style = MaterialTheme.typography.displayLarge
            )

            // Condition
            Text(
                text = weatherResponse.current.condition.text,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Additional details
            Divider()

            Spacer(modifier = Modifier.height(16.dp))

            // Feels like temperature
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Tuntuu kuin:")
                Text("${weatherResponse.current.feelslikeC}°C")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Humidity
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Kosteus:")
                Text("${weatherResponse.current.humidity}%")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Wind speed
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Tuuli:")
                Text("${weatherResponse.current.windKph} km/h")
            }
        }
    }
}