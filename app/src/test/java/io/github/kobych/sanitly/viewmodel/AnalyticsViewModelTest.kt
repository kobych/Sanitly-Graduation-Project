package io.github.kobych.sanitly.viewmodel

import android.database.sqlite.SQLiteException
import android.util.Log
import io.github.kobych.sanitly.MainDispatcherRule
import io.github.kobych.sanitly.util.createDefaultGoal
import io.github.kobych.sanitly.util.createDefaultTask
import io.github.kobych.sanitly.data.database.entities.Goal
import io.github.kobych.sanitly.data.repositories.AnalyticsRepository
import io.github.kobych.sanitly.ui.models.AnalyticsState
import io.github.kobych.sanitly.ui.viewmodels.AnalyticsViewModel
import io.mockk.coEvery
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
import java.time.LocalDate
import java.time.ZoneId

class AnalyticsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<AnalyticsRepository>()
    private lateinit var viewModel: AnalyticsViewModel

    @Before
    fun setup() {
        viewModel = AnalyticsViewModel(repository)
        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }


    @Test
    fun getColorForTasks_zeroOrNegativeTasks_returnsNoProgressAlpha() {
        val result = viewModel.getColorForTasks(-1)

        assertEquals(0f, result)
    }

    @Test
    fun getColorForTasks_normalAmountOfTasks_returnsCalculatedAlpha() {
        val result = viewModel.getColorForTasks(3)

        assertEquals(0.6f, result)
    }

    @Test
    fun getColorForTasks_moreThanMaxIntensity_returnsFullProgressAlpha() {
        val result = viewModel.getColorForTasks(10)

        assertEquals(1f, result)
    }

    @Test
    fun getAllTasks_noGoals_emptyStats() = runTest {
        coEvery { repository.getAllGoals() } returns emptyList()

        viewModel.getAllTasks()

        assertEquals(emptyList<Goal>(), viewModel.goals.value)
    }

    @Test
    fun getAllTasks_normalAmountOfGoals_normalStats() = runTest {
        val firstGoal = createDefaultGoal(0, tasks = listOf(createDefaultTask()))
        val secondGoal = createDefaultGoal(1, tasks = listOf(createDefaultTask()))
        val thirdGoal = createDefaultGoal(3, tasks = listOf(createDefaultTask()))
        coEvery { repository.getAllGoals() } returns listOf(firstGoal, secondGoal, thirdGoal)

        viewModel.getAllTasks()

        assertEquals(listOf(firstGoal, secondGoal, thirdGoal), viewModel.goals.value)
        assertEquals(3, viewModel.tasks.value.size)
    }

    @Test
    fun updateAnalyticsState_tasksCompletedOnDifferentWeeks_onlyOneTasksFiltered() = runTest {
        val lastWeekTaskCompletedAt =
            LocalDate.now().minusWeeks(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val thisWeekTaskCompletedAt = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val goal = listOf(
            Goal(
                id = 0,
                name = "Goal",
                category = "Category",
                hours = 1L,
                deadline = 1L,
                description = "",
                tasks = listOf(
                    createDefaultTask(completedAt = lastWeekTaskCompletedAt),
                    createDefaultTask(completedAt = thisWeekTaskCompletedAt)
                ),
                isCompleted = false,
                successNote = "",
                failureNote = "",
                summaryNote = ""
            )
        )
        coEvery { repository.getAllGoals() } returns goal

        viewModel.updateAnalyticsState(AnalyticsState.Week)
        viewModel.getAllTasks()

        assertEquals(1, viewModel.completedTasks.value.toInt())
    }

    @Test
    fun updateAnalyticsState_tasksCompletedOnPreviousWeeks_noTasksFiltered() = runTest {
        val lastWeekTaskCompletedAt = LocalDate.now()
            .minusWeeks(1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        val twoWeekAgoTaskCompletedAt = LocalDate.now()
            .minusWeeks(2)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        val goal = listOf(
            Goal(
                id = 0,
                name = "Goal",
                category = "Category",
                hours = 1L,
                deadline = 1L,
                description = "",
                tasks = listOf(
                    createDefaultTask(completedAt = lastWeekTaskCompletedAt),
                    createDefaultTask(completedAt = twoWeekAgoTaskCompletedAt)
                ),
                isCompleted = false,
                successNote = "",
                failureNote = "",
                summaryNote = ""
            )
        )
        coEvery { repository.getAllGoals() } returns goal

        viewModel.updateAnalyticsState(AnalyticsState.Week)
        viewModel.getAllTasks()

        assertEquals(0, viewModel.completedTasks.value.toInt())
    }

    @Test
    fun updateAnalyticsState_noGoals_nothingFiltered() = runTest {
        coEvery { repository.getAllGoals() } throws SQLiteException("Database error")

        viewModel.updateAnalyticsState(AnalyticsState.Week)
        viewModel.getAllTasks()

        val result = viewModel.completedTasks.value.toIntOrNull() ?: 0
        assertEquals(0, result)
    }
}
