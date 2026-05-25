package com.example.chef_ai_revan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.chef_ai_revan.data.entity.*
import com.example.chef_ai_revan.data.repository.BudgetRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import android.util.Log
import com.example.chef_ai_revan.data.api.*
import org.json.JSONObject

data class GeneratedRecipeMock(
    val name: String,
    val estimatedCost: Double,
    val description: String,
    val ingredients: List<Pair<String, Double>>,  // nama bahan to estimasi harga
    val steps: List<String>                        // langkah-langkah sebagai list
)

class BudgetViewModel(private val repository: BudgetRepository) : ViewModel() {

    // Main Flows
    val budget: StateFlow<Budget?> = repository.budget
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val groceryItems: StateFlow<List<GroceryItem>> = repository.groceryItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteRecipes: StateFlow<List<FavoriteRecipe>> = repository.favoriteRecipes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val weeklyPlan: StateFlow<List<WeeklyPlan>> = repository.weeklyPlan
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // API Key State
    val userApiKey: StateFlow<String?> = repository.userApiKey
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // AI States
    val isGenerating = MutableStateFlow(false)
    val generationProgress = MutableStateFlow("")
    val generatedRecipes = MutableStateFlow<List<GeneratedRecipeMock>>(emptyList())

    // Model yang tersedia untuk key ini (diisi saat saveApiKey)
    val availableModels = MutableStateFlow<List<String>>(emptyList())
    val isDetectingModels = MutableStateFlow(false)
    val detectedModelInfo = MutableStateFlow<String?>(null)

    val showApiSettingsDialog = MutableStateFlow(false)
    val showApiKeyWelcomeDialog = MutableStateFlow(false)

    // Survive Mode & Warnings
    val isSurviveMode: StateFlow<Boolean> = budget.map { b ->
        if (b == null || b.limit == 0.0) false
        else (b.currentBalance / b.limit) < 0.15 || b.currentBalance < 10000.0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isWeeklyBudgetWarning: StateFlow<Boolean> = budget.map { b ->
        if (b == null || b.limit == 0.0) false
        else (b.currentBalance / b.limit) <= 0.20
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        viewModelScope.launch {
            if (repository.budget.first() == null) repository.updateBudget(0.0, 0.0)
            if (repository.weeklyPlan.first().isEmpty()) {
                val days = listOf("SENIN", "SELASA", "RABU", "KAMIS", "JUMAT", "SABTU", "MINGGU")
                repository.initializeWeeklyPlans(days.mapIndexed { i, d -> WeeklyPlan(dayIndex = i, dayName = d) })
            }
            val introSeen = repository.hasSeenApiKeyIntro.first()
            val apiKey = repository.userApiKey.first()
            if (!introSeen && apiKey.isNullOrBlank()) {
                showApiKeyWelcomeDialog.value = true
            }
        }
    }

    fun saveApiKey(key: String) {
        viewModelScope.launch {
            repository.saveUserApiKey(key)
            if (key.isNotBlank()) {
                repository.setApiKeyIntroShown()
                detectAvailableModels(key)
            }
        }
    }

    fun dismissApiKeyWelcome(openSettings: Boolean) {
        viewModelScope.launch {
            repository.setApiKeyIntroShown()
            showApiKeyWelcomeDialog.value = false
            if (openSettings) showApiSettingsDialog.value = true
        }
    }

    fun detectAvailableModels(apiKey: String) {
        viewModelScope.launch {
            isDetectingModels.value = true
            detectedModelInfo.value = "Mendeteksi model yang tersedia..."
            try {
                val response = GeminiClient.instance.listModels(apiKey)
                val models = response.models
                    ?.filter { model ->
                        // hanya model yang support generateContent
                        model.supportedMethods?.contains("generateContent") == true &&
                        // hanya model gemini (bukan embedding, dll)
                        model.name.contains("gemini", ignoreCase = true)
                    }
                    ?.map { it.name.removePrefix("models/") } // "models/gemini-1.5-flash" → "gemini-1.5-flash"
                    ?: emptyList()

                // Urutkan sesuai preferensi
                val sorted = GeminiClient.PREFERRED_MODELS.filter { it in models } +
                    models.filter { it !in GeminiClient.PREFERRED_MODELS }

                availableModels.value = sorted
                detectedModelInfo.value = if (sorted.isEmpty())
                    "Tidak ada model yang tersedia untuk key ini."
                else
                    "Model tersedia: ${sorted.take(3).joinToString(", ")}${if (sorted.size > 3) " +${sorted.size - 3} lainnya" else ""}"

                Log.d("CHEF_AI", "Model tersedia: $sorted")
            } catch (e: Exception) {
                Log.e("CHEF_AI", "ListModels gagal: ${e.message}")
                detectedModelInfo.value = "Gagal deteksi model: ${e.message?.take(60)}"
                availableModels.value = emptyList()
            } finally {
                isDetectingModels.value = false
            }
        }
    }
    fun updateBudgetLimit(limit: Double) { viewModelScope.launch { repository.updateBudget(limit, budget.value?.currentBalance ?: limit) } }
    fun addExpense(amount: Double) { viewModelScope.launch { budget.value?.let { repository.updateBalance(it.currentBalance - amount) } } }
    fun addGroceryItem(name: String, cost: Double) { viewModelScope.launch { repository.addGroceryItem(name, cost) } }
    fun toggleGroceryItem(item: GroceryItem) { viewModelScope.launch { repository.toggleGroceryItem(item) } }
    fun deleteGroceryItem(item: GroceryItem) { viewModelScope.launch { repository.deleteGroceryItem(item) } }

    fun addRecipeToFavorites(recipe: GeneratedRecipeMock) {
        viewModelScope.launch {
            val ingredientsStr = recipe.ingredients.joinToString(",") { "${it.first}:${it.second}" }
            repository.addFavoriteRecipe(recipe.name, recipe.estimatedCost, recipe.description, ingredientsStr)
        }
    }    fun removeFavorite(recipe: FavoriteRecipe) { viewModelScope.launch { repository.removeFavoriteRecipe(recipe) } }
    fun removeRecipeFromFavoritesByName(name: String) { viewModelScope.launch { repository.removeFavoriteRecipeByName(name) } }

    // Weekly Plan Ops with Balance Restoration
    fun addRecipeToWeeklyPlan(dayIndex: Int, recipe: GeneratedRecipeMock) {
        viewModelScope.launch {
            val ingredientsStr = recipe.ingredients.joinToString("||") { "${it.first}:${it.second}" }
            val stepsStr = recipe.steps.joinToString("||")
            repository.updateWeeklyPlanForDay(
                dayIndex = dayIndex,
                recipeName = recipe.name,
                cost = recipe.estimatedCost,
                description = recipe.description,
                ingredientsList = ingredientsStr,
                steps = stepsStr
            )
            budget.value?.let { repository.updateBalance(it.currentBalance - recipe.estimatedCost) }
        }
    }

    fun clearWeeklyPlanForDay(dayIndex: Int, costToRestore: Double) {
        viewModelScope.launch {
            repository.clearWeeklyPlanForDay(dayIndex)
            budget.value?.let { repository.updateBalance(it.currentBalance + costToRestore) }
        }
    }

    fun clearAllWeeklyPlans() {
        viewModelScope.launch {
            val currentPlans = repository.weeklyPlan.first()
            val totalCostToRestore = currentPlans.sumOf { it.estimatedCost }
            repository.clearAllWeeklyPlans()
            budget.value?.let { repository.updateBalance(it.currentBalance + totalCostToRestore) }
        }
    }

    fun generateRecipesWithAI(selectedIngredients: List<String>, budgetLimit: Double) {
        viewModelScope.launch {
            val currentKey = userApiKey.value
            if (currentKey.isNullOrBlank()) {
                generationProgress.value = "Peringatan: API Key belum diisi. Tekan ikon gir untuk mengisi."
                isGenerating.value = true; delay(2000); isGenerating.value = false; return@launch
            }

            isGenerating.value = true
            generatedRecipes.value = emptyList()
            val ingredientsStr = if (selectedIngredients.isEmpty()) "Bahan hemat anak kos" else selectedIngredients.joinToString(", ")
            val prompt = createPrompt(budgetLimit, ingredientsStr)
            val requestBody = GeminiRequest(
                contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt)))),
                generationConfig = GeminiGenerationConfig(
                    temperature = 0.7f,
                    responseMimeType = "application/json"
                )
            )

            // Pakai model yang sudah terdeteksi, atau fallback ke daftar default
            val modelsToTry = availableModels.value.ifEmpty { GeminiClient.PREFERRED_MODELS }
            var lastError: Exception? = null

            for (model in modelsToTry) {
                try {
                    generationProgress.value = "Sedang meracik resep untukmu..."
                    val url = GeminiClient.buildGenerateUrl(model, currentKey)
                    Log.d("CHEF_AI", "POST ke: ${url.replace(currentKey, "***")} (model: $model)")

                    val response = GeminiClient.instance.generateContent(
                        url = url,
                        request = requestBody
                    )

                    val responseText = response.candidates
                        ?.firstOrNull()
                        ?.content
                        ?.parts
                        ?.firstOrNull()
                        ?.text
                        ?: throw Exception("Respons AI kosong dari $model")

                    Log.d("CHEF_AI", "Respons $model: $responseText")
                    processResponse(responseText, selectedIngredients)
                    generationProgress.value = "Resep siap! Selamat makan 🍽️"
                    isGenerating.value = false
                    return@launch

                } catch (e: Exception) {
                    Log.e("CHEF_AI", "$model Gagal: ${e.javaClass.simpleName} - ${e.message}")
                    lastError = e
                }
            }

            // Semua model Gemini gagal, coba Vercel Cloud
            try {
                generationProgress.value = "Menghubungi Cloud Chef Revan..."
                Log.d("CHEF_AI", "Mencoba Vercel cloud...")
                val cloudResponse = RetrofitClient.instance.getRecommendation(
                    RecipeRequest(budget = budgetLimit, ingredients = ingredientsStr, userKey = currentKey)
                )
                generatedRecipes.value = listOf(
                    GeneratedRecipeMock(
                        name = cloudResponse.name,
                        estimatedCost = cloudResponse.cost,
                        description = "Resep dari Chef Revan Cloud.",
                        ingredients = listOf("Bahan sesuai resep" to cloudResponse.cost),
                        steps = cloudResponse.steps
                            .split(Regex("(?=\\d+\\.)"))
                            .map { it.trim() }
                            .filter { it.isNotBlank() }
                            .ifEmpty { listOf(cloudResponse.steps) }
                    )
                )
                generationProgress.value = "Resep siap dari Cloud! Selamat makan 🍽️"

            } catch (e3: Exception) {
                Log.e("CHEF_AI", "Cloud Gagal: ${e3.javaClass.simpleName} - ${e3.message}")
                val errMsg = lastError?.message ?: e3.message ?: "Unknown Error"
                generationProgress.value = when {
                    errMsg.contains("API_KEY_INVALID", ignoreCase = true) ||
                    errMsg.contains("API key not valid", ignoreCase = true) ->
                        "ERROR: API Key tidak valid. Periksa kembali di pengaturan (ikon gir)."
                    errMsg.contains("400") ->
                        "ERROR 400: Permintaan tidak valid. Cek API Key di pengaturan."
                    errMsg.contains("403") ->
                        "ERROR 403: API Key tidak punya akses. Aktifkan Gemini API di Google AI Studio."
                    errMsg.contains("404") ->
                        "ERROR 404: Model tidak tersedia. Coba deteksi ulang model di pengaturan."
                    errMsg.contains("429") ->
                        "ERROR 429: Kuota API habis. Coba lagi nanti."
                    errMsg.contains("UnknownHostException") || errMsg.contains("timeout", ignoreCase = true) ->
                        "ERROR: Tidak ada koneksi internet."
                    else ->
                        "AI ERROR: $errMsg"
                }
            } finally {
                isGenerating.value = false
            }
        }
    }

    private fun createPrompt(budget: Double, ingredients: String) = """
        Kamu adalah Chef Revan, ahli masakan anak kos Indonesia.
        Buat 1 resep masakan hemat dengan budget Rp $budget menggunakan bahan: $ingredients.
        
        Balas HANYA dengan JSON murni (tanpa markdown, tanpa teks lain):
        {
          "name": "Nama Masakan",
          "cost": total_estimasi_biaya_angka,
          "description": "Deskripsi singkat masakan 1-2 kalimat",
          "ingredients": [
            {"name": "nama bahan", "amount": "jumlah dan satuan", "price": estimasi_harga_angka},
            {"name": "nama bahan 2", "amount": "jumlah", "price": estimasi_harga_angka}
          ],
          "steps": [
            "Langkah pertama yang jelas dan detail",
            "Langkah kedua",
            "Langkah ketiga dst"
          ]
        }
    """.trimIndent()

    private fun processResponse(text: String, selectedIngredients: List<String>) {
        var cleanJson = text
            .replace("```json", "")
            .replace("```", "")
            .trim()

        val jsonStart = cleanJson.indexOf('{')
        val jsonEnd = cleanJson.lastIndexOf('}')
        if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
            cleanJson = cleanJson.substring(jsonStart, jsonEnd + 1)
        }

        Log.d("CHEF_AI", "JSON setelah dibersihkan: $cleanJson")

        val jsonObj = JSONObject(cleanJson)
        val name = jsonObj.optString("name", "Resep Chef Revan")
        val cost = jsonObj.optDouble("cost", 0.0)
        val description = jsonObj.optString("description", "Kreasi Chef Revan AI.")

        // Parse ingredients array
        val ingredientsList = mutableListOf<Pair<String, Double>>()
        val ingredientsArray = jsonObj.optJSONArray("ingredients")
        if (ingredientsArray != null) {
            for (i in 0 until ingredientsArray.length()) {
                val item = ingredientsArray.optJSONObject(i)
                if (item != null) {
                    val iName = item.optString("name", "")
                    val iAmount = item.optString("amount", "")
                    val iPrice = item.optDouble("price", 0.0)
                    val label = if (iAmount.isNotBlank()) "$iName ($iAmount)" else iName
                    if (label.isNotBlank()) ingredientsList.add(label to iPrice)
                }
            }
        }
        // Fallback jika AI tidak mengembalikan array ingredients
        if (ingredientsList.isEmpty()) {
            if (selectedIngredients.isNotEmpty()) {
                selectedIngredients.forEach { ingredientsList.add(it to (cost / selectedIngredients.size)) }
            } else {
                ingredientsList.add("Bahan sesuai selera" to cost)
            }
        }

        // Parse steps array
        val stepsList = mutableListOf<String>()
        val stepsArray = jsonObj.optJSONArray("steps")
        if (stepsArray != null) {
            for (i in 0 until stepsArray.length()) {
                val step = stepsArray.optString(i, "")
                if (step.isNotBlank()) stepsList.add(step)
            }
        }
        // Fallback jika AI mengembalikan steps sebagai string
        if (stepsList.isEmpty()) {
            val stepsStr = jsonObj.optString("steps", "")
            if (stepsStr.isNotBlank()) {
                // Coba split berdasarkan pola "1. 2. 3." atau "\n"
                val split = stepsStr
                    .split(Regex("(?=\\d+\\.)"))
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                stepsList.addAll(if (split.size > 1) split else listOf(stepsStr))
            } else {
                stepsList.add("Masak bahan-bahan sesuai selera.")
            }
        }

        generatedRecipes.value = listOf(
            GeneratedRecipeMock(
                name = name,
                estimatedCost = cost,
                description = description,
                ingredients = ingredientsList,
                steps = stepsList
            )
        )
    }

    class Factory(private val repository: BudgetRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BudgetViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return BudgetViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
