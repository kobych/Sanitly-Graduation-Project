package io.github.kobych.sanitly.ui.screens.goalbuilder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.kobych.sanitly.R
import io.github.kobych.sanitly.data.database.entities.Task
import io.github.kobych.sanitly.data.database.entities.TaskDifficulty
import io.github.kobych.sanitly.data.database.entities.TaskMood
import io.github.kobych.sanitly.ui.components.alerts.CustomAlertDialog
import io.github.kobych.sanitly.ui.components.buttons.SingleChoiceSegmentedButton
import io.github.kobych.sanitly.ui.theme.Dimens
import io.github.kobych.sanitly.ui.theme.Size
import io.github.kobych.sanitly.ui.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalIntoTasksScreen(
    state: GoalIntoTasksState,
    onChangeState: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showTaskCreation by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(Dimens.L.padding)
    ) {
        Column {
            Text(
                text = stringResource(R.string.goal_into_tasks_title),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(modifier = Modifier.height(Dimens.S.padding))

            TaskItemRow(state = state)
            Spacer(modifier = Modifier.height(Dimens.XL.padding))
        }
        Column(modifier = Modifier.align(Alignment.BottomEnd)) {
            FloatingActionButton(
                onClick = { showTaskCreation = !showTaskCreation },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .align(Alignment.End)
                    .size(Size.Button.XL.dp)
            ) {
                Icon(Icons.Default.Add, "Add")
            }
            Spacer(modifier = Modifier.height(Dimens.XL.padding))
            Button(
                onClick = { onChangeState() },
                shape = CircleShape,
                enabled = state.onNextEnabled,
                modifier = Modifier.size(Size.Button.XL.dp)
            ) { Icon(Icons.AutoMirrored.Filled.ArrowForward, "Next state") }
        }


        if (showTaskCreation) {
            TaskCreationSheet(
                TaskCreationSheetState(
                    taskName = state.taskName,
                    taskHours = state.taskHours,
                    taskDifficulty = state.taskDifficulty,
                    isTaskSavable = state.isTaskSavable
                ),
                saveTask = state.saveTask
            ) { showTaskCreation = false }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskCreationSheet(
    state: TaskCreationSheetState,
    saveTask: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .padding(Dimens.L.padding)
        ) {
            TaskCreationItems(state = state)

            Row {
                Button(
                    onClick = { onDismiss() },
                    shape = MaterialTheme.shapes.small,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(Size.Button.XL.dp)
                ) {
                    Text(text = stringResource(R.string.common_cancel), style = Typography.labelLarge)
                }
                Spacer(modifier = Modifier.width(Dimens.S.padding))
                Button(
                    enabled = state.isTaskSavable,
                    onClick = {
                        saveTask()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier
                        .weight(1f)
                        .height(Size.Button.XL.dp)
                ) {
                    Text(text = stringResource(R.string.goal_new_task_add_btn), style = Typography.labelLarge)
                }
            }
        }
    }
}

@Composable
private fun TaskCreationItems(
    state: TaskCreationSheetState
) {
    Text(
        text = stringResource(R.string.goal_new_task_title),
        style = Typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(Dimens.S.padding))

    OutlinedTextField(
        value = state.taskName.value,
        onValueChange = { state.taskName.value = it },
        label = {
            Text(
                text = stringResource(R.string.goal_new_task_name_hint),
                style = Typography.labelLarge
            )
        },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(Dimens.XL.padding))

    Text(
        text = stringResource(R.string.goal_new_task_hours_label),
        style = Typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(Dimens.S.padding))

    OutlinedTextField(
        value = state.taskHours.value,
        onValueChange = { state.taskHours.value = it },
        label = {
            Text(
                text = stringResource(R.string.goal_new_task_hours_hint),
                style = Typography.labelLarge
            )
        },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(Dimens.XL.padding))

    Text(text = stringResource(R.string.goal_new_task_difficulty_label), style = Typography.labelLarge)
    Spacer(modifier = Modifier.height(Dimens.S.padding))
    SingleChoiceSegmentedButton(
        options = TaskDifficulty.entries,
        onClick = { state.taskDifficulty.value = it },
        labelText = { it.emoji },
    )
    Spacer(modifier = Modifier.height(Dimens.XL.padding))
}

@Composable
private fun TaskItemRow(
    state: GoalIntoTasksState,
) {
    val openDialog = remember { mutableStateOf(false) }
    val taskToDelete = remember { mutableStateOf<Task?>(null) }

    if (openDialog.value) {
        CustomAlertDialog(
            alertLabelId = R.string.task_deletion_alert,
            onDismiss = { openDialog.value = false },
            onConfirm = { state.deleteTask(taskToDelete.value) },
        )
    }
    state.taskList.value.forEach {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${it.name}. ${it.hours} ч. ${it.difficulty.difficultyName} сложность.",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.width(Dimens.XL.padding))
            IconButton(onClick = {
                taskToDelete.value = it
                openDialog.value = true
            }) {
                Icon(imageVector = Icons.Default.Clear, contentDescription = "Delete task")
            }
        }
        Spacer(modifier = Modifier.height(Dimens.S.padding))
    }
}

data class GoalIntoTasksState(
    val taskList: State<List<Task>>,
    val taskName: MutableState<String>,
    val taskHours: MutableState<String>,
    val taskDifficulty: MutableState<TaskDifficulty>,
    val onNextEnabled: Boolean,
    val isTaskSavable: Boolean,
    val saveTask: () -> Unit,
    val deleteTask: (Task?) -> Unit,
)

private data class TaskCreationSheetState(
    val taskName: MutableState<String>,
    val taskHours: MutableState<String>,
    val taskDifficulty: MutableState<TaskDifficulty>,
    val isTaskSavable: Boolean
)

@Preview(name = "Goal Into Tasks")
@Composable
fun GoalIntoTasksScreenPreview() {
    GoalIntoTasksScreen(
        state = GoalIntoTasksState(
            taskList = remember {
                mutableStateOf(
                    listOf(
                        Task(
                            name = "Task",
                            hours = 10,
                            difficulty = TaskDifficulty.EASY,
                            isFinished = false,
                            taskMood = TaskMood.NEUTRAL,
                            actualHours = 10,
                            actualMinutes = 11,
                            actualSeconds = 12,
                            isCompleted = false,
                            completedAt = 1L
                        )
                    )
                )
            },
            taskName = remember { mutableStateOf("Name") },
            taskHours = remember { mutableStateOf("10") },
            taskDifficulty = remember { mutableStateOf(TaskDifficulty.EASY) },
            onNextEnabled = false,
            isTaskSavable = false,
            saveTask = {},
            deleteTask = {}
        ),
        onChangeState = {},
    )
}
