package com.example.stopwatch.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import com.example.stopwatch.ApiInterface
import com.example.stopwatch.WeatherApp
import com.example.stopwatch.databinding.FragmentWeatherBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale

class WeatherFragment : Fragment() {

    private lateinit var binding : FragmentWeatherBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWeatherBinding.inflate(inflater,container,false)

        fetchWeatherData("Patna")
        searchCity()


        return binding.root
    }

    private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object  : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName : String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val response = retrofit.getWeatherData(cityName,"1","metric")
        response.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null){
                    Log.d("WeatherResponse", "Response: ${responseBody}")

                    val temperature = Math.round(responseBody.main.temp).toString()
                    val humidity = responseBody.main.humidity.toString()
                    val windspeed = responseBody.wind.speed.toString()
                    val sunrise = responseBody.sys.sunrise.toString()
                    val sunset = responseBody.sys.sunset.toString()
                    val sealevel = responseBody.main.pressure.toString()
                    val mintemp = responseBody.main.temp_min.toString()
                    val maxtemp = responseBody.main.temp_max.toString()
                    val condition = responseBody.weather.firstOrNull()?.main?: "unknown"

                    binding.temp.text = temperature
                    binding.humidity.text = "$humidity %"
                    binding.sealevel.text = "$sealevel hPa"
                    binding.mintemp.text = "Min temp  : $mintemp °C"
                    binding.maxtemp.text =  "Max temp : $maxtemp °C"
                    binding.sunset.text = sunset
                    binding.tvweather.text = condition
                    binding.sunrise.text = sunrise
                    binding.windspeed.text = "$windspeed m/s"
                    binding.conditions.text = condition
                    binding.cityname.text = "$cityName"
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

    }

}