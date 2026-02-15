package com.example.week5.data.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("location")
    val location: Location,

    @SerializedName("current")
    val current: Current
)

/**
 * Location information
 */
data class Location(
    @SerializedName("name")
    val name: String,           // City name: "London"

    @SerializedName("region")
    val region: String,         // Region/State: "City of London, Greater London"

    @SerializedName("country")
    val country: String,        // Country: "United Kingdom"

    @SerializedName("localtime")
    val localtime: String       // Local date and time: "2024-01-15 14:30"
)

/**
 * Current weather data
 */
data class Current(
    @SerializedName("temp_c")
    val tempC: Double,          // Temperature in Celsius: 8.0

    @SerializedName("temp_f")
    val tempF: Double,          // Temperature in Fahrenheit: 46.4

    @SerializedName("condition")
    val condition: Condition,   // Weather condition

    @SerializedName("wind_kph")
    val windKph: Double,        // Wind speed in km/h: 20.5

    @SerializedName("humidity")
    val humidity: Int,          // Humidity percentage: 72

    @SerializedName("feelslike_c")
    val feelslikeC: Double,     // Feels like temperature in Celsius: 5.0

    @SerializedName("feelslike_f")
    val feelslikeF: Double      // Feels like temperature in Fahrenheit: 41.0
)

/**
 * Weather condition information
 */
data class Condition(
    @SerializedName("text")
    val text: String,           // Condition text: "Partly cloudy"

    @SerializedName("icon")
    val icon: String,           // Icon URL: "//cdn.weatherapi.com/weather/64x64/day/116.png"

    @SerializedName("code")
    val code: Int               // Condition code: 1003
)