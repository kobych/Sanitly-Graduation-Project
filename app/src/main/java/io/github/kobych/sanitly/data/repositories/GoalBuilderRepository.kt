package io.github.kobych.sanitly.data.repositories

import android.util.Log
import androidx.sqlite.SQLiteException
import io.github.kobych.sanitly.data.database.dao.GoalDao
import io.github.kobych.sanitly.data.database.entities.Goal

class GoalBuilderRepository(
    private val goalDao: GoalDao
) {
    suspend fun saveGoal(goal: Goal) = goalDao.saveGoal(goal)

    suspend fun deleteGoal(goal: Goal) = goalDao.deleteGoal(goal)

    suspend fun getAllGoals(): List<Goal> {
        return try {
            goalDao.getAllGoals()
        } catch (e: SQLiteException) {
            Log.e("GoalBuilderRepository", "Error fetching goals", e)
            emptyList()
        }
    }
}
