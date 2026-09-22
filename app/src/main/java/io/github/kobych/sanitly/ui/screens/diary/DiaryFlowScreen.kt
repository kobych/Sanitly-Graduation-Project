package io.github.kobych.sanitly.ui.screens.diary

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import io.github.kobych.sanitly.R
import io.github.kobych.sanitly.data.database.entities.Goal
import io.github.kobych.sanitly.ui.models.DiaryStepsState
import io.github.kobych.sanitly.ui.theme.Dimens
import io.github.kobych.sanitly.ui.theme.Size
import io.github.kobych.sanitly.ui.theme.Typography
import io.github.kobych.sanitly.ui.viewmodels.DiaryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryFlowScreen(
    modifier: Modifier = Modifier
) {
    val viewModel: DiaryViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) { viewModel.getAllGoals() }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    if (uiState is DiaryStepsState.GoalNote) {
                        IconButton(onClick = { viewModel.updateDiaryState() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "To previous state")
                        }
                    }
                },
                title = {
                    Text(
                        text = stringResource(R.string.diary_screen_title),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
            )
        }
    ) { innerPadding ->
        when (uiState) {
            DiaryStepsState.GoalSelect -> {
                NotesGrid(
                    goals = viewModel.goals.value,
                    canNoteExpand = { viewModel.canNoteExpand(it) },
                    selectGoal = { viewModel.selectGoal(it) },
                    onChangeState = { viewModel.updateDiaryState() },
                    modifier = modifier
                        .background(MaterialTheme.colorScheme.background)
                        .padding(innerPadding)
                )
            }

            DiaryStepsState.GoalNote -> {
                NoteEditSheet(
                    state = NoteEditSheetState(
                        successNote = viewModel.successNote,
                        failureNote = viewModel.failureNote,
                        summaryNote = viewModel.summaryNote,
                    ),
                    onChangeState = { viewModel.updateDiaryState() },
                    saveNotes = { viewModel.saveNotes() },
                    isButtonAvailable = viewModel.isButtonAvailable(),
                    modifier = modifier
                        .background(MaterialTheme.colorScheme.background)
                        .padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun NotesGrid(
    goals: List<Goal>,
    canNoteExpand: (Goal) -> Boolean,
    selectGoal: (Goal) -> Unit,
    onChangeState: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = PaddingValues(Dimens.L.padding),
        modifier = modifier.fillMaxSize()
    ) {
        items(items = goals) { goal ->
            var isExpanded by rememberSaveable { mutableStateOf(false) }

            Card(
                onClick = {
                    if (canNoteExpand(goal)) {
                        isExpanded = !isExpanded
                    }
                },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .animateItem()
            ) {
                Column(modifier = Modifier.padding(Dimens.L.padding)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Dimens.S.padding)
                    ) {
                        Text(text = goal.name, style = Typography.titleMedium)
                        Spacer(modifier = Modifier.weight(1f))
                        if (isExpanded) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = "Is expanded",
                            )
                        } else {
                            Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "Is not expanded")
                        }
                        IconButton(onClick = {
                            selectGoal(goal)
                            onChangeState()
                        }) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                        }
                    }
                    if (isExpanded) {
                        Column {
                            Text(text = goal.successNote, style = Typography.bodyLarge)
                            Spacer(modifier = Modifier.height(Dimens.L.padding))
                            Text(text = goal.failureNote, style = Typography.bodyLarge)
                            Spacer(modifier = Modifier.height(Dimens.L.padding))
                            Text(text = goal.summaryNote, style = Typography.bodyLarge)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(Dimens.XL.padding))
        }
    }
}

@Composable
private fun NoteEditSheet(
    state: NoteEditSheetState,
    onChangeState: () -> Unit,
    saveNotes: () -> Unit,
    isButtonAvailable: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(Dimens.L.padding)
    ) {
        LazyColumn {
            item {
                TextAndTextField(
                    descriptionStringId = R.string.diary_screen_successes_label,
                    nameStringId = R.string.diary_screen_successes_hint,
                    value = state.successNote.value,
                    onValueChange = { state.successNote.value = it }
                )
                Spacer(modifier = Modifier.height(Dimens.XL.padding))
                TextAndTextField(
                    descriptionStringId = R.string.diary_screen_failures_label,
                    nameStringId = R.string.diary_screen_failures_hint,
                    value = state.failureNote.value,
                    onValueChange = { state.failureNote.value = it }
                )
                Spacer(modifier = Modifier.height(Dimens.XL.padding))
                TextAndTextField(
                    descriptionStringId = R.string.diary_screen_summary_label,
                    nameStringId = R.string.diary_screen_summary_hint,
                    value = state.summaryNote.value,
                    onValueChange = { state.summaryNote.value = it }
                )
                Spacer(modifier = Modifier.height(Dimens.XXL.padding))
            }
        }
        Button(
            onClick = {
                onChangeState()
                saveNotes()
            },
            enabled = isButtonAvailable,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.align(Alignment.BottomEnd)
        ) { Text(stringResource(R.string.common_save)) }
    }
}

@Composable
private fun TextAndTextField(
    descriptionStringId: Int,
    nameStringId: Int,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(descriptionStringId),
            color = MaterialTheme.colorScheme.onSurface,
            style = Typography.labelLarge,
        )
        Spacer(modifier = Modifier.height(Dimens.S.padding))
        OutlinedTextField(
            value = value,
            onValueChange = { onValueChange(it) },
            label = { Text(text = stringResource(nameStringId), style = Typography.bodyLarge) },
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

private data class NoteEditSheetState(
    val successNote: MutableState<String>,
    val failureNote: MutableState<String>,
    val summaryNote: MutableState<String>,
)

@Preview(name = "Diary Screen")
@Composable
fun DiaryFlowScreenPreview() {
    DiaryFlowScreen()
}
