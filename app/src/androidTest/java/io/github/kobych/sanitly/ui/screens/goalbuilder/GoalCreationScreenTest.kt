package io.github.kobych.sanitly.ui.screens.goalbuilder

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import io.github.kobych.sanitly.R
import org.junit.Rule
import org.junit.Test

class GoalCreationScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun goalCreationScreen_allLabelsDisplayed() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val nameLabel = context.getString(
            R.string.goal_builder_new_goal_name_label
        )
        val categoryLabel = context.getString(
            R.string.goal_builder_new_goal_category_label
        )
        val deadlineLabel = context.getString(
            R.string.goal_builder_new_goal_deadline_label
        )
        val descriptionLabel = context.getString(
            R.string.goal_builder_new_goal_description_label
        )

        composeTestRule.setContent {
            GoalCreationScreen(
                onNextEnabled = false,
                state = GoalCreationState(
                    goalName = mutableStateOf(""),
                    goalCategory = mutableStateOf(""),
                    goalDeadline = mutableStateOf(""),
                    goalDescription = mutableStateOf(""),
                    initialDateMillis = null
                ),
                onChangeState = {}
            )
        }

        composeTestRule.onNodeWithText(nameLabel)
            .assertExists()

        composeTestRule.onNodeWithText(categoryLabel)
            .assertExists()

        composeTestRule.onNodeWithText(descriptionLabel)
            .assertExists()

        composeTestRule.onAllNodesWithText(deadlineLabel)
            .assertCountEquals(2)
    }

    @Test
    fun goalCreationScreen_clickNextButton_callsCallback() {
        var clicked = false

        composeTestRule.setContent {
            GoalCreationScreen(
                onNextEnabled = true,
                state = GoalCreationState(
                    goalName = mutableStateOf(""),
                    goalCategory = mutableStateOf(""),
                    goalDeadline = mutableStateOf(""),
                    goalDescription = mutableStateOf(""),
                    initialDateMillis = null
                ),
                onChangeState = {
                    clicked = true
                }
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Next state")
            .assertExists()

        composeTestRule
            .onNodeWithContentDescription("Next state")
            .performClick()

        assert(clicked)
    }
}