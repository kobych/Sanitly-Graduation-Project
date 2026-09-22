package io.github.kobych.sanitly.ui.screens.goallist

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import io.github.kobych.sanitly.R
import io.github.kobych.sanitly.data.database.entities.Goal
import io.github.kobych.sanitly.data.database.entities.Task
import io.github.kobych.sanitly.data.database.entities.TaskDifficulty
import io.github.kobych.sanitly.data.database.entities.TaskMood
import io.github.kobych.sanitly.ui.components.alerts.CustomAlertDialog
import io.github.kobych.sanitly.ui.components.buttons.SingleChoiceSegmentedButton
import io.github.kobych.sanitly.ui.models.GoalProcessStepsState
import io.github.kobych.sanitly.ui.theme.Dimens
import io.github.kobych.sanitly.ui.theme.Size
import io.github.kobych.sanitly.ui.theme.Typography
import io.github.kobych.sanitly.ui.viewmodels.GoalListViewModel
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalListFlowScreen(modifier: Modifier = Modifier) {
    val viewModel: GoalListViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val goal = viewModel.selectedGoal.value

    var showTaskCompletion by remember { mutableStateOf(false) }
    val taskActions = provideTaskActions(
        viewModel = viewModel,
        changeTaskCompletionState = { showTaskCompletion = !showTaskCompletion },
        hideTaskCompletion = { showTaskCompletion = false }
    )

    LaunchedEffect(uiState) { viewModel.getAllGoals() }

    Scaffold(
        topBar = {
            GoalProcessTopAppBar(uiState = uiState, onChangeState = { viewModel.updateGoalProcessState() })
        }
    ) { innerPadding ->
        when (uiState) {
            GoalProcessStepsState.GoalSelect -> {
                GoalsList(
                    goals = viewModel.goals.value,
                    selectGoal = { viewModel.selectGoal(it) },
                    onChangeState = { viewModel.updateGoalProcessState() },
                    modifier = modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(innerPadding)
                )
            }

            GoalProcessStepsState.TaskSelect -> {
                if (goal != null) {
                    TaskSelect(
                        selectedGoal = goal,
                        selectTask = { viewModel.selectTask(it) },
                        changeTaskCompletion = { showTaskCompletion = !showTaskCompletion },
                        isButtonAvailable = { viewModel.isButtonAvailable(it) },
                        modifier = modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(Dimens.L.padding)
                            .padding(innerPadding)
                    )
                }

                if (showTaskCompletion) {
                    TaskCompletionSheet(
                        state = TaskSheetState(
                            task = viewModel.selectedTask.value,
                            isCompleted = viewModel.selectedTask.value?.isFinished ?: false,
                            isTimerOn = viewModel.isTimerOn.value
                        ),
                        actions = taskActions
                    )
                }
            }
        }
    }
}

private fun provideTaskActions(
    viewModel: GoalListViewModel,
    changeTaskCompletionState: () -> Unit,
    hideTaskCompletion: () -> Unit
): TaskSheetActions = TaskSheetActions(
    selectMood = { viewModel.selectMood(it) },
    onStart = { viewModel.launchTimer() },
    onComplete = {
        viewModel.selectedTask.value?.name?.let { name ->
            viewModel.convertAndSaveTime()
            viewModel.finishTask(name, true)
            changeTaskCompletionState()
        }
    },
    onDismiss = { hideTaskCompletion() },
    onFail = {
        viewModel.selectedTask.value?.name?.let { name ->
            viewModel.convertAndSaveTime()
            viewModel.finishTask(name, false)
            changeTaskCompletionState()
        }
    }
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GoalProcessTopAppBar(
    uiState: GoalProcessStepsState,
    onChangeState: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        navigationIcon = {
            if (uiState is GoalProcessStepsState.TaskSelect) {
                IconButton(onClick = { onChangeState() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "To previous state")
                }
            }
        },
        title = {
            Text(
                text = stringResource(R.string.goal_process_title),
                style = MaterialTheme.typography.titleLarge
            )
        },
        modifier = modifier
    )
}

@SuppressLint("SimpleDateFormat")
@Composable
private fun GoalsList(
    goals: List<Goal>,
    selectGoal: (Goal) -> Unit,
    onChangeState: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.L.padding)
    ) {
        items(goals) { goal ->
            Card(
                onClick = {
                    selectGoal(goal)
                    onChangeState()
                },
                shape = MaterialTheme.shapes.small,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(Dimens.L.padding)
                ) {
                    Row {
                        Text(
                            text = goal.name,
                            style = Typography.titleMedium,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        if (goal.isCompleted) {
                            Text(stringResource(R.string.goal_process_ended_label), style = Typography.labelLarge)
                        } else Text(
                            stringResource(R.string.goal_process_in_process_label),
                            style = Typography.labelLarge
                        )
                    }
                    Spacer(modifier = Modifier.height(Dimens.L.padding))
                    Text(text = goal.category, style = Typography.bodyLarge)
                    Spacer(modifier = Modifier.height(Dimens.S.padding))
                    Text(
                        text = stringResource(R.string.goal_process_planned_hours_label, goal.hours),
                        style = Typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(Dimens.S.padding))
                    Text(
                        text = stringResource(
                            R.string.goal_process_deadline_label,
                            SimpleDateFormat("dd.MM.yyyy").format(goal.deadline)
                        ),
                        style = Typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(modifier = Modifier.height(Dimens.S.padding))
        }
    }
}

@Composable
private fun TaskSelect(
    selectedGoal: Goal,
    selectTask: (Task) -> Unit,
    changeTaskCompletion: () -> Unit,
    isButtonAvailable: (Task) -> Boolean,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
    ) {
        items(selectedGoal.tasks) { task ->
            Card(
                onClick = {
                    selectTask(task)
                    changeTaskCompletion()
                },
                enabled = isButtonAvailable(task),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(Dimens.L.padding)) {
                    Text(
                        text = task.name,
                        style = Typography.titleMedium,
                    )
                    Spacer(modifier = Modifier.height(Dimens.L.padding))
                    Text(
                        text = stringResource(R.string.goal_process_planned_hours_label, task.hours),
                        style = Typography.bodyLarge,
                    )
                    Text(
                        text = stringResource(R.string.goal_process_difficulty_label, task.difficulty.emoji),
                        style = Typography.bodyLarge
                    )
                }
            }
            Spacer(modifier = Modifier.height(Dimens.S.padding))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskCompletionSheet(
    state: TaskSheetState,
    actions: TaskSheetActions
) {
    val openDialog = remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = { actions.onDismiss() })
    {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.L.padding)
        ) {
            if (openDialog.value) {
                CustomAlertDialog(
                    alertLabelId = R.string.goal_process_alert_label,
                    onDismiss = { openDialog.value = false },
                    onConfirm = { actions.onFail() },
                )
            }

            TaskCompletionDetails(
                task = state.task,
            )
            Spacer(modifier = Modifier.height(Dimens.XL.padding))

            when {
                state.isCompleted -> {
                    Text(stringResource(R.string.goal_process_task_completed_label))
                }

                !state.isTimerOn -> {
                    StartTaskButtonsRow(
                        onDismiss = { actions.onDismiss() },
                        onStart = { actions.onStart() }
                    )
                }

                state.isTimerOn -> {
                    Spacer(Modifier.height(Dimens.XL.padding))
                    Text(stringResource(R.string.goal_process_describe_mood_label))
                    Spacer(Modifier.height(Dimens.S.padding))

                    SingleChoiceSegmentedButton(
                        options = TaskMood.entries,
                        onClick = { actions.selectMood(it) },
                        labelText = { it.emoji },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(Dimens.XL.padding))

                    TaskCreatedButtonsRow(
                        onComplete = { actions.onComplete() },
                        onDialogChange = { openDialog.value = true }
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskCompletionDetails(
    task: Task?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "${stringResource(R.string.goal_process_task_label)}: ${task?.name.toString()}",
            style = Typography.labelLarge
        )
        Spacer(Modifier.height(Dimens.S.padding))
        Text(
            "${stringResource(R.string.goal_process_planned_time_label)}: ${task?.hours.toString()}",
            style = Typography.labelLarge
        )
        Spacer(Modifier.height(Dimens.S.padding))
        Text(
            "${stringResource(R.string.goal_new_task_difficulty_label)} ${task?.difficulty.toString()}",
            style = Typography.labelLarge
        )
    }
}

@Composable
private fun StartTaskButtonsRow(
    onDismiss: () -> Unit,
    onStart: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
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
        )
        { Text(stringResource(R.string.common_back)) }
        Spacer(modifier = Modifier.width(Dimens.S.padding))

        Button(
            onClick = { onStart() },
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier
                .weight(1f)
                .height(Size.Button.XL.dp)
        )
        { Text(stringResource(R.string.goal_process_start_task_btn)) }
    }
}

@Composable
private fun TaskCreatedButtonsRow(
    onComplete: () -> Unit,
    onDialogChange: () -> Unit
) {
    Row {
        Button(onClick = { onDialogChange() },
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            ),
            modifier = Modifier
                .weight(1f)
                .height(Size.Button.XL.dp))
        { Text(stringResource(R.string.common_cancel)) }
        Spacer(modifier = Modifier.width(Dimens.S.padding))
        Button(onClick = { onComplete() },
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier
                .weight(1f)
                .height(Size.Button.XL.dp))
        { Text(stringResource(R.string.goal_process_complete_btn)) }
    }
}

private data class TaskSheetActions(
    val selectMood: (TaskMood) -> Unit,
    val onStart: () -> Unit,
    val onComplete: () -> Unit,
    val onDismiss: () -> Unit,
    val onFail: () -> Unit
)

private data class TaskSheetState(
    val task: Task?,
    val isCompleted: Boolean,
    val isTimerOn: Boolean,
)

@Preview(name = "Goals Screen")
@Composable
fun GoalProcessScreenPreview() {
    TaskCompletionSheet(
        state = TaskSheetState(
            task = Task(
                name = "1",
                hours = 1,
                difficulty = TaskDifficulty.EASY,
                isFinished = false,
                taskMood = TaskMood.NEUTRAL,
                actualHours = 1,
                actualMinutes = 1,
                actualSeconds = 1,
                isCompleted = false,
                completedAt = 1L
            ),
            isCompleted = false,
            isTimerOn = false
        ),
        actions = TaskSheetActions(
            selectMood = {},
            onStart = {},
            onComplete = {},
            onDismiss = {},
            onFail = {}
        ),
    )
}
