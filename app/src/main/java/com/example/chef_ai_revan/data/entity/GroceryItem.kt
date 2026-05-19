package com.example.chef_ai_revan.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grocery_items")
data class GroceryItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val estimatedCost: Double,
    val isChecked: Boolean = false
)
