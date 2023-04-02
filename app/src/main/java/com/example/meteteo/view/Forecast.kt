package com.example.meteteo.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.meteteo.R
import com.example.meteteo.retrofit.RetrofitBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Forecast : AppCompatActivity() {
    @SuppressLint("SetTextI18n", "DiscouragedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)

        val button = findViewById<Button>(R.id.button)

        button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val retrofitBuilder = RetrofitBuilder.apiService
        CoroutineScope(Dispatchers.IO).launch {
            val responseWeather = retrofitBuilder.getWeather()
            withContext(Dispatchers.Main) {
                try {
                    if (responseWeather.isSuccessful) {
                        println("CALL API : ${responseWeather.body()}")

                        // Affichage de la bonne image selon le weather code

                        val currentWeatherCode = responseWeather.body()?.daily?.weatherCode?.get(0)
                        val currentImage = findViewById<ImageView>(resources.getIdentifier("image7", "id", packageName))
                        val currentImageResource = when (currentWeatherCode) {
                            0 -> R.drawable.sun
                            in 1..3 -> R.drawable.clear_sky
                            in 45..48 -> R.drawable.brouillard
                            in 51..67 -> R.drawable.rain
                            in 71..77 -> R.drawable.flocon_de_neige
                            in 80..86 -> R.drawable.rain
                            in 95..99 -> R.drawable.cloud_rain_and_lightning
                            else -> R.drawable.clear_sky
                        }
                        currentImage.setImageResource(currentImageResource)

                        // Affichage de la température actuelle
                        findViewById<TextView>(R.id.current_degree).text = "${responseWeather.body()?.current_weather?.temperature}°"

                        // Affichage de la précipitation
                        for (i in 0..23) {
                            val precipitation = findViewById<TextView>(resources.getIdentifier("precipitation", "id", packageName))
                            precipitation.text = "${responseWeather.body()?.hourly?.precipitationProbability?.get(i)}%"
                        }

                        // Affichage de l'humidité
                        for (i in 0..23) {
                            val humidity = findViewById<TextView>(resources.getIdentifier("humidity", "id", packageName))
                            humidity.text = "${responseWeather.body()?.hourly?.relativeHumidity?.get(i)}%"
                        }

                        findViewById<TextView>(R.id.wind_speed).text = "${responseWeather.body()?.current_weather?.windspeed} km/h"

                        // Affichage de la bonne image selon le weather code
                        for (i in 0..6) {
                            val dailyWeatherCode = responseWeather.body()?.daily?.weatherCode?.get(i)
                            val dailyImage = findViewById<ImageView>(resources.getIdentifier("image$i", "id", packageName))
                            val dailyImageResource = when (dailyWeatherCode) {
                                0 -> R.drawable.sun
                                in 1..3 -> R.drawable.clear_sky
                                in 45..48 -> R.drawable.brouillard
                                in 51..67 -> R.drawable.rain
                                in 71..77 -> R.drawable.flocon_de_neige
                                in 80..86 -> R.drawable.rain
                                in 95..99 -> R.drawable.cloud_rain_and_lightning
                                else -> R.drawable.clear_sky
                            }
                            dailyImage.setImageResource(dailyImageResource)
                        }

                        // Affichage de la température max de la semaine
                        for (i in 0..6) {
                            val tempMax = findViewById<TextView>(resources.getIdentifier("tempMax$i", "id", packageName))
                            tempMax.text = "${responseWeather.body()?.daily?.maxTemperature?.get(i)}°"
                        }

                        // Affichage de la température min de la semaine
                        for (i in 0..6) {
                            val tempMin = findViewById<TextView>(resources.getIdentifier("tempMin$i", "id", packageName))
                            tempMin.text = "${responseWeather.body()?.daily?.minTemperature?.get(i)}°"
                        }

                    } else {
                        Toast.makeText(baseContext, "Error: ${responseWeather.code()}", Toast.LENGTH_LONG).show()
                    }
                } catch (e: HttpException) {
                    Toast.makeText(baseContext, "Exception: ${e.message()}", Toast.LENGTH_LONG).show()
                } catch (e: Throwable) {
                    Toast.makeText(baseContext, "Ooops: Something else went wrong", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}