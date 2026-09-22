package io.github.kobych.sanitly.ui.screens.statussurvey

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import io.github.kobych.sanitly.R
import io.github.kobych.sanitly.data.database.entities.Result
import org.junit.Rule
import org.junit.Test

class SurveyResultsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    val verdict = "Verdict Text"
    val advantageOne = "Advantage 1"
    val advantageTwo = "Advantage 2"

    val result = Result(
        id = 0,
        minPoints = 0,
        maxPoints = 5,
        verdict = verdict,
        advantages = listOf(advantageOne, advantageTwo)
    )

    @Test
    fun surveyResultsScreen_displaysAllContent() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val verdictLabel =
            context.getString(R.string.survey_results_verdict_label)

        val advantagesLabel =
            context.getString(R.string.survey_results_advantages_label)

        val startButton =
            context.getString(R.string.survey_results_start_btn)

        composeTestRule.setContent {
            SurveyResultsScreen(
                result = result,
                onFinish = {}
            )
        }

        composeTestRule
            .onNodeWithText(verdictLabel)
            .assertExists()

        composeTestRule
            .onNodeWithText(verdict)
            .assertExists()

        composeTestRule
            .onNodeWithText(advantagesLabel)
            .assertExists()

        composeTestRule
            .onNodeWithText(advantageOne)
            .assertExists()

        composeTestRule
            .onNodeWithText(advantageTwo)
            .assertExists()

        composeTestRule
            .onNodeWithText(startButton)
            .assertExists()
    }

    @Test
    fun surveyResultsScreen_clickButton_callsNextState() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val startButton =
            context.getString(R.string.survey_results_start_btn)

        var stateChanged = false

        composeTestRule.setContent {
            SurveyResultsScreen(
                result = result,
                onFinish = { stateChanged = true }
            )
        }

        composeTestRule
            .onNodeWithText(startButton)
            .performClick()

        assert(stateChanged)
    }
}
