package io.github.kobych.sanitly.ui.screens.analytics

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.platform.app.InstrumentationRegistry
import io.github.kobych.sanitly.R
import io.github.kobych.sanitly.ui.viewmodels.DayMood
import io.github.kobych.sanitly.ui.viewmodels.DayProgress
import io.github.kobych.sanitly.ui.viewmodels.DaySummary
import org.junit.Rule
import org.junit.Test

class WeekAnalyticsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun weekAnalyticsScreen_displaysLabels() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val completedTasks = context.getString(R.string.analytics_day_week_completed_tasks_label)
        val workTime = context.getString(R.string.analytics_day_week_work_time_label)
        val completionPercent = context.getString(R.string.analytics_day_week_percentage_label)
        val averageDifficulty = context.getString(R.string.analytics_day_week_average_difficulty_label)
        val tasksByDays = context.getString(R.string.analytics_week_tasks_by_days_label)
        val moodByDays = context.getString(R.string.analytics_week_mood_by_days_label)

        composeTestRule.setContent {
            WeekAnalyticsScreen(
                state = WeekAnalyticsState(
                    completedTasks = "2",
                    workTime = "6",
                    completionPercent = 80,
                    averageDifficulty = 4,
                    tasksByDays = emptyList(),
                    moodByDays = emptyList(),
                    bestDay = DaySummary("Monday", 5, 2.0, "😊"),
                    worstDay = DaySummary("Tuesday", 1, 1.0, "😐")
                )
            )
        }

        composeTestRule.onNodeWithText(completedTasks).assertExists()
        composeTestRule.onNodeWithText(workTime).assertExists()
        composeTestRule.onNodeWithText(completionPercent).assertExists()
        composeTestRule.onNodeWithText(averageDifficulty).assertExists()
        composeTestRule.onNodeWithText(tasksByDays).assertExists()
        composeTestRule.onNodeWithText(moodByDays).assertExists()
    }

    @Test
    fun weekAnalyticsScreen_displaysPassedData() {
        val completedTasks = "2"
        val workTime = "6"
        val completionPercent = 80
        val averageDifficulty = 4

        val taskProgress = DayProgress(
            dayName = "Monday",
            completedTasks = 3,
            failedTasks = 2,
            totalTasks = 10
        )
        val mood = DayMood(
            dayName = "Tuesday",
            averageMood = "2.5",
            emoji = "😊"
        )

        val bestDay = DaySummary("Monday", 5, 2.0, "😊")
        val worstDay = DaySummary("Tuesday", 1, 1.0, "😐")

        composeTestRule.setContent {
            WeekAnalyticsScreen(
                state = WeekAnalyticsState(
                    completedTasks = completedTasks,
                    workTime = workTime,
                    completionPercent = completionPercent,
                    averageDifficulty = averageDifficulty,
                    tasksByDays = listOf(taskProgress),
                    moodByDays = listOf(mood),
                    bestDay = bestDay,
                    worstDay = worstDay
                )
            )
        }

        composeTestRule.onNodeWithText(completedTasks).assertExists()
        composeTestRule.onNodeWithText(workTime).assertExists()
        composeTestRule.onNodeWithText("$completionPercent%").assertExists()
        composeTestRule.onNodeWithText(averageDifficulty.toString()).assertExists()

        composeTestRule.onNodeWithText(taskProgress.dayName).assertExists()
        composeTestRule.onNodeWithText(taskProgress.completedTasks.toString()).assertExists()

        composeTestRule.onNodeWithText(mood.dayName).assertExists()
        composeTestRule.onNodeWithText(mood.emoji).assertExists()

        composeTestRule.onNodeWithText(bestDay.dayName).assertExists()
        composeTestRule.onNodeWithText(worstDay.dayName).assertExists()
    }
}