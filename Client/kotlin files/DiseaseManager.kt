import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

data class Disease(val name: String, val contraindicatedFoods: List<String>)


class DiseaseManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("HealthData", Context.MODE_PRIVATE)
    private val gson = Gson()


    fun getDiseases(context: Context): List<String> {
        val diseasesJson = sharedPreferences.getString("Diseases", "{}") ?: "{}"
        val diseasesMap = Gson().fromJson(diseasesJson, Map::class.java) as Map<String, List<String>>

        return diseasesMap.keys.toList()
    }

    fun getContraindicatedFoods(context: Context,): List<String>? {
        val diseasesJson = sharedPreferences.getString("Diseases", "{}") ?: "{}"
        val diseasesMap = Gson().fromJson(diseasesJson, Map::class.java) as Map<String, List<String>>

        val uniqueFoods = mutableSetOf<String>()

        for (foods in diseasesMap.values) {
            uniqueFoods.addAll(foods)
        }

        return uniqueFoods.toList()
    }


    fun addDisease(context: Context, disease: String, contraindicatedFoods: List<String>) {
        val editor = sharedPreferences.edit()

        val diseasesJson = sharedPreferences.getString("Diseases", "{}") ?: "{}"
        val diseasesMap = Gson().fromJson(diseasesJson, Map::class.java) as MutableMap<String, List<String>>

        diseasesMap[disease] = contraindicatedFoods

        editor.putString("Diseases", Gson().toJson(diseasesMap))
        editor.apply()
    }

    fun removeDisease(context: Context, disease: String) {
        val editor = sharedPreferences.edit()

        val diseasesJson = sharedPreferences.getString("Diseases", "{}") ?: "{}"
        val diseasesMap = Gson().fromJson(diseasesJson, Map::class.java) as MutableMap<String, List<String>>

        diseasesMap.remove(disease)

        editor.putString("Diseases", Gson().toJson(diseasesMap))
        editor.apply()
    }


}
