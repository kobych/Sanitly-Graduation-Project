package io.github.kobych.sanitly.data.repositories

import android.util.Log
import androidx.sqlite.SQLiteException
import io.github.kobych.sanitly.data.database.entities.Goal

class AnalyticsRepository(
    private val goalListRepository: GoalListRepository
) {
    suspend fun getAllGoals(): List<Goal> {
        return try {
            goalListRepository.getAllGoals()
        } catch (e: SQLiteException) {
            Log.e("GoalListRepository", "Error fetching goals", e)
            emptyList()
        }
    }
}
