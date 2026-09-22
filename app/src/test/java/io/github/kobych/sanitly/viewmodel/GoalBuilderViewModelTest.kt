package io.github.kobych.sanitly.viewmodel

import android.database.sqlite.SQLiteException
import android.util.Log
import io.github.kobych.sanitly.MainDispatcherRule
import io.github.kobych.sanitly.util.createDefaultGoal
import io.github.kobych.sanitly.util.createDefaultTask
import io.github.kobych.sanitly.data.database.entities.Goal
import io.github.kobych.sanitly.data.database.entities.TaskDifficulty
import io.github.kobych.sanitly.data.repositories.GoalBuilderRepository
import io.github.kobych.sanitly.ui.models.GoalBuilderStepsState
import io.github.kobych.sanitly.ui.viewmodels.GoalBuilderViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.collections.emptyList

class GoalBuilderViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<GoalBuilderRepository>()
    private lateinit var viewModel: GoalBuilderViewModel

    @Before
    fun setup() {
        viewModel = GoalBuilderViewModel(repository)

        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }


    @Test
    fun updateGoalBuilderState_passNormalState_stateChangesCorrect() {
        viewModel.updateGoalBuilderState()

        assertEquals(GoalBuilderStepsState.Creation, viewModel.uiState.value)
    }

    @Test
    fun updateGoalBuilderState_goNextPreviousAndNextState_stateChangesCorrect() {
        viewModel.updateGoalBuilderState()
        viewModel.goPreviousGoalBuilderState()
        viewModel.updateGoalBuilderState()

        assertEquals(GoalBuilderStepsState.Creation, viewModel.uiState.value)
    }

    @Test
    fun goPreviousGoalBuilderState_remainBuilderState_stateRemainedBuilder() {
        viewModel.goPreviousGoalBuilderState()

        assertEquals(GoalBuilderStepsState.Builder, viewModel.uiState.value)
    }

    @Test
    fun goPreviousGoalBuilderState_goNextState_stateChangesToPrevious() {
        viewModel.updateGoalBuilderState()
        viewModel.goPreviousGoalBuilderState()

        assertEquals(GoalBuilderStepsState.Builder, viewModel.uiState.value)
    }

    @Test
    fun goPreviousGoalBuilderState_goNextStateThreeTimes_stateChangesToPrevious() {
        viewModel.updateGoalBuilderState()
        viewModel.updateGoalBuilderState()
        viewModel.updateGoalBuilderState()
        viewModel.goPreviousGoalBuilderState()

        assertEquals(GoalBuilderStepsState.GoalIntoTasks, viewModel.uiState.value)
    }

    @Test
    fun goPreviousGoalBuilderState_goFullRouteToBuilder_stateRemainsBuilder() {
        viewModel.updateGoalBuilderState()
        viewModel.updateGoalBuilderState()
        viewModel.updateGoalBuilderState()
        viewModel.goPreviousGoalBuilderState()

        assertEquals(GoalBuilderStepsState.GoalIntoTasks, viewModel.uiState.value)
    }

    @Test
    fun getAllGoals_noGoals_emptyList() = runTest {
        coEvery { repository.getAllGoals() } returns emptyList()

        viewModel.getAllGoals()

        assertEquals(emptyList<Goal>(), viewModel.goals.value)
    }

    @Test
    fun getAllGoals_normalAmountOfGoals_emptyList() = runTest {
        val firstGoal = createDefaultGoal(0, tasks = listOf(createDefaultTask()))
        val secondGoal = createDefaultGoal(1, tasks = listOf(createDefaultTask()))
        coEvery { repository.getAllGoals() } returns listOf(firstGoal, secondGoal)

        viewModel.getAllGoals()

        assertEquals(listOf(firstGoal, secondGoal), viewModel.goals.value)
        assertEquals(2, viewModel.goals.value.size)
    }

    @Test
    fun getAllGoals_databaseError_emptyList() = runTest {
        coEvery { repository.getAllGoals() } throws SQLiteException("Database error")

        viewModel.getAllGoals()

        assertEquals(emptyList<Goal>(), viewModel.goals.value)
    }

    @Test
    fun deleteGoal_normalAmountOfGoals_selectedGoalDeleted() = runTest {
        val firstGoal = createDefaultGoal(0, tasks = listOf(createDefaultTask()))
        val secondGoal = createDefaultGoal(1, tasks = listOf(createDefaultTask()))
        val thirdGoal = createDefaultGoal(2, tasks = listOf(createDefaultTask()))
        coEvery { repository.getAllGoals() } returnsMany listOf(
            listOf(firstGoal, secondGoal, thirdGoal),
            listOf(firstGoal, secondGoal)
        )
        coEvery { repository.deleteGoal(any()) } returns Unit

        viewModel.getAllGoals()
        viewModel.deleteGoal(thirdGoal)
        viewModel.getAllGoals()

        assertEquals(listOf(firstGoal, secondGoal), viewModel.goals.value)
        assertEquals(2, viewModel.goals.value.size)
    }

    @Test
    fun deleteGoal_deletesOnlyGoal_emptyList() = runTest {
        val firstGoal = createDefaultGoal(0, tasks = listOf(createDefaultTask()))
        coEvery { repository.getAllGoals() } returnsMany listOf(listOf(firstGoal), emptyList())
        coEvery { repository.deleteGoal(any()) } returns Unit

        viewModel.getAllGoals()
        viewModel.deleteGoal(firstGoal)
        viewModel.getAllGoals()

        assertEquals(emptyList<Goal>(), viewModel.goals.value)
        assertEquals (0, viewModel.goals.value.size)
    }

    @Test
    fun createAndSaveGoal_defaultGoal_goalSaved() = runTest {
        val goal = createDefaultGoal(0, tasks = listOf(createDefaultTask()))
        coEvery { repository.saveGoal(any()) } returns Unit
        coEvery { repository.getAllGoals() } returns listOf(goal)

        viewModel.createAndSaveGoal()
        viewModel.getAllGoals()

        assertEquals(listOf(goal), viewModel.goals.value)
        assertEquals (1, viewModel.goals.value.size)
        coVerify(exactly = 1) { repository.saveGoal(any()) }
    }

    @Test
    fun createAndSaveGoal_multipleTasks_savesGoalWithAggregatedHours() = runTest {
        coEvery { repository.saveGoal(any()) } returns Unit

        viewModel.goalName.value = "Fitness Goal"
        viewModel.goalCategory.value = "Health"
        viewModel.goalDeadline.value = "1000"
        viewModel.goalDescription.value = "Get fit"

        viewModel.taskName.value = "Running"
        viewModel.taskHours.value = "2"
        viewModel.taskDifficulty.value = TaskDifficulty.VERY_EASY
        viewModel.goalIntoTasksState.saveTask()

        viewModel.taskName.value = "Swimming"
        viewModel.taskHours.value = "3"
        viewModel.taskDifficulty.value = TaskDifficulty.VERY_EASY
        viewModel.goalIntoTasksState.saveTask()

        viewModel.createAndSaveGoal()

        val expectedTask1 = createDefaultTask(
            name = "Running",
            hours = 2L,
            difficulty = TaskDifficulty.VERY_EASY,
            actualHours = 0L,
            actualMinutes = 0,
            actualSeconds = 0,
            completedAt = 0L
        )
        val expectedTask2 = createDefaultTask(
            name = "Swimming",
            hours = 3L,
            difficulty = TaskDifficulty.VERY_EASY,
            actualHours = 0L,
            actualMinutes = 0,
            actualSeconds = 0,
            completedAt = 0L
        )
        val expectedGoal = createDefaultGoal(
            id = 0,
            goalName = "Fitness Goal",
            goalCategory = "Health",
            hours = 5L,
            deadline = 1000L,
            description = "Get fit",
            tasks = listOf(expectedTask1, expectedTask2)
        )

        coVerify(exactly = 1) { repository.saveGoal(expectedGoal) }
    }

    @Test
    fun deleteTask_multipleTasks_removesOnlySelectedTask() = runTest {
        viewModel.taskName.value = "Keep Me"
        viewModel.taskHours.value = "2"
        viewModel.taskDifficulty.value = TaskDifficulty.VERY_EASY
        viewModel.goalIntoTasksState.saveTask()

        viewModel.taskName.value = "Delete Me"
        viewModel.taskHours.value = "4"
        viewModel.taskDifficulty.value = TaskDifficulty.VERY_EASY
        viewModel.goalIntoTasksState.saveTask()

        val taskToDelete = viewModel.taskList.value.last()
        viewModel.goalIntoTasksState.deleteTask(taskToDelete)

        val expectedTask = createDefaultTask(
            name = "Keep Me",
            hours = 2L,
            difficulty = TaskDifficulty.VERY_EASY,
            actualHours = 0L,
            actualMinutes = 0,
            actualSeconds = 0,
            completedAt = 0L
        )

        assertEquals(1, viewModel.taskList.value.size)
        assertEquals(expectedTask, viewModel.taskList.value.first())
    }
}
