package com.example.week5.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.week5.BuildConfig
import com.example.week5.data.model.WeatherResponse
import com.example.week5.data.remote.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val weatherData: WeatherResponse? = null,
        val errorMessage: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())

    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var currentCity: String = ""


    fun updateCity(city: String) {
        currentCity = city
    }

    fun fetchWeather() {
        if (currentCity.isBlank()) {
            _uiState.value = UiState(
                errorMessage = "Please enter a city name"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = UiState(isLoading = true)

            try {
                val response = RetrofitInstance.api.getCurrentWeather(
                    city = currentCity,
                    apiKey = BuildConfig.API_KEY
                )

                _uiState.value = UiState(
                    weatherData = response,
                    isLoading = false
                )

            } catch (e: Exception) {
                // Show detailed error message
                _uiState.value = UiState(
                    errorMessage = "Error: ${e.javaClass.simpleName}: ${e.message}",
                    isLoading = false
                )
            }
        }
    }
}