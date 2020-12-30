package com.utb.myweatherapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import android.widget.RadioButton as RadioButton

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val sharedPreferences : SharedPreferences = getSharedPreferences("SETUP_SHARED", Context.MODE_PRIVATE)
        val city : String? = sharedPreferences.getString( "CITY", null )
        val units : String? = sharedPreferences.getString( "UNITS", null )

        findViewById<EditText>(R.id.edcity).setText(city)

        if (units == "imperial") {
            findViewById<RadioButton>(R.id.radioimperial).isChecked = true
        }

        val btnSave : Button = findViewById(R.id.bSave)

        btnSave.setOnClickListener {

            var sunits : String = "imperial"
            if (findViewById<RadioButton>(R.id.radiometric).isChecked) {
                sunits = "metric"
            }
            val editor = sharedPreferences.edit()
            editor.putString("CITY", findViewById<EditText>(R.id.edcity).text.toString())
            editor.putString("UNITS", sunits)
            editor.apply()

            val intent = Intent(this, MainActivity :: class.java)
            startActivity(intent)
        }


    }

}
