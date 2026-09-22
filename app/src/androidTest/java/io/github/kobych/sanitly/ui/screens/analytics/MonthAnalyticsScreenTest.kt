import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.platform.app.InstrumentationRegistry
import io.github.kobych.sanitly.R
import io.github.kobych.sanitly.ui.screens.analytics.MonthAnalyticsScreen
import io.github.kobych.sanitly.ui.screens.analytics.MonthAnalyticsState
import org.junit.Rule
import org.junit.Test

class MonthAnalyticsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun monthAnalyticsScreen_displaysLabels() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val completionLabel =
            context.getString(R.string.analytics_month_completion_label)
        val moodRatioLabel =
            context.getString(R.string.analytics_month_mood_ratio_label)
        val activeDaysLabel =
            context.getString(R.string.analytics_month_active_days_label)
        val moodDynamicsLabel =
            context.getString(R.string.analytics_month_mood_dynamics_label)

        composeTestRule.setContent {
            MonthAnalyticsScreen(
                state = MonthAnalyticsState(
                    tasksByDays = emptyList(),
                    getColorForTasks = { 0.5f },
                    completionPercentage = 50,
                    moodPercentage = 60,
                    activeDaysPercentage = 70,
                    moodByDays = emptyList()
                )
            )
        }

        composeTestRule.onNodeWithText(completionLabel)
            .assertExists()

        composeTestRule.onNodeWithText(moodRatioLabel)
            .assertExists()

        composeTestRule.onNodeWithText(activeDaysLabel)
            .assertExists()

        composeTestRule.onNodeWithText(moodDynamicsLabel)
            .assertExists()
    }

    @Test
    fun monthAnalyticsScreen_displaysPassedData() {
        val completionPercentage = 50
        val moodPercentage = 60
        val activeDaysPercentage = 70

        composeTestRule.setContent {
            MonthAnalyticsScreen(
                state = MonthAnalyticsState(
                    tasksByDays = emptyList(),
                    getColorForTasks = { 0.5f },
                    completionPercentage = completionPercentage,
                    moodPercentage = moodPercentage,
                    activeDaysPercentage = activeDaysPercentage,
                    moodByDays = emptyList()
                )
            )
        }

        composeTestRule.onNodeWithText(
            "$completionPercentage%"
        ).assertExists()

        composeTestRule.onNodeWithText(
            "$moodPercentage%"
        ).assertExists()

        composeTestRule.onNodeWithText(
            "$activeDaysPercentage%"
        ).assertExists()
    }
}
