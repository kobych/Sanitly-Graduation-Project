package io.github.kobych.sanitly.ui.screens.goalbuilder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.kobych.sanitly.R
import io.github.kobych.sanitly.ui.components.DatePickerDocked
import io.github.kobych.sanitly.ui.theme.Dimens
import io.github.kobych.sanitly.ui.theme.Size

@Composable
fun GoalCreationScreen(
    onNextEnabled: Boolean,
    state: GoalCreationState,
    onChangeState: () -> Unit,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(Dimens.L.padding)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(vertical = Dimens.XL.padding),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                GoalCreationItem(
                    labelId = R.string.goal_builder_new_goal_name_label,
                    hintId = R.string.goal_builder_new_goal_name_hint,
                    textFieldContent = state.goalName
                )
                Spacer(modifier = Modifier.height(Dimens.XL.padding))

                GoalCreationItem(
                    labelId = R.string.goal_builder_new_goal_category_label,
                    hintId = R.string.goal_builder_new_goal_category_hint,
                    textFieldContent = state.goalCategory
                )
                Spacer(modifier = Modifier.height(Dimens.XL.padding))

                Text(
                    text = stringResource(R.string.goal_builder_new_goal_deadline_label),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(Dimens.S.padding))
                DatePickerDocked(initialDateMillis = state.initialDateMillis) {
                    state.goalDeadline.value = it?.toString() ?: ""
                }
                Spacer(modifier = Modifier.height(Dimens.XL.padding))

                GoalCreationItem(
                    labelId = R.string.goal_builder_new_goal_description_label,
                    hintId = R.string.goal_builder_new_goal_description_hint,
                    textFieldContent = state.goalDescription
                )
                Spacer(modifier = Modifier.height(Dimens.XXL.padding))
            }
        }

        Button(
            onClick = { onChangeState() },
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                disabledContentColor = MaterialTheme.colorScheme.onSurface,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            enabled = onNextEnabled,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(Size.Button.XL.dp)
        ) { Icon(Icons.AutoMirrored.Filled.ArrowForward, "Next state") }
    }
}

@Composable
private fun GoalCreationItem(
    labelId: Int,
    hintId: Int,
    textFieldContent: MutableState<String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(labelId),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(Dimens.S.padding))
        OutlinedTextField(
            value = textFieldContent.value,
            onValueChange = { textFieldContent.value = it },
            label = {
                Text(
                    text = stringResource(hintId),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

data class GoalCreationState(
    val goalName: MutableState<String>,
    val goalCategory: MutableState<String>,
    val goalDeadline: MutableState<String>,
    val goalDescription: MutableState<String>,
    val initialDateMillis: Long?
)

@Preview(name = "Goal Creation")
@Composable
fun GoalCreationScreenPreview() {
    GoalCreationScreen(
        state = GoalCreationState(
            remember { mutableStateOf("Name") },
            goalCategory = remember { mutableStateOf("Category") },
            goalDeadline = remember { mutableStateOf("Deadline") },
            goalDescription = remember { mutableStateOf("Description") },
            initialDateMillis = 1L
        ),
        onChangeState = {},
        onNextEnabled = false
    )
}
