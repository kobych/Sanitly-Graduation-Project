package io.github.kobych.sanitly.viewmodel

import android.database.sqlite.SQLiteException
import android.util.Log
import io.github.kobych.sanitly.MainDispatcherRule
import io.github.kobych.sanitly.util.createDefaultGoal
import io.github.kobych.sanitly.util.createDefaultTask
import io.github.kobych.sanitly.data.database.entities.Goal
import io.github.kobych.sanitly.data.database.entities.TaskMood
import io.github.kobych.sanitly.data.repositories.GoalListRepository
import io.github.kobych.sanitly.ui.models.GoalProcessStepsState
import io.github.kobych.sanitly.ui.viewmodels.GoalListViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

class GoalListViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<GoalListRepository>()
    private lateinit var viewModel: GoalListViewModel

    @Before
    fun setup() {
        viewModel = GoalListViewModel(repository)

        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    @Test
    fun updateGoalProcessState_goalSelectState_updatesToTaskSelect() {
        viewModel.updateGoalProcessState()

        assertEquals(GoalProcessStepsState.TaskSelect, viewModel.uiState.value)
    }

    @Test
    fun updateGoalProcessState_taskSelectState_updatesToGoalSelect() {
        viewModel.updateGoalProcessState()
        viewModel.updateGoalProcessState()

        assertEquals(GoalProcessStepsState.GoalSelect, viewModel.uiState.value)
    }

    @Test
    fun isButtonAvailable_normalTaskTimerOff_returnsTrue() = runTest {
        val task = createDefaultTask()

        assertEquals(true, viewModel.isButtonAvailable(task))
    }

    @Test
    fun selectGoal_normalGoal_selectsGoal() {
        val goal = createDefaultGoal(0, tasks = listOf(createDefaultTask()))

        viewModel.selectGoal(goal)

        assertEquals(goal, viewModel.selectedGoal.value)
    }

    @Test
    fun selectTask_normalTask_selectsTask() {
        val task = createDefaultTask()

        viewModel.selectTask(task)

        assertEquals(task, viewModel.selectedTask.value)
    }

    @Test
    fun selectMood_normalMood_selectsMood() {
        val task = createDefaultTask()

        viewModel.selectMood(TaskMood.NEUTRAL)

        assertEquals(task.taskMood, viewModel.taskMood.value)
    }

    @Test
    fun finishTask_taskIsCompleted_successfulFinishedAndCompleted() = runTest {
        val task = createDefaultTask(name = "TestTask", isCompleted = false)
        val goal = createDefaultGoal(1, tasks = listOf(task))
        coEvery { repository.updateGoal(any()) } returns Unit
        coEvery { repository.completeGoal(any()) } returns Unit

        viewModel.selectGoal(goal)
        viewModel.finishTask(task.name, true)

        val updatedTask = viewModel.selectedGoal.value?.tasks?.find { it.name == task.name }
        assertEquals(true, updatedTask?.isCompleted)
        assertEquals(true, updatedTask?.isFinished)
    }

    @Test
    fun finishTask_taskIsNotCompleted_taskFinishedNotCompleted() = runTest {
        val task = createDefaultTask(name = "TestTask", isCompleted = false)
        val goal = createDefaultGoal(1, tasks = listOf(task))
        coEvery { repository.updateGoal(any()) } returns Unit
        coEvery { repository.completeGoal(any()) } returns Unit

        viewModel.selectGoal(goal)
        viewModel.finishTask(task.name, false)

        val updatedTask = viewModel.selectedGoal.value?.tasks?.find { it.name == task.name }
        assertEquals(false, updatedTask?.isCompleted)
        assertEquals(true, updatedTask?.isFinished)
    }

    @Test
    fun finishTask_differentTaskNames_nothingUpdates() = runTest {
        val task = createDefaultTask(name = "TestTask", isCompleted = false)
        val goal = createDefaultGoal(1, tasks = listOf(task))
        coEvery { repository.updateGoal(any()) } returns Unit
        coEvery { repository.completeGoal(any()) } returns Unit

        viewModel.selectGoal(goal)
        viewModel.finishTask("Task", true)

        val updatedTask = viewModel.selectedGoal.value?.tasks?.find { it.name == task.name }
        assertEquals(false, updatedTask?.isCompleted)
        assertEquals(false, updatedTask?.isFinished)
    }

    @Test
    fun launchTimer_timerOff_timerLaunches() {
        coEvery { repository.setTimerRunning(any()) } returns Unit
        coEvery { repository.updateSeconds(any())} returns Unit

        viewModel.launchTimer()

        assertEquals(true, viewModel.isTimerOn.value)
    }

    @Test
    fun convertAndSaveTime_timerIsRunning_resetsTimerAndUpdatesDuration() = runTest {
        coEvery { repository.setTimerRunning(any()) } returns Unit

        viewModel.convertAndSaveTime()

        assertEquals(false, viewModel.isTimerOn.value)
        assertEquals(0.seconds, viewModel.taskActualDuration.value)
    }

    @Test
    fun getAllGoals_noGoals_emptyList() = runTest {
        coEvery { repository.getAllGoals() } returns emptyList()

        viewModel.getAllGoals()

        Assert.assertEquals(emptyList<Goal>(), viewModel.goals.value)
    }

    @Test
    fun getAllGoals_normalAmountOfGoals_notEmptyList() = runTest {
        val firstGoal = createDefaultGoal(0, tasks = listOf(createDefaultTask()))
        val secondGoal = createDefaultGoal(1, tasks = listOf(createDefaultTask()))
        coEvery { repository.getAllGoals() } returns listOf(firstGoal, secondGoal)

        viewModel.getAllGoals()

        Assert.assertEquals(listOf(firstGoal, secondGoal), viewModel.goals.value)
        Assert.assertEquals(2, viewModel.goals.value.size)
    }

    @Test
    fun getAllGoals_databaseError_emptyList() = runTest {
        coEvery { repository.getAllGoals() } throws SQLiteException("Database error")

        viewModel.getAllGoals()

        Assert.assertEquals(emptyList<Goal>(), viewModel.goals.value)
    }
}
