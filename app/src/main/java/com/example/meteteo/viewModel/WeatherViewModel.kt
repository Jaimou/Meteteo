package com.example.meteteo.viewModel

import androidx.lifecycle.ViewModel
import com.example.meteteo.model.Weather

class WeatherViewModel: ViewModel() {
    var text: String = "Vos coordonnées GPS sont : "

    fun setWeatherText(weather: Weather): String {
        return "$text ${weather.latitude} / ${weather.longitude}"
    }
}