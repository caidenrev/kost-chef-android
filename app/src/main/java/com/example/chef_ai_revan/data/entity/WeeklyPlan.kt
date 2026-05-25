package com.example.chef_ai_revan.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weekly_plans")
data class WeeklyPlan(
    @PrimaryKey val dayIndex: Int,
    val dayName: String,
    val recipeName: String? = null,
    val estimatedCost: Double = 0.0,
    val description: String? = null,
    val ingredientsList: String? = null,  // "Bahan (100g):2000,Bahan2:3000"
    val steps: String? = null             // "Langkah 1||Langkah 2||Langkah 3"
)
