package com.example.smartfood

import DiseaseManager
import android.content.Context
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit


class NetworkClient() {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .build()
    private val url = "https://f994-193-233-23-217.ngrok-free.app/api/"


    fun getContraindicatedFood(context: Context, sickness: String) {
        val json = JSONObject()
        json.put("sickness", sickness)

        val requestBody =
            json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url + "contraindicatedFood")
            .post(requestBody)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {

                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val responseData = response.body?.string()
                    val jsonObject = JSONObject(responseData!!)

                    val answerString = jsonObject.getString("answer")

                    val cleanAnswerString =
                        answerString.replace("[", "").replace("]", "").replace("'", "\"")
                    val answerArray = JSONArray("[$cleanAnswerString]")

                    val answerList = mutableListOf<String>()
                    for (i in 0 until answerArray.length()) {
                        answerList.add(answerArray.getString(i))
                    }
                    val manager = DiseaseManager(context)
                    manager.addDisease(context, sickness, answerList)
                }

            }

        })

    }

    fun getFood(imageFile: File, context: MainActivity) {
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "image",
                imageFile.name,
                imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            )
            .build()

        val request = Request.Builder()
            .url(url + "food")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val responseData = response.body?.string()
                    context.runOnUiThread {
                        context.binding.productsText.setText(context.binding.productsText.text.toString() + responseData)
                    }
                }
            }
        })
    }

    fun getFinanceReport(id: Int, callback: FinanceCallback) {
        val json = JSONObject()
        json.put("user_id", id)
        json.put("year", 2023)

        val requestBody =
            json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url + "finance")
            .post(requestBody)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {

                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val responseData =
                        response.body?.string() // 1228.0067222222222 || ['Перекрёсток', 'Пятёрочка', 'Дикси']
                    val parts = responseData!!.split(" || ", limit = 2)
                    val number = parts[0].toFloatOrNull()
                    val items = parts[1].split(",").map { it.trim() }
                    callback.onSuccess(number!!, items.joinToString(","))
                }

            }

        })
    }


    fun getDiet(kcal: Float, food: String, money: Float, callback: DietCallback) {
        val json = JSONObject()
        json.put("kcal", kcal)
        json.put("food", food)
        json.put("money", money)

        val requestBody =
            json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url + "diet")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val responseData = response.body?.string()
                    val jsonResponse = JSONArray(responseData!!)
                    callback.onSuccess(jsonResponse)
                }
            }
        })
    }

    fun getShops(address: String, products: String, radius: Int, budget: Float, shops: String) {
        val json = JSONObject()
        json.put("address", address)
        json.put("products", products)
        json.put("radius", radius)
        json.put("budget", budget)
        json.put("shops", shops)

        val requestBody =
            json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url + "find")
            .post(requestBody)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {

                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val responseData = response.body?.string()
                }

            }

        })
    }

}