package com.example.chef_ai_revan.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.chef_ai_revan.data.dao.*
import com.example.chef_ai_revan.data.entity.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BudgetRepository(
    private val budgetDao: BudgetDao,
    private val groceryItemDao: GroceryItemDao,
    private val favoriteRecipeDao: FavoriteRecipeDao,
    private val weeklyPlanDao: WeeklyPlanDao,
    private val dataStore: DataStore<Preferences>
) {
    val budget: Flow<Budget?> = budgetDao.getBudget()
    val groceryItems: Flow<List<GroceryItem>> = groceryItemDao.getAllItems()
    val favoriteRecipes: Flow<List<FavoriteRecipe>> = favoriteRecipeDao.getAllFavorites()
    val weeklyPlan: Flow<List<WeeklyPlan>> = weeklyPlanDao.getWeeklyPlan()

    private val USER_API_KEY = stringPreferencesKey("user_gemini_api_key")
    val userApiKey: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_API_KEY]
    }

    suspend fun saveUserApiKey(key: String) {
        dataStore.edit { preferences ->
            preferences[USER_API_KEY] = key
        }
    }

    suspend fun updateBudget(limit: Double, balance: Double) {
        budgetDao.insertBudget(Budget(limit = limit, currentBalance = balance))
    }

    suspend fun updateBalance(newBalance: Double) {
        budgetDao.updateBalance(newBalance)
    }

    suspend fun addGroceryItem(name: String, cost: Double) {
        groceryItemDao.insertItem(GroceryItem(name = name, estimatedCost = cost))
    }

    suspend fun toggleGroceryItem(item: GroceryItem) {
        groceryItemDao.updateItem(item.copy(isChecked = !item.isChecked))
    }

    suspend fun updateGroceryItem(item: GroceryItem) {
        groceryItemDao.updateItem(item)
    }

    suspend fun deleteGroceryItem(item: GroceryItem) {
        groceryItemDao.deleteItem(item)
    }

    // Favorite Recipes Operations
    suspend fun addFavoriteRecipe(name: String, cost: Double, description: String, ingredients: String) {
        favoriteRecipeDao.insertFavorite(
            FavoriteRecipe(name = name, estimatedCost = cost, description = description, ingredientsList = ingredients)
        )
    }

    suspend fun removeFavoriteRecipeByName(name: String) {
        favoriteRecipeDao.deleteFavoriteByName(name)
    }

    suspend fun removeFavoriteRecipe(recipe: FavoriteRecipe) {
        favoriteRecipeDao.deleteFavorite(recipe)
    }

    // Weekly Plan Operations
    suspend fun initializeWeeklyPlans(plans: List<WeeklyPlan>) {
        weeklyPlanDao.insertWeeklyPlan(plans)
    }

    suspend fun updateWeeklyPlanForDay(dayIndex: Int, recipeName: String?, cost: Double, description: String?, ingredientsList: String?, steps: String?) {
        weeklyPlanDao.updateWeeklyPlanForDay(dayIndex, recipeName, cost, description, ingredientsList, steps)
    }

    suspend fun clearWeeklyPlanForDay(dayIndex: Int) {
        weeklyPlanDao.clearWeeklyPlanForDay(dayIndex)
    }

    suspend fun clearAllWeeklyPlans() {
        weeklyPlanDao.clearAllWeeklyPlans()
    }
}
