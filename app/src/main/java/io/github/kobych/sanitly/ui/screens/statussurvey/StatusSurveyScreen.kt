package io.github.kobych.sanitly.ui.screens.statussurvey

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.kobych.sanitly.R
import io.github.kobych.sanitly.data.database.entities.Answer
import io.github.kobych.sanitly.data.database.entities.Question
import io.github.kobych.sanitly.ui.theme.Dimens
import io.github.kobych.sanitly.ui.theme.Size

@Composable
fun StatusSurveyScreen(
    state: StatusSurveyState,
    modifier: Modifier = Modifier
) {
    if (state.currentIndex < state.questions.size) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            if (state.error != null) {
                Text("Error: ${state.error}")
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Dimens.L.padding),
                    modifier = Modifier
                        .padding(Dimens.L.padding)
                        .align(Alignment.Center)
                ) {
                    QuestionCard(text = state.questions[state.currentIndex].content)

                    state.questions[state.currentIndex].answers.forEach {
                        Button(
                            onClick = {
                                state.updateAnswer(state.questions[state.currentIndex].id, it)
                                if (state.currentIndex == state.questions.lastIndex) {
                                    state.completeSurvey()
                                } else {
                                    state.increaseIndex()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(Size.Button.XL.dp),
                        ) { Text(it.content) }
                    }
                    if (state.currentIndex > 0) {
                        Row {
                            Button(
                                shape = MaterialTheme.shapes.small,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                onClick = { state.decreaseIndex() },
                                modifier = Modifier
                                    .height(Size.Button.XL.dp)
                                    .weight(1f)
                            ) { Text(stringResource(R.string.common_back)) }
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestionCard(
    text: String,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.elevatedCardColors(
            contentColor = MaterialTheme.colorScheme.onPrimary,
            containerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            modifier.padding(Dimens.L.padding)
        )
    }
}

data class StatusSurveyState(
    val currentIndex: Int,
    val error: String?,
    val questions: List<Question>,
    val increaseIndex: () -> Unit,
    val decreaseIndex: () -> Unit,
    val completeSurvey: () -> Unit,
    val updateAnswer: (Int, Answer) -> Unit
)

@Preview
@Composable
fun StatusSurveyScreenPreview() {
    StatusSurveyScreen(
        state = StatusSurveyState(
            currentIndex = 0,
            error = "",
            questions = emptyList(),
            increaseIndex = {},
            decreaseIndex = {},
            completeSurvey = {},
            updateAnswer = { _, _ -> {} }
        )
    )
}
