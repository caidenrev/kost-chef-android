package com.example.chef_ai_revan.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.chef_ai_revan.data.dao.*
import com.example.chef_ai_revan.data.entity.*

@Database(
    entities = [Budget::class, GroceryItem::class, FavoriteRecipe::class, WeeklyPlan::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun budgetDao(): BudgetDao
    abstract fun groceryItemDao(): GroceryItemDao
    abstract fun favoriteRecipeDao(): FavoriteRecipeDao
    abstract fun weeklyPlanDao(): WeeklyPlanDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "chef_ai_revan_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

