package com.example.smartfood

import org.json.JSONArray
import org.json.JSONObject

interface DietCallback {
    fun onSuccess(jsonResponse: JSONArray)
}