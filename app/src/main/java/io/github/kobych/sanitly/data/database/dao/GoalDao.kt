package io.github.kobych.sanitly.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import io.github.kobych.sanitly.data.database.entities.Goal

@Dao
interface GoalDao {
    /**
     * Instead of Insert
     * Upsert inserts parameters in database
     * or updates them if parameters already exist
     */
    @Upsert
    suspend fun saveGoal(goal: Goal)

    @Transaction
    @Query("SELECT * FROM goals")
    suspend fun getAllGoals(): List<Goal>

    @Update
    suspend fun completeGoal(goal: Goal)

    @Update
    suspend fun updateGoal(goal: Goal)

    @Delete
    suspend fun deleteGoal(goal: Goal)
}
