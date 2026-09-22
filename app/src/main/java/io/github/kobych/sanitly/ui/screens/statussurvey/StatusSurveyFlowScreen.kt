package io.github.kobych.sanitly.ui.screens.statussurvey

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import io.github.kobych.sanitly.ui.models.StatusSurveyStepsState
import io.github.kobych.sanitly.ui.viewmodels.StatusSurveyViewModel

@Composable
fun StatusSurveyFlowScreen(
    onSurveyCompleted: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StatusSurveyViewModel = hiltViewModel(),
) {
    val questions by viewModel.questions
    val introductions by viewModel.introductions
    val result by viewModel.finalResult
    val error by viewModel.errorMessage

    val uiState by viewModel.uiState.collectAsState()

    var currentIndex by rememberSaveable { mutableIntStateOf(0) }
    when (uiState) {
        StatusSurveyStepsState.Introduction -> SurveyIntroductionScreen(
            onNext = { viewModel.updateStatusSurveyState() },
            introductions = introductions,
            modifier = modifier
        )

        StatusSurveyStepsState.Survey -> StatusSurveyScreen(
            state = StatusSurveyState(
                currentIndex = currentIndex,
                error = error,
                questions = questions,
                increaseIndex = { currentIndex++ },
                decreaseIndex = { currentIndex-- },
                completeSurvey = { viewModel.completeSurvey() },
                updateAnswer = { questionId, answer -> viewModel.updateAnswers(questionId, answer) }
            ),
            modifier = modifier
        )

        StatusSurveyStepsState.Results -> SurveyResultsScreen(
            result = result,
            onFinish = {
                viewModel.saveAndCloseSurvey(
                    onClosed = { onSurveyCompleted() }
                )
            },
            modifier = modifier
        )
    }
}
