package com.example.smartfood

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.smartfood.databinding.ActivityFirstloginBinding

class FirstLoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityFirstloginBinding
    private lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirstloginBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(binding.root)

        pref = getSharedPreferences("UserData", Context.MODE_PRIVATE)

        binding.registerButton.setOnClickListener {
            val editor = pref.edit()
            val height = binding.userHeight.text.toString().toInt()
            val weight = binding.userWeight.text.toString().toInt()
            val age = binding.userAge.text.toString().toInt()
            val sex = binding.sex.selectedItem.toString()
            val activityLevel = binding.activityLevel.selectedItem.toString()

            val dHeight = height.toFloat() / 100
            val bmi = weight / (dHeight * dHeight)

            var physique = "Мезоморф"

            var bmr = 0.0f
            bmr = if (sex == "Мужчина")
                88.362f + (13.397f * weight) + (4.799f * height) - (5.677f * age)
            else
                447.593f + (9.247f * weight) + (3.098f * height) - (4.330f * age)

            when (activityLevel) {
                "Сидячий образ жизни" -> bmr *= 1.2f
                "Умеренная активность" -> bmr *= 1.375f
                "Высокая активность" -> bmr *= 1.55f
                "Очень высокая активность" -> bmr *= 1.725f
            }

            if (bmi < 18.5) {
                physique = "Эктоморф"
                bmr *= 1.1f
            } else if (bmi > 25 && bmi < 30) {
                physique = "Эндоморф"
                bmr *= 0.95f
            }
            else if(bmi >= 30)
            {
                physique = "Эндоморф"
                bmr *= 0.85f
            }

            editor.putInt("height", height)
            editor.putInt("weight", weight)
            editor.putFloat("bmi", bmi)
            editor.putString("physique", physique)
            editor.putInt("age", age)
            editor.putString("sex", sex)
            editor.putString("activityLevel", activityLevel)
            editor.putFloat("bmr", bmr)

            editor.apply()


            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}