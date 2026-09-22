package io.github.kobych.sanitly.data.repositories

import android.util.Log
import androidx.sqlite.SQLiteException
import io.github.kobych.sanitly.data.database.entities.Goal

class DiaryRepository(
    val goalProcessRepository: GoalListRepository
) {
    suspend fun saveNote(goal: Goal) = goalProcessRepository.updateGoal(goal)

    suspend fun getAllGoals(): List<Goal> {
        return try {
            goalProcessRepository.getAllGoals()
        } catch (e: SQLiteException) {
            Log.e("DiaryRepository", "Error fetching goals", e)
            emptyList()
        }
    }
}
