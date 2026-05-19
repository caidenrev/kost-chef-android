package com.example.chef_ai_revan

import android.app.Application
import com.example.chef_ai_revan.data.AppDatabase
import com.example.chef_ai_revan.data.repository.BudgetRepository

class ChefAiApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy {
        BudgetRepository(
            database.budgetDao(),
            database.groceryItemDao(),
            database.favoriteRecipeDao(),
            database.weeklyPlanDao()
        )
    }
}
