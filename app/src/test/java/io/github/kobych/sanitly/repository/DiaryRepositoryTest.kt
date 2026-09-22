package io.github.kobych.sanitly.repository

import android.database.sqlite.SQLiteException
import android.util.Log
import io.github.kobych.sanitly.data.database.entities.Goal
import io.github.kobych.sanitly.data.repositories.DiaryRepository
import io.github.kobych.sanitly.data.repositories.GoalListRepository
import io.github.kobych.sanitly.util.createDefaultGoal
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class DiaryRepositoryTest {
    private val goalProcessRepository = mockk<GoalListRepository>()
    private val diaryRepository = DiaryRepository(goalProcessRepository)

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    @Test
    fun getAllGoals_oneGoal_getsListSuccessfully() = runTest {
        val goals = listOf(createDefaultGoal(1))
        coEvery { goalProcessRepository.getAllGoals() } returns goals

        val result = diaryRepository.getAllGoals()

        assertEquals(goals, result)
        coVerify(exactly = 1) { goalProcessRepository.getAllGoals() }
    }

    @Test
    fun getAllGoals_emptyList_getsNothing() = runTest {
        val goals = emptyList<Goal>()
        coEvery { goalProcessRepository.getAllGoals() } returns goals

        val result = diaryRepository.getAllGoals()

        assertEquals(emptyList<Goal>(), result)
        coVerify(exactly = 1) { goalProcessRepository.getAllGoals() }
    }

    @Test
    fun getAllGoals_severalGoals_getsFullList() = runTest {
        val goals = listOf(
            createDefaultGoal(1),
            createDefaultGoal(2),
            createDefaultGoal(3),
            createDefaultGoal(4),
            createDefaultGoal(5)
        )
        coEvery { goalProcessRepository.getAllGoals() } returns goals

        val result = diaryRepository.getAllGoals()

        assertEquals(goals, result)
        assertEquals(goals.size, result.size)
        coVerify(exactly = 1) { goalProcessRepository.getAllGoals() }
    }

    @Test
    fun getAllGoals_databaseError_emptyList() = runTest {
        coEvery { goalProcessRepository.getAllGoals() } throws SQLiteException("Database error")

        val result = diaryRepository.getAllGoals()

        assertEquals(emptyList<Goal>(), result)
        coVerify(exactly = 1) { goalProcessRepository.getAllGoals() }
    }

    @Test
    fun saveNote_defaultGoal_savesGoal() = runTest {
        val goals = mutableListOf<Goal>()
        val goal = createDefaultGoal(1)
        coEvery { goalProcessRepository.getAllGoals() } returns goals
        coEvery { goalProcessRepository.updateGoal(any()) } answers { goals.add(goal) }

        diaryRepository.saveNote(goal)

        assertEquals(listOf(goal), diaryRepository.getAllGoals())
    }
}
