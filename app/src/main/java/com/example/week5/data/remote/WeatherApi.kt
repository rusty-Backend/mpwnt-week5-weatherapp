package com.example.week5.data.remote

import com.example.week5.data.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("current.json")
    suspend fun getCurrentWeather(
        @Query("q") city: String,
        @Query("key") apiKey: String
    ): WeatherResponse
}