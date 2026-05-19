package com.example.chef_ai_revan.data.repository

import com.example.chef_ai_revan.data.dao.*
import com.example.chef_ai_revan.data.entity.*
import kotlinx.coroutines.flow.Flow

class BudgetRepository(
    private val budgetDao: BudgetDao,
    private val groceryItemDao: GroceryItemDao,
    private val favoriteRecipeDao: FavoriteRecipeDao,
    private val weeklyPlanDao: WeeklyPlanDao
) {
    val budget: Flow<Budget?> = budgetDao.getBudget()
    val groceryItems: Flow<List<GroceryItem>> = groceryItemDao.getAllItems()
    val favoriteRecipes: Flow<List<FavoriteRecipe>> = favoriteRecipeDao.getAllFavorites()
    val weeklyPlan: Flow<List<WeeklyPlan>> = weeklyPlanDao.getWeeklyPlan()

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

    suspend fun updateWeeklyPlanForDay(dayIndex: Int, recipeName: String?, cost: Double, description: String?) {
        weeklyPlanDao.updateWeeklyPlanForDay(dayIndex, recipeName, cost, description)
    }

    suspend fun clearWeeklyPlanForDay(dayIndex: Int) {
        weeklyPlanDao.clearWeeklyPlanForDay(dayIndex)
    }

    suspend fun clearAllWeeklyPlans() {
        weeklyPlanDao.clearAllWeeklyPlans()
    }
}
