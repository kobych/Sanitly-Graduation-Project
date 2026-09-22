package io.github.kobych.sanitly.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.SQLiteException
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kobych.sanitly.data.database.entities.Goal
import io.github.kobych.sanitly.data.database.entities.Task
import io.github.kobych.sanitly.data.database.entities.TaskDifficulty
import io.github.kobych.sanitly.data.database.entities.TaskMood
import io.github.kobych.sanitly.data.repositories.GoalBuilderRepository
import io.github.kobych.sanitly.ui.models.GoalBuilderStepsState
import io.github.kobych.sanitly.ui.screens.goalbuilder.GoalCreationState
import io.github.kobych.sanitly.ui.screens.goalbuilder.GoalIntoTasksState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the multi-step goal creation flow (Builder → Creation → GoalIntoTasks → Created).
 *
 * UI state advances through [GoalBuilderStepsState] via [updateGoalBuilderState] and can
 * step back via [goPreviousGoalBuilderState]. Guard properties ([isGoalIntoTasksScreenAvailable],
 * [isGoalCreatedScreenAvailable], [isTaskSavable]) disable navigation buttons until the minimum
 * required fields are filled in.
 */
@HiltViewModel
class GoalBuilderViewModel @Inject constructor(
    private val goalBuilderRepository: GoalBuilderRepository
) : ViewModel() {
    private val _isFailed = mutableStateOf(false)
    private val _completedAt = mutableLongStateOf(0L)

    private val _uiState = MutableStateFlow<GoalBuilderStepsState>(GoalBuilderStepsState.Builder)
    val uiState: StateFlow<GoalBuilderStepsState> = _uiState.asStateFlow()

    var goalName = mutableStateOf("")
    var goalCategory = mutableStateOf("")
    var goalDeadline = mutableStateOf("")
    var goalDescription = mutableStateOf("")
    var isGoalCompleted = mutableStateOf(false)

    var taskName = mutableStateOf("")
    var taskDifficulty = mutableStateOf(TaskDifficulty.VERY_EASY)
    var taskHours = mutableStateOf("")
    var isTaskCompleted = mutableStateOf(false)
    var taskMood = mutableStateOf(TaskMood.NEUTRAL)
    var taskActualHours = mutableLongStateOf(0L)
    var taskActualMinutes = mutableIntStateOf(0)
    var taskActualSeconds = mutableIntStateOf(0)

    private val _taskList = mutableStateOf<List<Task>>(emptyList())
    var taskList: State<List<Task>> = _taskList

    private val _goals = mutableStateOf<List<Goal>>(emptyList())
    var goals: State<List<Goal>> = _goals

    val isGoalIntoTasksScreenAvailable: Boolean by derivedStateOf {
        goalName.value.isNotBlank() && goalCategory.value.isNotBlank() && goalDeadline.value.isNotBlank()
    }

    val isGoalCreatedScreenAvailable: Boolean by derivedStateOf {
        taskList.value.isNotEmpty()
    }

    val isTaskSavable: Boolean by derivedStateOf {
        taskName.value.isNotBlank() && taskHours.value.isNotBlank()
    }

    val goalCreationState
        get() = GoalCreationState(
            goalName = goalName,
            goalCategory = goalCategory,
            goalDeadline = goalDeadline,
            goalDescription = goalDescription,
            initialDateMillis = goalDeadline.value.toLongOrNull(),
        )

    val goalIntoTasksState
        get() = GoalIntoTasksState(
            taskList = _taskList,
            taskName = taskName,
            taskHours = taskHours,
            taskDifficulty = taskDifficulty,
            onNextEnabled = isGoalCreatedScreenAvailable,
            isTaskSavable = isTaskSavable,
            saveTask = { saveTask() },
            deleteTask = { deleteTask(it) },
        )

    private fun deleteTask(task: Task?) {
        viewModelScope.launch {
            _taskList.value -= task!!
        }
    }

    private fun saveTask() {
        viewModelScope.launch {
            _taskList.value = taskList.value + Task(
                name = taskName.value,
                hours = taskHours.value.toLongOrNull() ?: 0,
                difficulty = taskDifficulty.value,
                isFinished = isTaskCompleted.value,
                taskMood = taskMood.value,
                actualHours = taskActualHours.longValue,
                actualMinutes = taskActualMinutes.intValue,
                actualSeconds = taskActualSeconds.intValue,
                isCompleted = _isFailed.value,
                completedAt = _completedAt.longValue
            )
            taskName.value = ""
            taskHours.value = ""
            taskDifficulty.value = TaskDifficulty.VERY_EASY
        }
    }

    fun updateGoalBuilderState() {
        _uiState.update { currentState ->
            when (currentState) {
                GoalBuilderStepsState.Builder -> GoalBuilderStepsState.Creation
                GoalBuilderStepsState.Creation -> GoalBuilderStepsState.GoalIntoTasks
                GoalBuilderStepsState.GoalIntoTasks -> GoalBuilderStepsState.Created
                GoalBuilderStepsState.Created -> GoalBuilderStepsState.Builder
            }
        }
    }

    fun goPreviousGoalBuilderState() {
        _uiState.update { currentState ->
            when (currentState) {
                GoalBuilderStepsState.Created -> GoalBuilderStepsState.GoalIntoTasks
                GoalBuilderStepsState.GoalIntoTasks -> GoalBuilderStepsState.Creation
                GoalBuilderStepsState.Creation -> GoalBuilderStepsState.Builder
                GoalBuilderStepsState.Builder -> GoalBuilderStepsState.Builder
            }
        }
    }

    fun getAllGoals() {
        viewModelScope.launch {
            try {
                _goals.value = goalBuilderRepository.getAllGoals()
            } catch (e: SQLiteException) {
                Log.e("GoalBuilderViewModel", "Error fetching goals", e)
                _goals.value = emptyList()
            }
        }
    }

    fun createAndSaveGoal() {
        val goal = Goal(
            id = 0,
            name = goalName.value,
            category = goalCategory.value,
            hours = taskList.value.sumOf { it.hours },
            deadline = goalDeadline.value.toLongOrNull(),
            description = goalDescription.value,
            tasks = taskList.value,
            isCompleted = isGoalCompleted.value,
            successNote = "",
            failureNote = "",
            summaryNote = ""
        )
        viewModelScope.launch {
            goalBuilderRepository.saveGoal(goal)
            goalName.value = ""
            goalCategory.value = ""
            goalDeadline.value = ""
            goalDescription.value = ""
            _taskList.value = emptyList()
        }
    }

    fun deleteGoal(goal: Goal) {
        viewModelScope.launch {
            _goals.value -= goal
            goalBuilderRepository.deleteGoal(goal)
        }
    }
}
