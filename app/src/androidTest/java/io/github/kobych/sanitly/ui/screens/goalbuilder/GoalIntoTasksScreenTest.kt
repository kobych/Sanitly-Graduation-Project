package io.github.kobych.sanitly.ui.screens.goalbuilder

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import io.github.kobych.sanitly.R
import io.github.kobych.sanitly.data.database.entities.TaskDifficulty
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test

class GoalIntoTasksScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun goalIntoTasksScreen_allLabelsDisplayedAfterFabClick() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val taskTitle = context.getString(R.string.goal_new_task_title)
        val hoursLabel = context.getString(R.string.goal_new_task_hours_label)
        val difficultyLabel = context.getString(R.string.goal_new_task_difficulty_label)

        composeTestRule.setContent {
            GoalIntoTasksScreen(
                state = GoalIntoTasksState(
                    taskList = mutableStateOf(emptyList()),
                    taskName = mutableStateOf(""),
                    taskHours = mutableStateOf(""),
                    taskDifficulty = mutableStateOf(TaskDifficulty.EASY),
                    onNextEnabled = false,
                    isTaskSavable = false,
                    saveTask = {},
                    deleteTask = {}
                ),
                onChangeState = {}
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Add")
            .performClick()

        composeTestRule
            .onNodeWithText(taskTitle)
            .assertExists()

        composeTestRule
            .onNodeWithText(hoursLabel)
            .assertExists()

        composeTestRule
            .onNodeWithText(difficultyLabel)
            .assertExists()
    }

    @Test
    fun goalIntoTasksScreen_clickAdd_addsTask() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val addTaskString: String = context.getString(R.string.goal_new_task_add_btn)
        var taskSaved = false

        composeTestRule.setContent {
            GoalIntoTasksScreen(
                state = GoalIntoTasksState(
                    taskList = mutableStateOf(emptyList()),
                    taskName = mutableStateOf("Task"),
                    taskHours = mutableStateOf("1"),
                    taskDifficulty = mutableStateOf(TaskDifficulty.EASY),
                    onNextEnabled = false,
                    isTaskSavable = true,
                    saveTask = { taskSaved = true },
                    deleteTask = {}
                ),
                onChangeState = {}
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Add")
            .performClick()

        composeTestRule
            .onNodeWithText(addTaskString)
            .assertExists()
            .assertIsEnabled()
            .performClick()

        assertTrue(taskSaved)
    }

    @Test
    fun goalIntoTasksScreen_clickNext_callsOnChangeState() {
        var stateChanged = false

        composeTestRule.setContent {
            GoalIntoTasksScreen(
                state = GoalIntoTasksState(
                    taskList = mutableStateOf(emptyList()),
                    taskName = mutableStateOf(""),
                    taskHours = mutableStateOf(""),
                    taskDifficulty = mutableStateOf(TaskDifficulty.EASY),
                    onNextEnabled = true,
                    isTaskSavable = false,
                    saveTask = {},
                    deleteTask = {}
                ),
                onChangeState = { stateChanged = true }
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Next state")
            .assertExists()
            .assertIsEnabled()
            .performClick()

        assertTrue(stateChanged)
    }

    @Test
    fun goalIntoTasksScreen_clickNextDisabled_stateNotChanges() {
        var stateChanged = false

        composeTestRule.setContent {
            GoalIntoTasksScreen(
                state = GoalIntoTasksState(
                    taskList = mutableStateOf(emptyList()),
                    taskName = mutableStateOf(""),
                    taskHours = mutableStateOf(""),
                    taskDifficulty = mutableStateOf(TaskDifficulty.EASY),
                    onNextEnabled = false,
                    isTaskSavable = false,
                    saveTask = {},
                    deleteTask = {}
                ),
                onChangeState = { stateChanged = true }
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Next state")
            .assertExists()
            .performClick()

        assertFalse(stateChanged)
    }
}