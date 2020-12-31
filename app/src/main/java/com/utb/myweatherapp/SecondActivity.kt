package com.utb.myweatherapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.utb.myweatherapp.databinding.ActivitySecondBinding
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import android.widget.RadioButton as RadioButton

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_second)

        val binding =  ActivitySecondBinding.inflate (layoutInflater)
        setContentView (binding.root)


        val sharedPreferences : SharedPreferences = getSharedPreferences("SETUP_SHARED", Context.MODE_PRIVATE)
        val city : String? = sharedPreferences.getString( "CITY", null )
        val units : String? = sharedPreferences.getString( "UNITS", null )

        binding.edcity.setText(city)

        if (units == "imperial") {
            binding.radioimperial.isChecked = true
        }

        //val btnSave : Button = findViewById(R.id.bSave)
        val btnSave : Button = binding.bSave

        btnSave.setOnClickListener {

            var sunits : String = "imperial"
            if (binding.radiometric.isChecked) {
                sunits = "metric"
            }
            val editor = sharedPreferences.edit()
            if (binding.edcity.text.toString() == "") {
                editor.putString("CITY", "Brno")
            } else {
                editor.putString("CITY", binding.edcity.text.toString())
            }
            editor.putString("UNITS", sunits)
            editor.apply()

            val intent = Intent(this, MainActivity :: class.java)
            startActivity(intent)
        }


    }

}
