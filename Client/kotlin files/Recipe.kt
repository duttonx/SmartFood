package com.example.smartfood

data class Recipe(
    val day: Int,
    val name: String,
    val ingredients: List<String>,
    val recipe: String
)
