package io.github.kobych.sanitly.ui.screens.statussurvey

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import io.github.kobych.sanitly.R
import io.github.kobych.sanitly.data.database.entities.Answer
import io.github.kobych.sanitly.data.database.entities.Question
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test

class StatusSurveyScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    val answers = listOf(
        Answer(
            id = 1,
            content = "Плохо",
            questionId = 1,
        ),
        Answer(
            id = 2,
            content = "Нормально",
            questionId = 2,
        ),
        Answer(
            id = 3,
            content = "Отлично",
            questionId = 3,
        )
    )
    val question = Question(
        id = 1,
        content = "Как вы себя чувствуете?",
        answers = answers
    )
    val secondQuestion = Question(
        id = 2,
        content = "Второй вопрос",
        answers = answers
    )

    @Test
    fun statusSurveyScreen_displaysQuestionAndAnswers() {
        composeTestRule.setContent {
            StatusSurveyScreen(
                state = StatusSurveyState(
                    currentIndex = 0,
                    error = null,
                    questions = listOf(question),
                    increaseIndex = {},
                    decreaseIndex = {},
                    completeSurvey = {},
                    updateAnswer = { _, _ -> }
                )
            )
        }

        composeTestRule
            .onNodeWithText("Как вы себя чувствуете?")
            .assertExists()

        composeTestRule
            .onNodeWithText("Плохо")
            .assertExists()

        composeTestRule
            .onNodeWithText("Нормально")
            .assertExists()

        composeTestRule
            .onNodeWithText("Отлично")
            .assertExists()
    }

    @Test
    fun statusSurveyScreen_clickAnswer_indexChanges() {
        var called = false
        var receivedQuestionId = -1
        var receivedAnswer: Answer? = null

        composeTestRule.setContent {
            StatusSurveyScreen(
                state = StatusSurveyState(
                    currentIndex = 0,
                    error = null,
                    questions = listOf(question),
                    increaseIndex = {},
                    decreaseIndex = {},
                    completeSurvey = {},
                    updateAnswer = { questionId, answer ->
                        called = true
                        receivedQuestionId = questionId
                        receivedAnswer = answer
                    }
                )
            )
        }

        composeTestRule
            .onNodeWithText("Плохо")
            .assertExists()
            .performClick()

        assertTrue(called)
        assertEquals(1, receivedQuestionId)
        assertEquals(answers[0], receivedAnswer)
    }

    @Test
    fun statusSurveyScreen_notLastIndexAnswerClick_callsUpdateAndIncreaseIndex() {
        var updateCalled = false
        var indexIncreased = false

        var receivedQuestionId = 0
        var receivedAnswer: Answer? = null

        composeTestRule.setContent {
            StatusSurveyScreen(
                state = StatusSurveyState(
                    currentIndex = 0,
                    error = null,
                    questions = listOf(question, secondQuestion),
                    increaseIndex = { indexIncreased = true },
                    decreaseIndex = {},
                    completeSurvey = {},
                    updateAnswer = { id, answer ->
                        updateCalled = true
                        receivedQuestionId = id
                        receivedAnswer = answer
                    }
                )
            )
        }

        composeTestRule
            .onNodeWithText("Плохо")
            .assertExists()
            .performClick()

        assertTrue(updateCalled)
        assertEquals(1, receivedQuestionId)
        assertEquals(answers[0], receivedAnswer)
        assertTrue(indexIncreased)
    }

    @Test
    fun statusSurveyScreen_lastIndexAnswerClick_completesSurvey() {
        var completeCalled = false

        composeTestRule.setContent {
            StatusSurveyScreen(
                state = StatusSurveyState(
                    currentIndex = 0,
                    error = null,
                    questions = listOf(question),
                    increaseIndex = {},
                    decreaseIndex = {},
                    completeSurvey = { completeCalled = true },
                    updateAnswer = { _, _ -> }
                )
            )
        }

        composeTestRule
            .onNodeWithText("Плохо")
            .assertExists()
            .performClick()

        assertTrue(completeCalled)
    }

    @Test
    fun statusSurveyScreen_notFirstIndex_backButtonDisplaysAndDecreasesIndex() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val backButtonString = context.getString(R.string.common_back)

        var indexDecreased = false

        composeTestRule.setContent {
            StatusSurveyScreen(
                state = StatusSurveyState(
                    currentIndex = 1,
                    error = null,
                    questions = listOf(question, secondQuestion),
                    increaseIndex = {},
                    decreaseIndex = {
                        indexDecreased = true
                    },
                    completeSurvey = {},
                    updateAnswer = { _, _ -> }
                )
            )
        }

        composeTestRule
            .onNodeWithText(backButtonString)
            .assertExists()
            .performClick()

        assertTrue(indexDecreased)
    }

    @Test
    fun statusSurveyScreen_firstIndex_noBackButton() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val backButtonString = context.getString(R.string.common_back)

        composeTestRule.setContent {
            StatusSurveyScreen(
                state = StatusSurveyState(
                    currentIndex = 0,
                    error = null,
                    questions = listOf(question, secondQuestion),
                    increaseIndex = {},
                    decreaseIndex = {},
                    completeSurvey = {},
                    updateAnswer = { _, _ -> }
                )
            )
        }

        composeTestRule
            .onNodeWithText(backButtonString)
            .assertDoesNotExist()
    }
}
