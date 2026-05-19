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

    @Query("UPDATE weekly_plans SET recipeName = :recipeName, estimatedCost = :cost, description = :description WHERE dayIndex = :dayIndex")
    suspend fun updateWeeklyPlanForDay(dayIndex: Int, recipeName: String?, cost: Double, description: String?)

    @Query("UPDATE weekly_plans SET recipeName = NULL, estimatedCost = 0.0, description = NULL WHERE dayIndex = :dayIndex")
    suspend fun clearWeeklyPlanForDay(dayIndex: Int)

    @Query("UPDATE weekly_plans SET recipeName = NULL, estimatedCost = 0.0, description = NULL")
    suspend fun clearAllWeeklyPlans()
}
