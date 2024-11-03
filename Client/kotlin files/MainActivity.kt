package com.example.smartfood

import DiseaseManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.example.smartfood.databinding.ActivityMainBinding
import org.json.JSONArray
import java.io.File
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.random.Random


class MainActivity : BaseActivity() {
    lateinit var binding: ActivityMainBinding
    private val networkClient: NetworkClient = NetworkClient()
    private lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        baseBinding.title.text = "Главная"
    }

    override fun loadContent() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        val contentLayout = baseBinding.content
        contentLayout.addView(binding.root)

        pref = getSharedPreferences("BankUser", Context.MODE_PRIVATE)

        val manager = DiseaseManager(this)
        val context = this;
        binding.photoButton.setOnClickListener {
            selectImageInAlbum()
        }
        binding.generateButton.setOnClickListener {
            networkClient.getFinanceReport(pref.getInt("id", -1), object : FinanceCallback {
                override fun onSuccess(money: Float, shops: String) {
                    val startSeparator = View(context).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            15
                        )
                        setBackgroundColor(Color.parseColor("#D3D3D3"))
                    }
                    context.runOnUiThread {
                        context.binding.content.addView(startSeparator)
                    }
                    val food = manager.getContraindicatedFoods(context)
                    pref = getSharedPreferences("UserData", Context.MODE_PRIVATE)
                    networkClient.getDiet(
                        pref.getFloat("bmr", 2500.0f),
                        food.toString(),
                        money,
                        object : DietCallback {
                            override fun onSuccess(jsonResponse: JSONArray) {
                                val ingredientsByDayWithoutGrams =
                                    mutableMapOf<Int, MutableSet<String>>()
                                mutableMapOf<Int, MutableSet<String>>()
                                val ingredientRegex =
                                    Regex("^([\\D]+)\\s([\\d.]+\\s?[гмлкГМЛК]*)?$")
                                val str = jsonResponse.toString()
                                pref = getSharedPreferences("MainPage", Context.MODE_PRIVATE)
                                val editor = pref.edit()
                                editor.putString("diet", str)
                                editor.apply()
                                val recipesByDay = parseRecipes(str)
                                for ((day, recipes) in recipesByDay) {
                                    val ingredientsWithoutGrams = mutableSetOf<String>()
                                    val titleView = TextView(context).apply {
                                        text = "День " + day
                                        textSize = 25f
                                        setTextColor(Color.BLACK)
                                    }
                                    context.runOnUiThread {
                                        context.binding.content.addView(titleView)
                                    }
                                    for (recipe in recipes) {
                                        for (ingredient in recipe.ingredients) {
                                            val matchResult = ingredientRegex.find(ingredient)

                                            if (matchResult != null) {
                                                val (name, grams) = matchResult.destructured
                                                ingredientsWithoutGrams.add(name.trim())
                                            } else {
                                                ingredientsWithoutGrams.add(ingredient.trim())
                                            }
                                        }
                                        context.runOnUiThread {
                                            addRecipe(
                                                recipe.name,
                                                recipe.ingredients.toString(),
                                                recipe.recipe
                                            )
                                        }
                                    }
                                    val separator = View(context).apply {
                                        layoutParams = LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            10
                                        )
                                        setBackgroundColor(Color.parseColor("#D3D3D3"))
                                    }
                                    context.runOnUiThread {
                                        context.binding.content.addView(separator)
                                    }
                                    ingredientsByDayWithoutGrams[day] = ingredientsWithoutGrams
                                }
                                val stores = getStores()
                                val titleView = TextView(context).apply {
                                    text = "Магазины для покупки продуктов: $stores"
                                    textSize = 15f
                                    setTextColor(Color.BLACK)
                                }
                                context.runOnUiThread {
                                    context.binding.content.addView(titleView)
                                }
                            }
                        })
                }
            })
        }
    }

    fun selectImageInAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM && resultCode == RESULT_OK) {
            val imageUri: Uri? = data?.data
            if (imageUri != null) {
                val imageFile = createFileFromUri(imageUri)
                if (imageFile != null) {
                    networkClient.getFood(imageFile, this)
                }
            }
        }
    }

    fun createFileFromUri(uri: Uri): File? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val tempFile = File.createTempFile("temp_image", ".jpg", cacheDir)
            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun takePhoto() {
        val intent1 = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent1.resolveActivity(packageManager) != null) {
            startActivityForResult(intent1, REQUEST_TAKE_PHOTO)
        }
    }

    companion object {
        private val REQUEST_TAKE_PHOTO = 0
        private val REQUEST_SELECT_IMAGE_IN_ALBUM = 1
    }

    private fun addRecipe(title: String, ingredients: String, instructions: String) {

        val recipeContainer = binding.content

        val titleView = TextView(this).apply {
            text = title
            textSize = 20f
            setTextColor(Color.BLACK)
        }
        recipeContainer.addView(titleView)

        val ingredientsView = TextView(this).apply {
            text = "Ингредиенты: $ingredients"
            setTextColor(Color.BLACK)
        }
        recipeContainer.addView(ingredientsView)

        val instructionsView = TextView(this).apply {
            text = "Рецепт: $instructions"
            setTextColor(Color.BLACK)
        }
        recipeContainer.addView(instructionsView)
    }

    fun parseRecipes(jsonData: String): Map<Int, List<Recipe>> {
        val gson = Gson()
        val recipeListType = object : TypeToken<List<Recipe>>() {}.type
        val recipes: List<Recipe> = gson.fromJson(jsonData, recipeListType)

        return recipes.groupBy { it.day }
    }

    fun getStores(): List<String> {
        val stores = listOf(
            "Пятёрочка",
            "Магнит",
            "Лента",
            "Карусель",
            "Дикси",
            "Ашан",
            "Перекресток",
            "Семья",
            "Красное и Белое",
            "Гипермаркет Седьмой Континент"
        )

        return stores.shuffled(Random).take(3)
    }

}