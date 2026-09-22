package io.github.kobych.sanitly.repository

import android.database.sqlite.SQLiteException
import android.util.Log
import io.github.kobych.sanitly.data.database.dao.GoalDao
import io.github.kobych.sanitly.data.database.entities.Goal
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

class GoalListRepositoryTest {
    private val goalDao = mockk<GoalDao>()
    private val goalListRepository = GoalListRepository(goalDao)

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
    fun updateSeconds_zeroSeconds_updatesNothing() {
        val secondsAmount = 0

        goalListRepository.updateSeconds(secondsAmount)

        assertEquals(0, goalListRepository.currentSeconds.value)
    }

    @Test
    fun updateSeconds_tenSeconds_updatesSeconds() {
        val secondsAmount = 10

        goalListRepository.updateSeconds(secondsAmount)

        assertEquals(10, goalListRepository.currentSeconds.value)
    }

    @Test
    fun updateSeconds_negativeSeconds_updatesNothing() {
        val secondsAmount = -10

        goalListRepository.updateSeconds(secondsAmount)

        assertEquals(0, goalListRepository.currentSeconds.value)
    }

    @Test
    fun getAllGoals_oneGoal_getsListSuccessfully() = runTest {
        val goals = listOf(createDefaultGoal(1))
        coEvery { goalDao.getAllGoals() } returns goals

        val result = goalListRepository.getAllGoals()

        assertEquals(goals, result)
        coVerify(exactly = 1) { goalListRepository.getAllGoals() }
    }

    @Test
    fun getAllGoals_emptyList_getsNothing() = runTest {
        val goals = emptyList<Goal>()
        coEvery { goalDao.getAllGoals() } returns goals

        val result = goalListRepository.getAllGoals()

        assertEquals(emptyList<Goal>(), result)
        coVerify(exactly = 1) { goalListRepository.getAllGoals() }
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

        val result = goalListRepository.getAllGoals()

        assertEquals(goals, result)
        coVerify(exactly = 1) { goalListRepository.getAllGoals() }
    }

    @Test
    fun getAllGoals_databaseError_emptyList() = runTest {
        coEvery { goalDao.getAllGoals() } throws SQLiteException("Database error")

        val result = goalListRepository.getAllGoals()

        assertEquals(emptyList<Goal>(), result)
        coVerify(exactly = 1) { goalListRepository.getAllGoals() }
    }

    @Test
    fun completeGoal_defaultGoal_completesGoal() = runTest {
        val goal = createDefaultGoal(1)
        coEvery { goalDao.completeGoal(goal) } returns Unit

        goalListRepository.completeGoal(goal)

        coVerify(exactly = 1) { goalDao.completeGoal(goal) }
    }

    @Test
    fun updateGoal_defaultGoal_updatesGoal() = runTest {
        val goal = createDefaultGoal(1)
        coEvery { goalDao.updateGoal(goal) } returns Unit

        goalListRepository.updateGoal(goal)

        coVerify(exactly = 1) { goalDao.updateGoal(goal) }
    }

    @Test
    fun setTimerRunning_makeTimerRunningTrue_enablesTimer() {
        goalListRepository.setTimerRunning(true)

        assertEquals(true, goalListRepository.isTimerRunning.value)
    }
}
