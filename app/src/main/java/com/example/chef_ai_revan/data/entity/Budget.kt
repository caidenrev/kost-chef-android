package com.example.chef_ai_revan.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budget")
data class Budget(
    @PrimaryKey val id: Int = 1, // Single entry for current weekly budget
    val limit: Double = 0.0,
    val currentBalance: Double = 0.0,
    val startDate: Long = System.currentTimeMillis()
)
