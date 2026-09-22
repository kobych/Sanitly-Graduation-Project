package io.github.kobych.sanitly.ui.viewmodels

import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kobych.sanitly.data.database.entities.Goal
import io.github.kobych.sanitly.data.database.entities.Task
import io.github.kobych.sanitly.data.database.entities.TaskMood
import io.github.kobych.sanitly.data.repositories.GoalListRepository
import io.github.kobych.sanitly.ui.models.GoalProcessStepsState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private const val SECOND: Long = 1000L

/**
 * ViewModel for the goal tracking flow (GoalSelect → TaskSelect screens).
 *
 * Owns the in-app task timer: [launchTimer] starts a 1-second tick loop and broadcasts
 * the elapsed value to [GoalListRepository.currentSeconds] so the foreground service can
 * display it in the notification. [convertAndSaveTime] stops the timer and stores the
 * accumulated duration for the finished task.
 */
@HiltViewModel
class GoalListViewModel @Inject constructor(
    private val goalProcessRepository: GoalListRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<GoalProcessStepsState>(GoalProcessStepsState.GoalSelect)
    val uiState: StateFlow<GoalProcessStepsState> = _uiState.asStateFlow()

    private val _goals = mutableStateOf<List<Goal>>(emptyList())
    var goals: State<List<Goal>> = _goals

    private val _taskActualDuration = mutableStateOf(0.seconds)
    val taskActualDuration: State<Duration> = _taskActualDuration

    private val _selectedTask = mutableStateOf<Task?>(null)
    val selectedTask: State<Task?> = _selectedTask

    private val _taskMood = mutableStateOf(TaskMood.NEUTRAL)
    val taskMood: State<TaskMood> = _taskMood

    private val _selectedGoal = mutableStateOf<Goal?>(null)
    val selectedGoal: State<Goal?> = _selectedGoal

    private val _isTimerOn = mutableStateOf(false)
    val isTimerOn: State<Boolean> = _isTimerOn

    private val _seconds = mutableIntStateOf(0)
    private val _isGoalCompleted = mutableStateOf(false)

    private suspend fun completeGoal() {
        val areAllTasksCompleted = selectedGoal.value?.tasks?.all { it.isFinished } ?: false
        if (areAllTasksCompleted) {
            val updatedGoal = selectedGoal.value?.copy(
                isCompleted = true
            )
            goalProcessRepository.completeGoal(updatedGoal ?: return)
            _isGoalCompleted.value = true
        }
    }

    fun updateGoalProcessState() {
        _uiState.update { currentState ->
            when(currentState) {
                GoalProcessStepsState.GoalSelect -> GoalProcessStepsState.TaskSelect
                GoalProcessStepsState.TaskSelect -> GoalProcessStepsState.GoalSelect
            }
        }
    }

    fun isButtonAvailable(task: Task): Boolean {
        return !isTimerOn.value || task.name == selectedTask.value?.name
    }

    fun selectGoal(goal: Goal) {
        _selectedGoal.value = goal
    }

    fun selectTask(task: Task) {
        _selectedTask.value = task
    }

    fun selectMood(mood: TaskMood) {
        _taskMood.value = mood
    }

    /**
     * Persists the finished task into the goal, records actual duration and mood,
     * and triggers [completeGoal] if every task in the goal is now finished.
     *
     * @param name Name of the task to mark as finished.
     * @param isCompleted Whether the user considers the task successfully completed (true) or abandoned (false).
     */
    fun finishTask(name: String, isCompleted: Boolean) {
        val oldTasks = selectedGoal.value?.tasks
        val updatedTasks = oldTasks?.map { task ->
            if (task.name == name) {
                taskActualDuration.value.toComponents { hours, minutes, seconds, _ ->
                    task.copy(
                        isFinished = true,
                        taskMood = taskMood.value,
                        actualSeconds = seconds,
                        actualMinutes = minutes,
                        actualHours = hours,
                        isCompleted = isCompleted,
                        completedAt = System.currentTimeMillis()
                    )
                }
            } else {
                task
            }
        }
        val updatedGoal = selectedGoal.value?.copy(tasks = updatedTasks ?: emptyList())
        _selectedGoal.value = updatedGoal
        viewModelScope.launch {
            goalProcessRepository.updateGoal(updatedGoal ?: return@launch)
            completeGoal()
        }
    }

    fun launchTimer() {
        viewModelScope.launch {
            if (!isTimerOn.value) {
                _isTimerOn.value = true
                goalProcessRepository.setTimerRunning(true)
                while (isTimerOn.value) {
                    delay(SECOND)
                    _seconds.intValue++
                    goalProcessRepository.updateSeconds(_seconds.intValue)
                }
            }
        }
    }

    fun convertAndSaveTime() {
        _isTimerOn.value = false
        goalProcessRepository.setTimerRunning(false)
        _taskActualDuration.value = _seconds.intValue.seconds
        _seconds.intValue = 0
    }

    fun getAllGoals() {
        viewModelScope.launch {
            try {
                _goals.value = goalProcessRepository.getAllGoals()
            } catch (e: SQLiteException) {
                Log.e("GoalListViewModel", "Error fetching goals", e)
                _goals.value = emptyList()
            }
        }
    }
}
