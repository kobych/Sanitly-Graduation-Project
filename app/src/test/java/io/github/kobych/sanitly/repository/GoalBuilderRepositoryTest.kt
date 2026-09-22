package io.github.kobych.sanitly.repository

import android.database.sqlite.SQLiteException
import android.util.Log
import io.github.kobych.sanitly.data.database.dao.GoalDao
import io.github.kobych.sanitly.data.database.entities.Goal
import io.github.kobych.sanitly.data.repositories.GoalBuilderRepository
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

class GoalBuilderRepositoryTest {
    private val goalDao = mockk<GoalDao>()
    private val goalBuilderRepository = GoalBuilderRepository(goalDao)

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
        coEvery { goalDao.getAllGoals() } returns goals

        val result = goalBuilderRepository.getAllGoals()

        assertEquals(goals, result)
        coVerify(exactly = 1) { goalBuilderRepository.getAllGoals() }
    }

    @Test
    fun getAllGoals_emptyList_getsNothing() = runTest {
        val goals = emptyList<Goal>()
        coEvery { goalDao.getAllGoals() } returns goals

        val result = goalBuilderRepository.getAllGoals()

        assertEquals(emptyList<Goal>(), result)
        coVerify(exactly = 1) { goalBuilderRepository.getAllGoals() }
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
        coEvery { goalDao.getAllGoals() } returns goals

        val result = goalBuilderRepository.getAllGoals()

        assertEquals(goals, result)
        coVerify(exactly = 1) { goalBuilderRepository.getAllGoals() }
    }

    @Test
    fun getAllGoals_databaseError_emptyList() = runTest {
        coEvery { goalDao.getAllGoals() } throws SQLiteException("Database error")

        val result = goalBuilderRepository.getAllGoals()

        assertEquals(emptyList<Goal>(), result)
        coVerify(exactly = 1) { goalBuilderRepository.getAllGoals() }
    }

    @Test
    fun saveGoal_defaultGoal_savesGoal() = runTest {
        val goals = mutableListOf<Goal>()
        val goal = createDefaultGoal(1)
        coEvery { goalDao.saveGoal(any()) } answers { goals.add(goal) }
        coEvery { goalDao.getAllGoals() } returns goals

        goalBuilderRepository.saveGoal(goal)

        assertEquals(listOf(goal), goalBuilderRepository.getAllGoals())
    }

    @Test
    fun deleteGoal_defaultGoal_deletesGoal() = runTest {
        val firstGoal = createDefaultGoal(1)
        val secondGoal = createDefaultGoal(2)
        val goals = mutableListOf(firstGoal, secondGoal)
        coEvery { goalDao.deleteGoal(any()) } answers { goals.remove(firstArg()) }
        coEvery { goalDao.getAllGoals() } returns goals

        goalBuilderRepository.deleteGoal(secondGoal)

        assertEquals(listOf(firstGoal), goalBuilderRepository.getAllGoals())
    }
}
