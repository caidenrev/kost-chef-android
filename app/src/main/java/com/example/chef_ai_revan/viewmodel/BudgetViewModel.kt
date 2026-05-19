package com.example.chef_ai_revan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.chef_ai_revan.data.entity.*
import com.example.chef_ai_revan.data.repository.BudgetRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class GeneratedRecipeMock(
    val name: String,
    val estimatedCost: Double,
    val description: String,
    val ingredients: List<Pair<String, Double>>,
    val instructions: String
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

    // Cloud Sync States
    val isSyncing = MutableStateFlow(false)
    val syncEmail = MutableStateFlow<String?>(null)

    // AI Generation States
    val isGenerating = MutableStateFlow(false)
    val generationProgress = MutableStateFlow("")
    val generatedRecipes = MutableStateFlow<List<GeneratedRecipeMock>>(emptyList())

    // Survive Mode: Balance < Rp 10.000 or < 15% of Limit
    val isSurviveMode: StateFlow<Boolean> = budget.map { b ->
        if (b == null || b.limit == 0.0) false
        else (b.currentBalance / b.limit) < 0.15 || b.currentBalance < 10000.0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Budget Monitor Alert: spending exceeds 80% (i.e. remaining balance <= 20% of limit)
    val isWeeklyBudgetWarning: StateFlow<Boolean> = budget.map { b ->
        if (b == null || b.limit == 0.0) false
        else (b.currentBalance / b.limit) <= 0.20
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        // Pre-populate 7-day plan if Room holds no entries
        viewModelScope.launch {
            val currentPlans = repository.weeklyPlan.first()
            if (currentPlans.isEmpty()) {
                val days = listOf("SENIN", "SELASA", "RABU", "KAMIS", "JUMAT", "SABTU", "MINGGU")
                val initialPlans = days.mapIndexed { idx, day ->
                    WeeklyPlan(dayIndex = idx, dayName = day)
                }
                repository.initializeWeeklyPlans(initialPlans)
            }
        }
    }

    // Budget Operations
    fun updateBudgetLimit(limit: Double) {
        viewModelScope.launch {
            val current = budget.value
            repository.updateBudget(limit, current?.currentBalance ?: limit)
        }
    }

    fun addExpense(amount: Double) {
        viewModelScope.launch {
            val current = budget.value
            if (current != null) {
                repository.updateBalance(current.currentBalance - amount)
            }
        }
    }

    // Grocery Operations
    fun addGroceryItem(name: String, cost: Double) {
        viewModelScope.launch {
            repository.addGroceryItem(name, cost)
        }
    }

    fun toggleGroceryItem(item: GroceryItem) {
        viewModelScope.launch {
            repository.toggleGroceryItem(item)
        }
    }

    fun deleteGroceryItem(item: GroceryItem) {
        viewModelScope.launch {
            repository.deleteGroceryItem(item)
        }
    }

    // Favorites Operations
    fun addRecipeToFavorites(recipe: GeneratedRecipeMock) {
        viewModelScope.launch {
            val ingredientsStr = recipe.ingredients.joinToString(",") { "${it.first}:${it.second}" }
            repository.addFavoriteRecipe(recipe.name, recipe.estimatedCost, recipe.description, ingredientsStr)
        }
    }

    fun removeRecipeFromFavoritesByName(name: String) {
        viewModelScope.launch {
            repository.removeFavoriteRecipeByName(name)
        }
    }

    fun removeFavorite(recipe: FavoriteRecipe) {
        viewModelScope.launch {
            repository.removeFavoriteRecipe(recipe)
        }
    }

    // Weekly Planner Operations
    fun addRecipeToWeeklyPlan(dayIndex: Int, recipeName: String, cost: Double, description: String) {
        viewModelScope.launch {
            repository.updateWeeklyPlanForDay(dayIndex, recipeName, cost, description)
            // Automatically deduct planned meal cost from main wallet balance!
            val current = budget.value
            if (current != null) {
                repository.updateBalance(current.currentBalance - cost)
            }
        }
    }

    fun clearWeeklyPlanForDay(dayIndex: Int, costToRestore: Double) {
        viewModelScope.launch {
            repository.clearWeeklyPlanForDay(dayIndex)
            // Restore wallet balance when clearing a planned dish!
            val current = budget.value
            if (current != null) {
                repository.updateBalance(current.currentBalance + costToRestore)
            }
        }
    }

    fun clearAllWeeklyPlans() {
        viewModelScope.launch {
            repository.clearAllWeeklyPlans()
        }
    }

    // Cloud Sync Google Simulation
    fun triggerGoogleCloudSync() {
        viewModelScope.launch {
            isSyncing.value = true
            delay(1500)
            syncEmail.value = "anak_kos_gemilang@gmail.com"
            isSyncing.value = false
        }
    }

    fun logoutGoogleSync() {
        syncEmail.value = null
    }

    // AI Generation Engine Simulator
    fun generateRecipesWithAI(selectedIngredients: List<String>, budgetLimit: Double) {
        viewModelScope.launch {
            isGenerating.value = true
            generatedRecipes.value = emptyList()
            
            generationProgress.value = "Connecting to Google Gemini 1.5 Flash..."
            delay(800)
            generationProgress.value = "Analyzing selected ingredients: ${selectedIngredients.joinToString(", ")}..."
            delay(800)
            generationProgress.value = "Calculating budget-friendly warung local pricing..."
            delay(800)
            generationProgress.value = "Structuring optimal recipe plans under Rp ${budgetLimit.toInt()}..."
            delay(800)

            val matches = getMockRecipesData().filter { recipe ->
                recipe.estimatedCost <= budgetLimit && (selectedIngredients.isEmpty() || selectedIngredients.any { ing ->
                    recipe.name.contains(ing, ignoreCase = true) || 
                    recipe.ingredients.any { it.first.contains(ing, ignoreCase = true) }
                })
            }

            // Fallback to budget recipes if no strict match
            val finalRecipes = if (matches.isNotEmpty()) {
                matches
            } else {
                getMockRecipesData().filter { it.estimatedCost <= budgetLimit }
            }

            generatedRecipes.value = finalRecipes.shuffled().take(3)
            isGenerating.value = false
        }
    }

    fun getMockRecipesData(): List<GeneratedRecipeMock> {
        return listOf(
            GeneratedRecipeMock(
                "Nasi Telur Dadar Kribo",
                6000.0,
                "Resep kribo crispy super harum. Sangat nikmat dimakan dengan kecap manis dan nasi panas.",
                listOf("Nasi/Beras" to 3000.0, "Telur" to 2000.0, "Minyak & Kecap" to 1000.0),
                "Kocok telur dengan garam dan sedikit air. Tuangkan tinggi ke dalam minyak yang sangat panas. Goreng kering hingga kecokelatan."
            ),
            GeneratedRecipeMock(
                "Tumis Tempe Kacang Panjang",
                8000.0,
                "Tumisan tempe manis gurih protein tinggi ditambah kacang panjang kaya serat segar.",
                listOf("Tempe" to 3000.0, "Kacang Panjang" to 3000.0, "Bumbu Iris" to 2000.0),
                "Potong tempe dadu kecil lalu goreng setengah kering. Tumis bawang bombay cabai bawang putih, masukkan kacang panjang dan tempe, kecap manis, garam merica."
            ),
            GeneratedRecipeMock(
                "Mie Instan Nyemek Sosis",
                9000.0,
                "Kreasi mie instan favorit warkop. Kuah nyemek kental, pedas mantap ditambah sosis iris.",
                listOf("Mie Instan" to 3500.0, "Sosis Sapi" to 3000.0, "Sawi Hijau" to 1500.0, "Telur" to 1000.0),
                "Rebus mie 2 menit tiriskan. Tumis bawang putih cabai merah, masukkan sosis, air secukupnya. Tambahkan mie, bumbu instan, telur dikocok lepas hingga kuah mengental."
            ),
            GeneratedRecipeMock(
                "Tahu Cabe Garam Rice Cooker",
                7000.0,
                "Tahu goreng kering berlumur irisan bawang putih, cabai rawit pedas dan daun bawang wangi.",
                listOf("Tahu Sutra/Putih" to 3000.0, "Cabai rawit & Bawang" to 2000.0, "Tepung Bumbu" to 2000.0),
                "Balut tahu dengan tepung basah lalu kering. Masak di rice cooker dengan minyak hingga kecokelatan. Masukkan bawang cabai garam penyedap, tumis rata."
            ),
            GeneratedRecipeMock(
                "Sarden Campur Kentang Goreng",
                15000.0,
                "Sarden saus tomat kaya gizi dipadu kentang goreng empuk. Pas disajikan untuk porsi berdua.",
                listOf("Sarden Kaleng Kecil" to 10000.0, "Kentang Sedang" to 3000.0, "Bawang Iris" to 2000.0),
                "Kupas kentang lalu goreng dadu. Tumis bawang bombay iris cabai rawit, tuang sarden kaleng, masukkan kentang goreng, masak hingga bumbu meresap."
            ),
            GeneratedRecipeMock(
                "Tumis Kangkung Belacan",
                6000.0,
                "Kangkung warung tenda. Kangkung tumis super cepat dengan bumbu terasi bakar gurih pedas.",
                listOf("Kangkung 1 Ikat" to 3000.0, "Bumbu Terasi & Cabai" to 3000.0),
                "Panaskan minyak, tumis bumbu ulek terasi cabai bawang merah bawang putih. Masukkan kangkung dan air sedikit, aduk cepat dengan api besar hingga layu."
            ),
            GeneratedRecipeMock(
                "Ayam Kecap Rice Cooker",
                18000.0,
                "Resep mewah akhir bulan. Ayam empuk berbalut saus kecap kental gurih, cukup dimasak dalam rice cooker.",
                listOf("Ayam Potong 250g" to 12000.0, "Bawang Bombay & Putih" to 3000.0, "Kecap Manis & Saus" to 3000.0),
                "Marinate ayam dengan kecap manis dan lada. Tata irisan bawang bombay di dasar rice cooker, taruh ayam di atasnya. Masak hingga tombol rice cooker berpindah."
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
