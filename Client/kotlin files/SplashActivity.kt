package com.example.smartfood

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import com.example.smartfood.databinding.ActivityBaseBinding
import com.example.smartfood.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
     lateinit var baseBinding: ActivitySplashBinding
    private lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseBinding = ActivitySplashBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(baseBinding.root)

        pref  = getSharedPreferences("UserData", Context.MODE_PRIVATE)

        Handler().postDelayed({
            val bmi = pref.getFloat("bmi", -1.0f)
            if(bmi == -1.0f){
                val intent = Intent(this, FirstLoginActivity::class.java)
                startActivity(intent)
            }
            else{
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        },1200)
    }
}