package com.example.smartfood

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import com.example.smartfood.databinding.ActivityFinanceBinding

class FinanceActivity : BaseActivity() {

    lateinit var binding: ActivityFinanceBinding
    private lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        baseBinding.title.text = "Финансовая информация"
    }

    override fun loadContent() {
        binding = ActivityFinanceBinding.inflate(layoutInflater)
        val contentLayout = baseBinding.content
        contentLayout.addView(binding.root)

        pref  = getSharedPreferences("BankUser", Context.MODE_PRIVATE)

        val id = pref.getInt("id", -1)
        if (id != -1)
            binding.idText.setText(id.toString())

        binding.saveButton.setOnClickListener {
            val editor = pref.edit()
            editor.putInt("id", binding.idText.text.toString().toInt())
            editor.apply()
        }

    }

}