package com.example.smartfood

import DiseaseManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import com.example.smartfood.databinding.ActivitySicknessBinding

class SicknessActivity: BaseActivity() {
    private lateinit var binding: ActivitySicknessBinding
    private val networkClient: NetworkClient = NetworkClient()
    private lateinit var manager: DiseaseManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        baseBinding.title.text = "Заболевания"
    }
    override fun loadContent() {
        binding = ActivitySicknessBinding.inflate(layoutInflater)
        val contentLayout = baseBinding.content
        contentLayout.addView(binding.root)
        manager = DiseaseManager(this)
        val diseases = manager.getDiseases(this)
        for(x in diseases)
            editSickness(x)

        binding.add.setOnClickListener{
           editSickness()
        }
    }

    private fun editSickness(inp: String? = null) {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val editText = EditText(this).apply {
            hint = "Введите текст"
            setText(inp)
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0.8f
            )
        }

        val checkButton = Button(this).apply {
            text = "✔️"
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0.1f)
            setOnClickListener {
                val enteredText = editText.text.toString()
                networkClient.getContraindicatedFood(container.context,enteredText)

            }
        }

        val deleteButton = Button(this).apply {
            text = "❌"
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0.1f)
            setOnClickListener {
                manager.removeDisease(container.context, editText.text.toString())
                binding.content.removeView(container)
            }

        }

        container.addView(editText)
        container.addView(checkButton)
        container.addView(deleteButton)

        binding.content.addView(container)
    }
}