package io.github.kobych.sanitly.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kobych.sanitly.data.database.entities.Answer
import io.github.kobych.sanitly.data.database.entities.Introduction
import io.github.kobych.sanitly.data.database.entities.Question
import io.github.kobych.sanitly.data.database.entities.Result
import io.github.kobych.sanitly.data.repositories.StatusSurveyRepository
import io.github.kobych.sanitly.data.repositories.UserPreferencesRepository
import io.github.kobych.sanitly.ui.models.StatusSurveyStepsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class StatusSurveyViewModel @Inject constructor(
    private val statusSurveyRepository: StatusSurveyRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {
    /**
     * Responsible for changing screens in StatusSurveyFlowScreen
     */
    private val _uiState = MutableStateFlow<StatusSurveyStepsState>(StatusSurveyStepsState.Introduction)
    val uiState: StateFlow<StatusSurveyStepsState> = _uiState.asStateFlow()

    private val _questions = mutableStateOf<List<Question>>(emptyList())
    val questions: State<List<Question>> = _questions

    /**
     * This state is responsible for introduction texts in SurveyIntroductionScreen
     */
    private val _introductions = mutableStateOf<List<Introduction>>(emptyList())
    val introductions: State<List<Introduction>> = _introductions

    private val _results = mutableStateOf<List<Result>>(emptyList())

    private val _finalResult = mutableStateOf<Result?>(null)
    val finalResult: State<Result?> = _finalResult

    private val _answers = mutableStateOf<Map<Int, Answer>>(emptyMap())
    val answers: State<Map<Int, Answer>> = _answers

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    init {
        getResources()
    }

    private fun getResources() {
        viewModelScope.launch {
            try {
                val statusSurveyData = statusSurveyRepository.getSurveyData()
                _questions.value = statusSurveyData.questions
                _introductions.value = statusSurveyData.introductions
                _results.value = statusSurveyData.results
            } catch (e: HttpException) {
                _errorMessage.value = "Failed to load strings: ${e.localizedMessage}"
            }
        }
    }

    fun updateStatusSurveyState() {
        _uiState.update { currentState ->
            when (currentState) {
                StatusSurveyStepsState.Introduction -> StatusSurveyStepsState.Survey
                StatusSurveyStepsState.Survey -> StatusSurveyStepsState.Results
                StatusSurveyStepsState.Results -> StatusSurveyStepsState.Results
            }
        }
    }

    /**
     * Summarizes all points from answers in StatusSurveyScreen
     * Finds final result for user's points
     * Updates state of StatusSurveyFlowScreen to next screen
     */
    fun completeSurvey() {
        viewModelScope.launch {
            val surveyPoints = _answers.value.values.sumOf { answer ->
                answer.id
            }
            _finalResult.value = _results.value.find { surveyPoints in it.minPoints..it.maxPoints }
            updateStatusSurveyState()
        }
    }

    /**
     * Saves preference to dataStore that survey is completed
     * Closes StatusSurveyFlowScreen forever
     */
    fun saveAndCloseSurvey(onClosed: () -> Unit) {
        viewModelScope.launch {
            userPreferencesRepository.saveSurveyCompletedPreference()
            onClosed()
        }
    }

    fun updateAnswers(
        questionId: Int,
        answer: Answer
    ) {
        _answers.value += mapOf(questionId to answer)
    }
}
