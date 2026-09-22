package io.github.kobych.sanitly.ui.screens.goalbuilder

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test

class GoalCreatedScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun goalCreatedScreen_allLabelsAndButtonDisplayed() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val goalCreatedTitle = context.getString(io.github.kobych.sanitly.R.string.goal_created_title)
        val goalCreatedLabel = context.getString(io.github.kobych.sanitly.R.string.goal_created_label)
        val startGoalLabel = context.getString(io.github.kobych.sanitly.R.string.goal_created_start_goal_btn)
        val toGoalBuilderLabel = context.getString(io.github.kobych.sanitly.R.string.goal_created_to_goal_builder_btn)

        composeTestRule.setContent {
            GoalCreatedScreen(
                onCreatedToGoals = {},
                createAndSaveGoal = {},
                onChangeState = {},
            )
        }

        composeTestRule.onNodeWithText(goalCreatedTitle).assertExists()
        composeTestRule.onNodeWithText(goalCreatedLabel).assertExists()
        composeTestRule.onNodeWithText(startGoalLabel).assertExists()
        composeTestRule.onNodeWithText(toGoalBuilderLabel).assertExists()
    }

    @Test
    fun goalCreatedScreen_clickStartGoal_returnsCallback() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val startGoalLabel = context.getString(io.github.kobych.sanitly.R.string.goal_created_start_goal_btn)
        var createAndSaveGoalCalled = false
        var onCreatedToGoalsCalled = false

        composeTestRule.setContent {
            GoalCreatedScreen(
                onCreatedToGoals = { onCreatedToGoalsCalled = true },
                createAndSaveGoal = { createAndSaveGoalCalled = true },
                onChangeState = {},
            )
        }

        composeTestRule.onNodeWithText(startGoalLabel).performClick()

        assert(createAndSaveGoalCalled)
        assert(onCreatedToGoalsCalled)
    }
}