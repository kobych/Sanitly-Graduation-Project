package io.github.kobych.sanitly.ui.screens.goalbuilder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import io.github.kobych.sanitly.R
import io.github.kobych.sanitly.data.database.entities.Goal
import io.github.kobych.sanitly.ui.components.alerts.CustomAlertDialog
import io.github.kobych.sanitly.ui.models.GoalBuilderStepsState
import io.github.kobych.sanitly.ui.theme.Dimens
import io.github.kobych.sanitly.ui.viewmodels.GoalBuilderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalBuilderFlowScreen(
    onCreatedToGoals: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: GoalBuilderViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    DisposableEffect(Unit) {
        onDispose { if (uiState is GoalBuilderStepsState.Created) viewModel.updateGoalBuilderState() }
    }

    Scaffold(
        topBar = {
            GoalBuilderTopAppBar(
                uiState = uiState,
                goPreviousGoalBuilderState = { viewModel.goPreviousGoalBuilderState() })
        }
    ) { innerPadding ->
        when (uiState) {
            GoalBuilderStepsState.Builder -> GoalBuilderButton(
                getAllGoals = { viewModel.getAllGoals() },
                goals = viewModel.goals,
                onChangeState = { viewModel.updateGoalBuilderState() },
                onDelete = { viewModel.deleteGoal(it) },
                modifier = modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
            )

            GoalBuilderStepsState.Creation -> GoalCreationScreen(
                state = viewModel.goalCreationState,
                onChangeState = { viewModel.updateGoalBuilderState() },
                onNextEnabled = viewModel.isGoalIntoTasksScreenAvailable,
                modifier = modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
            )

            GoalBuilderStepsState.GoalIntoTasks -> GoalIntoTasksScreen(
                state = viewModel.goalIntoTasksState,
                onChangeState = { viewModel.updateGoalBuilderState() },
                modifier = modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
            )

            GoalBuilderStepsState.Created -> GoalCreatedScreen(
                createAndSaveGoal = { viewModel.createAndSaveGoal() },
                onChangeState = { viewModel.updateGoalBuilderState() },
                onCreatedToGoals = { onCreatedToGoals() },
                modifier = modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GoalBuilderTopAppBar(
    uiState: GoalBuilderStepsState,
    goPreviousGoalBuilderState: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        navigationIcon = {
            when (uiState) {
                GoalBuilderStepsState.Creation, GoalBuilderStepsState.GoalIntoTasks, GoalBuilderStepsState.Created -> {
                    IconButton(onClick = { goPreviousGoalBuilderState() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "To previous state"
                        )
                    }
                }

                GoalBuilderStepsState.Builder -> {}
            }
        },
        title = {
            Text(
                text = stringResource(R.string.goal_builder_screen_title),
                style = MaterialTheme.typography.titleLarge
            )
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GoalBuilderButton(
    getAllGoals: () -> Unit,
    goals: State<List<Goal>>,
    onChangeState: () -> Unit,
    onDelete: (Goal) -> Unit,
    modifier: Modifier = Modifier
) {
    val openDialog = remember { mutableStateOf(false) }
    var goalToDelete by remember { mutableStateOf<Goal?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.L.padding)
    ) {
        LaunchedEffect(Unit) { getAllGoals() }

        if (openDialog.value) {
            CustomAlertDialog(
                alertLabelId = R.string.goal_cancel_alert,
                onDismiss = { openDialog.value = false },
                onConfirm = { goalToDelete?.let { onDelete(it) } },
            )
        }
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            verticalItemSpacing = Dimens.S.padding,
            horizontalArrangement = Arrangement.spacedBy(Dimens.S.padding)
        ) {
            items(items = goals.value) { goal ->
                GoalCard(
                    goal = goal,
                    onDelete = { goalToDelete = goal },
                    onOpenDialog = { openDialog.value = true },
                )
            }
        }
        Spacer(modifier = Modifier.height(Dimens.XL.padding))

        FloatingActionButton(
            onClick = { onChangeState() },
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.align(Alignment.BottomEnd)
        ) { Icon(Icons.Default.Add, "Add") }
    }
}

@Composable
private fun GoalCard(
    goal: Goal,
    onDelete: () -> Unit,
    onOpenDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.S.padding),
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.L.padding)
        )
        {
            Text(
                text = goal.name,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = {
                onDelete()
                onOpenDialog()
            })
            { Icon(imageVector = Icons.Default.Clear, contentDescription = "Delete goal") }
        }
    }
}

@Preview(name = "Goal Builder Button")
@Composable
fun GoalBuilderButtonPreview() {
    GoalBuilderButton(
        getAllGoals = {},
        goals = remember { mutableStateOf(emptyList()) },
        onDelete = {},
        onChangeState = {}
    )
}
