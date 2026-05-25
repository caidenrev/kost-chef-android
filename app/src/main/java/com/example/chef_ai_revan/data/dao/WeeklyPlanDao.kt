package com.example.chef_ai_revan.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.chef_ai_revan.data.entity.WeeklyPlan
import kotlinx.coroutines.flow.Flow

@Dao
interface WeeklyPlanDao {
    @Query("SELECT * FROM weekly_plans ORDER BY dayIndex ASC")
    fun getWeeklyPlan(): Flow<List<WeeklyPlan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeeklyPlan(plans: List<WeeklyPlan>)

    @Update
    suspend fun updateWeeklyPlan(plan: WeeklyPlan)

    @Query("UPDATE weekly_plans SET recipeName = :recipeName, estimatedCost = :cost, description = :description, ingredientsList = :ingredientsList, steps = :steps WHERE dayIndex = :dayIndex")
    suspend fun updateWeeklyPlanForDay(dayIndex: Int, recipeName: String?, cost: Double, description: String?, ingredientsList: String?, steps: String?)

    @Query("UPDATE weekly_plans SET recipeName = NULL, estimatedCost = 0.0, description = NULL, ingredientsList = NULL, steps = NULL WHERE dayIndex = :dayIndex")
    suspend fun clearWeeklyPlanForDay(dayIndex: Int)

    @Query("UPDATE weekly_plans SET recipeName = NULL, estimatedCost = 0.0, description = NULL, ingredientsList = NULL, steps = NULL")
    suspend fun clearAllWeeklyPlans()
}
