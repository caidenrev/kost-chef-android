package com.example.chef_ai_revan.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_recipes")
data class FavoriteRecipe(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val estimatedCost: Double,
    val description: String,
    val ingredientsList: String // Stored as comma-separated values (e.g. "Beras:3000,Telur:2000")
)
