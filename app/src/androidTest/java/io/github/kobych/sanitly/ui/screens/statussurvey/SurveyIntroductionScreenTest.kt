package io.github.kobych.sanitly.ui.screens.statussurvey

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import io.github.kobych.sanitly.data.database.entities.Introduction
import io.github.kobych.sanitly.test.R
import junit.framework.TestCase.assertFalse
import org.junit.Rule
import org.junit.Test

class SurveyIntroductionScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    val firstString = "First Text"
    val secondString = "Second Text"
    val thirdString = "Third Text"
    val introductions = listOf(
        Introduction(
            id = 0,
            text = firstString
        ),
        Introduction(
            id = 1,
            text = secondString
        ),
        Introduction(
            id = 2,
            text = thirdString
        )
    )

    @Test
    fun surveyIntroductionScreen_displaysAllStrings() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val introductionString = context.getString(io.github.kobych.sanitly.R.string.survey_introduction_title)

        composeTestRule.setContent {
            SurveyIntroductionScreen(
                onNext = {},
                introductions = introductions
            )
        }

        composeTestRule
            .onNodeWithText(introductionString)
            .assertExists()

        composeTestRule
            .onNodeWithText(firstString)
            .assertExists()

        composeTestRule
            .onNodeWithContentDescription("Next state")
            .performClick()

        composeTestRule
            .onNodeWithText(firstString)
            .assertDoesNotExist()
        composeTestRule
            .onNodeWithText(secondString)
            .assertExists()

        composeTestRule
            .onNodeWithContentDescription("Next state")
            .performClick()

        composeTestRule
            .onNodeWithText(secondString)
            .assertDoesNotExist()
        composeTestRule
            .onNodeWithText(thirdString)
            .assertExists()
    }

    @Test
    fun surveyIntroductionScreen_clickToLastSlide_callsNextState() {
        var stateChanged = false

        composeTestRule.setContent {
            SurveyIntroductionScreen(
                onNext = { stateChanged = true },
                introductions = introductions
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Next state")
            .performClick()
            .performClick()
            .performClick()

        assert(stateChanged)
    }

    @Test
    fun surveyIntroductionScreen_clickToSecondSlide_noNextState() {
        var stateChanged = false

        composeTestRule.setContent {
            SurveyIntroductionScreen(
                onNext = { stateChanged = true },
                introductions = introductions
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Next state")
            .performClick()

        assertFalse(stateChanged)
    }
}
