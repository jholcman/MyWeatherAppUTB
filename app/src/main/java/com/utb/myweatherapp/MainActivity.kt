package com.utb.myweatherapp

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.utb.myweatherapp.databinding.ActivityMainBinding
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt
import android.content.Intent
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    var city: String? = null
    var units: String? = null
    val api: String = "45dca48978390897290109479536333a" // My API key OpenWeatherMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        WeatherTask().execute()
    }

    @SuppressLint("StaticFieldLeak")
    inner class WeatherTask() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()

            val binding =  ActivityMainBinding.inflate (layoutInflater)
            setContentView (binding.root)

            binding.loader.visibility = View.VISIBLE
            binding.mainContainer.visibility = View.GONE
            binding.errorText.visibility = View.GONE
        }

        override fun doInBackground(vararg params: String?): String? {
            val sharedPreferences : SharedPreferences = getSharedPreferences("SETUP_SHARED", MODE_PRIVATE)
            city = sharedPreferences.getString( "CITY", null )
            units = sharedPreferences.getString( "UNITS", null )

            val editor = sharedPreferences.edit()

            if (city == null) {
                editor.putString("CITY", "Brno")
                editor.apply()
            }
            if (units == null) {
                editor.putString("UNITS", "metric")
                editor.apply()
            }

            var response:String?
            try{
                response = URL("https://api.openweathermap.org/data/2.5/weather?q=$city&units=$units&appid=$api").readText(Charsets.UTF_8)
            }catch (e: Exception){
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            val binding =  ActivityMainBinding.inflate (layoutInflater)
            setContentView (binding.root)

            try {
                var teplota : String = "°F"
                var vitr : String = "Mph"
                if (units == "metric") {
                    teplota = "°C"
                    vitr = "m/s"
                }

                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                val address = jsonObj.getString("name")+", "+sys.getString("country")
                val updatedAt:Long = jsonObj.getLong("dt")
                val updatedAtText = "Last update: "+ SimpleDateFormat("d.W.yyyy kk:mm", Locale.ENGLISH).format(Date(updatedAt*1000))

                val weatherDescription = weather.getString("description")
                val temp = main.getString("temp").toFloat().roundToInt().toString()
                val tempMin = "Min Temp: " + main.getString("temp_min").toFloat().roundToInt().toString() + teplota
                val tempMax = "Max Temp: " + main.getString("temp_max").toFloat().roundToInt().toString() + teplota

                val sunrise:Long = sys.getLong("sunrise")
                val sunset:Long = sys.getLong("sunset")
                val windSpeed = wind.getString("speed") + vitr
                val pressure = main.getString("pressure")+" hPa"
                val humidity = main.getString("humidity")+" %"
                val feel = main.getString("feels_like").toFloat().roundToInt().toString() + teplota


                binding.address.text = address
                binding.updatedat.text =  updatedAtText
                binding.status.text = weatherDescription.capitalize()
                binding.temp.text = temp
                binding.unit.text = teplota
                binding.tempmin.text = tempMin
                binding.tempmax.text = tempMax
                binding.sunrise.text = SimpleDateFormat("k:mm", Locale.ENGLISH).format(Date(sunrise*1000))
                binding.sunset.text = SimpleDateFormat("k:mm", Locale.ENGLISH).format(Date(sunset*1000))
                binding.wind.text = windSpeed
                binding.pressure.text = pressure
                binding.humidity.text = humidity
                binding.feel.text = feel

                binding.loader.visibility = View.GONE
                binding.mainContainer.visibility = View.VISIBLE


            } catch (e: Exception) {

                binding.mainContainer.visibility = View.GONE
                binding.loader.visibility = View.GONE
                binding.errorText.visibility = View.VISIBLE
            }

        }
    }

    fun secondActivity(view: View) {
        val intent = Intent(this, SecondActivity :: class.java)
        startActivity(intent);
    }
}
