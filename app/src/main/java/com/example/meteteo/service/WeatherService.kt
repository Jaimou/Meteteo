package com.example.meteteo.service

import com.example.meteteo.model.Weather
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    // Changer la requête pour récupérer les données que vous souhaitez intégrer dans votre UI
    @GET("/v1/forecast")
    suspend fun getWeather(
        @Query("longitude") longitude: Float = -0.57F,
        @Query("latitude") latitude: Float = 44.86F,
        @Query("timezone") timezone: String = "Europe/Berlin",
        @Query("current_weather") current_weather: String = "true",
        @Query("hourly") hourly: String = "temperature_2m,precipitation_probability,weathercode,relativehumidity_2m",
        @Query("daily") daily: String = "weathercode,temperature_2m_max,temperature_2m_min"
    ): Response<Weather>
}