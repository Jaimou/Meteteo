package com.example.meteteo.view

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.lifecycle.ViewModelProvider
import com.example.meteteo.R
import com.example.meteteo.database.CityRoomDatabase
import com.example.meteteo.viewModel.WeatherViewModel
import com.example.meteteo.databinding.ActivityMainBinding
import com.example.meteteo.entity.WeatherCityEntity
import com.example.meteteo.repository.WeatherCityRepository
import com.example.meteteo.retrofit.RetrofitBuilder
import kotlinx.coroutines.*
import retrofit2.HttpException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var weatherViewModel: WeatherViewModel

    @SuppressLint("ShowToast", "SetTextI18n", "DiscouragedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)

        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]

        // Tout le code en dessous, ne doit pas exister dans le MainActivity, il doit être présent dans le ViewModel
        // Database Room
        val applicationScope = CoroutineScope(SupervisorJob())
        val database = CityRoomDatabase.getDatabase(this, applicationScope)
        val repository = WeatherCityRepository(database.cityDao())
        applicationScope.launch(Dispatchers.IO) {
            repository.insert(WeatherCityEntity("Test"))
        }

        // Retrofit Create instance and Get Request

        // Il faut utiliser un ViewModel pour faire le call api
        val retrofitBuilder = RetrofitBuilder.apiService
        CoroutineScope(Dispatchers.IO).launch {
            val responseWeather = retrofitBuilder.getWeather()
            withContext(Dispatchers.Main) {
                try {
                    if (responseWeather.isSuccessful) {
                        println("CALL API : ${responseWeather.body()}")
                        // À changer car ça affiche le timezone et non la ville
                        findViewById<TextView>(R.id.city_name).text = "${responseWeather.body()?.timezone}"
                        // Il manque le weather code pour afficher l'image
                        findViewById<TextView>(R.id.current_degree).text = "${responseWeather.body()?.current_weather?.temperature}°"

                        val inputPattern = "yyyy-MM-dd'T'HH:mm"
                        val outputPattern = "dd/MM/yyyy"
                        val inputFormatter = DateTimeFormatter.ofPattern(inputPattern)
                        val outputFormatter = DateTimeFormatter.ofPattern(outputPattern)
                        val inputDate = LocalDateTime.parse(responseWeather.body()?.current_weather?.time, inputFormatter)
                        val outputDate = outputFormatter.format(inputDate)
                        findViewById<TextView>(R.id.current_date).text = outputDate

                        for (i in 0..23) {
                            val precipitation = findViewById<TextView>(resources.getIdentifier("precipitation", "id", packageName))
                            precipitation.text = "${responseWeather.body()?.hourly?.precipitationProbability?.get(i)}%"
                        }

                        for (i in 0..23) {
                            val humidity = findViewById<TextView>(resources.getIdentifier("humidity", "id", packageName))
                            humidity.text = "${responseWeather.body()?.hourly?.relativeHumidity?.get(i)}%"
                        }

                        findViewById<TextView>(R.id.wind_speed).text = "${responseWeather.body()?.current_weather?.windspeed} km/h"

                        // Affichage de la bonne image selon le weather code
                        for (i in 0..24) {
                            val weatherCode = responseWeather.body()?.hourly?.weatherCode?.get(i)
                            val image = findViewById<ImageView>(resources.getIdentifier("image$i", "id", packageName))
                            val imageResource = when (weatherCode) {
                                0 -> R.drawable.sun
                                in 1..3 -> R.drawable.clear_sky
                                in 45..48 -> R.drawable.brouillard
                                in 51..67 -> R.drawable.rain
                                in 71..77 -> R.drawable.flocon_de_neige
                                in 80..86 -> R.drawable.rain
                                in 95..99 -> R.drawable.cloud_rain_and_lightning
                                else -> R.drawable.clear_sky
                            }
                            image.setImageResource(imageResource)
                        }

                        // Affichage de la température de la journée
                        for (i in 0..23) {
                            val temp = findViewById<TextView>(resources.getIdentifier("temp$i", "id", packageName))
                            temp.text = "${responseWeather.body()?.hourly?.temperature?.get(i)}°"
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