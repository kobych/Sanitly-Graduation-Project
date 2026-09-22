package io.github.kobych.sanitly.viewmodel

import android.database.sqlite.SQLiteException
import android.util.Log
import io.github.kobych.sanitly.MainDispatcherRule
import io.github.kobych.sanitly.util.createDefaultGoal
import io.github.kobych.sanitly.util.createDefaultTask
import io.github.kobych.sanitly.data.database.entities.Goal
import io.github.kobych.sanitly.data.repositories.DiaryRepository
import io.github.kobych.sanitly.ui.models.DiaryStepsState
import io.github.kobych.sanitly.ui.viewmodels.DiaryViewModel
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

class DiaryViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<DiaryRepository>()
    private lateinit var viewModel: DiaryViewModel

    @Before
    fun setup() {
        viewModel = DiaryViewModel(repository)
        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }


    @Test
    fun canNoteExpand_emptyNotes_returnsFalse() {
        val goals = listOf(createDefaultGoal(0, tasks = listOf(createDefaultTask())))
        coEvery { repository.getAllGoals() } returns goals

        val canNoteExpand = viewModel.canNoteExpand(goals[0])
        assertEquals(false, canNoteExpand)
    }

    @Test
    fun canNoteExpand_normalAmountOfNotes_returnsTrue() {
        val goals = listOf(
            createDefaultGoal(
                id = 0,
                isCompleted = true,
                successNote = "Test",
                failureNote = "Test",
                summaryNote = "Test",
                tasks = listOf(createDefaultTask())
            )
        )
        coEvery { repository.getAllGoals() } returns goals

        val canNoteExpand = viewModel.canNoteExpand(goals.first())
        assertEquals(true, canNoteExpand)
    }

    @Test
    fun saveNotes_selectedGoalIsNull_nothingSaved() {
        viewModel.saveNotes()

        assertEquals(null, viewModel.selectedGoal.value)
    }

    @Test
    fun saveNotes_goalIsCompleted_selectedGoalNotesSaved() {
        val goals = listOf(
            createDefaultGoal(
                id = 0,
                isCompleted = true,
                successNote = "Test",
                failureNote = "Test",
                summaryNote = "Test",
                tasks = listOf(createDefaultTask())
            )
        )
        coEvery { repository.getAllGoals() } returns goals
        coEvery { repository.saveNote(any()) } returns Unit

        viewModel.selectGoal(goals.first())
        viewModel.saveNotes()

        assertEquals(goals.first(), viewModel.selectedGoal.value)
        assertEquals(goals.first().successNote, viewModel.selectedGoal.value?.successNote)
        assertEquals(goals.first().failureNote, viewModel.selectedGoal.value?.failureNote)
        assertEquals(goals.first().summaryNote, viewModel.selectedGoal.value?.summaryNote)
        coVerify(exactly = 1) { repository.saveNote(any()) }
    }

    @Test
    fun saveNotes_goalIsNotCompleted_selectedGoalNotesSaved() {
        val goals = listOf(
            createDefaultGoal(
                id = 0,
                isCompleted = false,
                successNote = "",
                failureNote = "",
                summaryNote = "",
                tasks = listOf(createDefaultTask())
            )
        )
        coEvery { repository.getAllGoals() } returns goals
        coEvery { repository.saveNote(any()) } returns Unit

        viewModel.selectGoal(goals.first())
        viewModel.saveNotes()

        assertEquals(goals.first(), viewModel.selectedGoal.value)
        assertEquals("", viewModel.selectedGoal.value?.successNote)
        assertEquals("", viewModel.selectedGoal.value?.failureNote)
        assertEquals("", viewModel.selectedGoal.value?.summaryNote)
        coVerify(exactly = 0) { repository.saveNote(any()) }
    }

    @Test
    fun isButtonAvailable_selectedGoalIsNotCompleted_returnsFalse() {
        val goals = listOf(
            createDefaultGoal(
                id = 0,
                isCompleted = false,
                tasks = listOf(createDefaultTask())
            )
        )
        coEvery { repository.getAllGoals() } returns goals

        viewModel.selectGoal(goals.first())

        val isButtonAvailable = viewModel.isButtonAvailable()
        assertEquals(false, isButtonAvailable)
    }

    @Test
    fun isButtonAvailable_selectedGoalIsCompleted_returnsTrue() {
        val goals = listOf(
            createDefaultGoal(
                id = 0,
                isCompleted = true,
                tasks = listOf(createDefaultTask())
            )
        )
        coEvery { repository.getAllGoals() } returns goals

        viewModel.selectGoal(goals.first())

        val isButtonAvailable = viewModel.isButtonAvailable()
        assertEquals(true, isButtonAvailable)
    }

    @Test
    fun selectGoal_normalGoalPassed_statesUpdated() {
        val goal = createDefaultGoal(
            id = 0,
            isCompleted = true,
            successNote = "Test",
            failureNote = "Test",
            summaryNote = "Test",
            tasks = listOf(createDefaultTask())
        )

        viewModel.selectGoal(goal)

        assertEquals(viewModel.selectedGoal.value, goal)
        assertEquals(viewModel.selectedGoal.value?.successNote, goal.successNote)
        assertEquals(viewModel.selectedGoal.value?.failureNote, goal.failureNote)
        assertEquals(viewModel.selectedGoal.value?.summaryNote, goal.summaryNote)
    }

    @Test
    fun getAllGoals_noGoals_emptyList() = runTest {
        coEvery { repository.getAllGoals() } returns emptyList()

        viewModel.getAllGoals()

        assertEquals(emptyList<Goal>(), viewModel.goals.value)
    }

    @Test
    fun getAllGoals_normalAmountOfGoals_notEmptyList() = runTest {
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
    fun updateDiaryState_normalStateChange_changesState() = runTest {
        viewModel.updateDiaryState()

        assertEquals(DiaryStepsState.GoalNote, viewModel.uiState.value)
    }
}
