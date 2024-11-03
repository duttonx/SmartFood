package com.example.smartfood

import org.json.JSONArray

interface FinanceCallback {
    fun onSuccess(money: Float, shops: String)
}