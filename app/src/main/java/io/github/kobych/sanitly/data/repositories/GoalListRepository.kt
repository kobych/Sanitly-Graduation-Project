package io.github.kobych.sanitly.data.repositories

import android.util.Log
import androidx.sqlite.SQLiteException
import io.github.kobych.sanitly.data.database.dao.GoalDao
import io.github.kobych.sanitly.data.database.entities.Goal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository for goal list operations and shared timer state.
 *
 * In addition to CRUD access via [GoalDao], this class owns [currentSeconds] and [isTimerRunning]
 * — two hot [StateFlow]s that are shared between [GoalListViewModel] (writes) and
 * [TaskForegroundService] (reads). This shared state is what keeps the foreground service
 * notification in sync with the timer running in the ViewModel.
 */
class GoalListRepository(
    private val goalDao: GoalDao
) {
    private val _currentSeconds = MutableStateFlow(0)
    /** Current elapsed timer value in seconds; updated every second while a task timer is running. */
    val currentSeconds = _currentSeconds.asStateFlow()

    private val _isTimerRunning: MutableStateFlow<Boolean> = MutableStateFlow(false)
    /** True while the task timer is active. The foreground service observes this to know when to stop. */
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning

    suspend fun getAllGoals(): List<Goal> {
        return try {
            goalDao.getAllGoals()
        } catch (e: SQLiteException) {
            Log.e("GoalListRepository", "Error fetching goals", e)
            emptyList()
        }
    }

    suspend fun completeGoal(goal: Goal) = goalDao.completeGoal(goal)

    suspend fun updateGoal(goal: Goal) = goalDao.updateGoal(goal)

    /** Updates the shared timer counter. Negative values are ignored. */
    fun updateSeconds(seconds: Int) {
        if (seconds >= 0) {
            _currentSeconds.value = seconds
        }
    }

    fun setTimerRunning(isTimerRunning: Boolean) {
        _isTimerRunning.value = isTimerRunning
    }
}
