package com.example.chef_ai_revan.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weekly_plans")
data class WeeklyPlan(
    @PrimaryKey val dayIndex: Int, // 0 = Senin, 1 = Selasa, ..., 6 = Minggu
    val dayName: String,           // "SENIN", "SELASA", etc.
    val recipeName: String? = null,
    val estimatedCost: Double = 0.0,
    val description: String? = null
)
