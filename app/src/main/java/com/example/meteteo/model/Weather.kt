package com.example.meteteo.model

import com.google.gson.annotations.SerializedName

data class Weather(
    val latitude: Float,
    val longitude: Float,
    val elevation: Float,
    val timezone: String,
    @SerializedName("timezone_abbreviation") val timeZoneAbbreviation: String,
    val current_weather: CurrentWeatherData,
    val hourly: HourlyData,
    val daily: DailyData
)

data class CurrentWeatherData(
    @SerializedName("time") val time: String,
    @SerializedName("temperature") val temperature: Float,
    @SerializedName("windspeed") val windspeed: Float
)

data class HourlyData(
    @SerializedName("time") val timestamp: List<String>,
    @SerializedName("temperature_2m") val temperature: List<Float>,
    @SerializedName("precipitation_probability") val precipitationProbability: List<Int>,
    @SerializedName("weathercode") val weatherCode: List<Int>,
    @SerializedName("relativehumidity_2m") val relativeHumidity: List<Int>,
)

data class DailyData(
    @SerializedName("weathercode") val weatherCode: List<Int>,
    @SerializedName("temperature_2m_max") val maxTemperature: List<Float>,
    @SerializedName("temperature_2m_min") val minTemperature: List<Float>,
    @SerializedName("apparent_temperature_max") val maxApparentTemperature: List<Float>,
    @SerializedName("apparent_temperature_min") val minApparentTemperature: List<Float>
)