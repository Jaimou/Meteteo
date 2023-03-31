package com.example.meteteo.repository

import androidx.annotation.WorkerThread
import com.example.meteteo.dao.WeatherCityDao
import com.example.meteteo.entity.WeatherCityEntity
import kotlinx.coroutines.flow.Flow

class WeatherCityRepository(private val wordDao: WeatherCityDao) {
    val allCities: Flow<List<WeatherCityEntity>> = wordDao.getWeatherCities()

    @WorkerThread
    suspend fun insert(city: WeatherCityEntity) {
        wordDao.insert(city)
    }
}