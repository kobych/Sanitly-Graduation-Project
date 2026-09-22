package io.github.kobych.sanitly.ui.screens.goalbuilder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.kobych.sanitly.R
import io.github.kobych.sanitly.ui.theme.Dimens
import io.github.kobych.sanitly.ui.theme.Size
import io.github.kobych.sanitly.ui.theme.Typography

@Composable
fun GoalCreatedScreen(
    onCreatedToGoals: () -> Unit,
    createAndSaveGoal: () -> Unit,
    onChangeState: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.L.padding)
    ) {
        Text(
            text = stringResource(R.string.goal_created_title),
            style = Typography.labelLarge
        )
        Spacer(modifier = Modifier.height(Dimens.S.padding))
        Text(
            text = stringResource(R.string.goal_created_label),
            style = Typography.bodyLarge
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    createAndSaveGoal()
                    onCreatedToGoals()
                },
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .height(Size.Button.XL.dp)
                    .weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.goal_created_start_goal_btn),
                    style = Typography.labelLarge
                )
            }
            Spacer(modifier = Modifier.width(Dimens.S.padding))
            Button(
                onClick = {
                    createAndSaveGoal()
                    onChangeState()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .height(Size.Button.XL.dp)
                    .weight(1f)
            ) {
                Text(text = stringResource(R.string.goal_created_to_goal_builder_btn), style = Typography.labelLarge)
            }
        }
    }
}

@Preview(name = "Goal Created")
@Composable
fun GoalCreatedScreenPreview() {
    GoalCreatedScreen(
        createAndSaveGoal = {},
        onChangeState = {},
        onCreatedToGoals = {},
    )
}
