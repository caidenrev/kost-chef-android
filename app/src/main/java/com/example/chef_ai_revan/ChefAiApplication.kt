package com.example.chef_ai_revan

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.example.chef_ai_revan.data.AppDatabase
import com.example.chef_ai_revan.data.repository.BudgetRepository

private val Context.dataStore by preferencesDataStore(name = "settings")

class ChefAiApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy {
        BudgetRepository(
            database.budgetDao(),
            database.groceryItemDao(),
            database.favoriteRecipeDao(),
            database.weeklyPlanDao(),
            dataStore
        )
    }
}
