package com.example.smartfood

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import com.example.smartfood.databinding.ActivityBaseBinding

abstract class BaseActivity : AppCompatActivity() {
    protected lateinit var baseBinding: ActivityBaseBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseBinding = ActivityBaseBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(baseBinding.root)
        if (savedInstanceState == null) {
            loadContent()
        }
        baseBinding.menuButton.setOnClickListener {
            baseBinding.drawer.openDrawer(GravityCompat.START)
        }

        baseBinding.drawerContent.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.mainItem -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                R.id.sicknessItem -> {
                    val intent = Intent(this, SicknessActivity::class.java)
                    startActivity(intent)
                }
                R.id.bankItem ->{
                    val intent = Intent(this, FinanceActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }
    }

    protected abstract fun loadContent()
}