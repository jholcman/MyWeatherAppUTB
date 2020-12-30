package com.utb.myweatherapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {

    var city: String? = null
    var units: String? = null


    //var CITY: String = "Praha"
    val API: String = "45dca48978390897290109479536333a" // Use API key

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSetup : Button = findViewById(R.id.bSetup)
        btnSetup.setOnClickListener {
            val intent = Intent(this, SecondActivity :: class.java)
            startActivity(intent)
        }

        WeatherTask().execute()


    }



    @SuppressLint("StaticFieldLeak")
    inner class WeatherTask() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errorText).visibility = View.GONE
        }

        override fun doInBackground(vararg params: String?): String? {
            val sharedPreferences : SharedPreferences = getSharedPreferences("SETUP_SHARED", Context.MODE_PRIVATE)
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
                response = URL("https://api.openweathermap.org/data/2.5/weather?q=$city&units=$units&appid=$API").readText(
                    Charsets.UTF_8
                )
            }catch (e: Exception){
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
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


                findViewById<TextView>(R.id.address).text = address
                findViewById<TextView>(R.id.updated_at).text =  updatedAtText
                findViewById<TextView>(R.id.status).text = weatherDescription.capitalize()
                findViewById<TextView>(R.id.temp).text = temp
                findViewById<TextView>(R.id.unit).text = teplota
                findViewById<TextView>(R.id.temp_min).text = tempMin
                findViewById<TextView>(R.id.temp_max).text = tempMax
                findViewById<TextView>(R.id.sunrise).text = SimpleDateFormat("k:mm", Locale.ENGLISH).format(Date(sunrise*1000))
                findViewById<TextView>(R.id.sunset).text = SimpleDateFormat("k:mm", Locale.ENGLISH).format(Date(sunset*1000))
                findViewById<TextView>(R.id.wind).text = windSpeed
                findViewById<TextView>(R.id.pressure).text = pressure
                findViewById<TextView>(R.id.humidity).text = humidity
                findViewById<TextView>(R.id.feel).text = feel

                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE

            } catch (e: Exception) {
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
            }

        }
    }
}
