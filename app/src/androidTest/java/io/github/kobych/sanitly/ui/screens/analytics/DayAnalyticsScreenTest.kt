package io.github.kobych.sanitly.ui.screens.analytics

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.platform.app.InstrumentationRegistry
import io.github.kobych.sanitly.R
import org.junit.Rule
import org.junit.Test

class DayAnalyticsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun dayAnalyticsScreen_displaysLabels() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val tasksCompletedString: String = context.getString(R.string.analytics_day_week_completed_tasks_label)
        val workTimeString: String = context.getString(R.string.analytics_day_week_work_time_label)
        val averageDifficultyString: String = context.getString(R.string.analytics_day_week_average_difficulty_label)
        val productivityPeakString: String = context.getString(R.string.analytics_day_productivity_peak_label)
        val longestTaskString: String = context.getString(R.string.analytics_day_longest_task_label)
        val averageMoodString: String = context.getString(R.string.analytics_day_average_mood_label)

        composeTestRule.setContent {
            DayAnalyticsScreen(
                state = DayAnalyticsState(
                    completedTasks = "2",
                    workTime = "6",
                    averageDifficulty = 3,
                    productivityPeak = 12,
                    averageMoodString = "\uD83D\uDE10",
                    longestTask = "3"
                )
            )
        }

        composeTestRule.onNodeWithText(tasksCompletedString).assertExists()
        composeTestRule.onNodeWithText(workTimeString).assertExists()
        composeTestRule.onNodeWithText(averageDifficultyString).assertExists()
        composeTestRule.onNodeWithText(productivityPeakString).assertExists()
        composeTestRule.onNodeWithText(longestTaskString).assertExists()
        composeTestRule.onNodeWithText(averageMoodString).assertExists()
    }

    @Test
    fun dayAnalyticsScreen_displaysPassedData() {
        val completedTasks = "2"
        val workTime = "6"
        val averageDifficulty = 4
        val productivityPeak = 12
        val averageMoodString = "\uD83D\uDE10"
        val longestTask = "3"

        composeTestRule.setContent {
            DayAnalyticsScreen(
                state = DayAnalyticsState(
                    completedTasks = completedTasks,
                    workTime = workTime,
                    averageDifficulty = averageDifficulty,
                    productivityPeak = productivityPeak,
                    averageMoodString = averageMoodString,
                    longestTask = longestTask
                )
            )
        }

        composeTestRule.onNodeWithText(completedTasks).assertExists()
        composeTestRule.onNodeWithText(workTime).assertExists()
        composeTestRule.onNodeWithText("$averageDifficulty").assertExists()
        composeTestRule.onNodeWithText("$productivityPeak").assertExists()
        composeTestRule.onNodeWithText(averageMoodString).assertExists()
        composeTestRule.onNodeWithText(longestTask).assertExists()
    }
}