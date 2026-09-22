package io.github.kobych.sanitly.viewmodel

import io.github.kobych.sanitly.MainDispatcherRule
import io.github.kobych.sanitly.data.database.entities.Answer
import io.github.kobych.sanitly.data.database.entities.Introduction
import io.github.kobych.sanitly.data.database.entities.Question
import io.github.kobych.sanitly.data.database.entities.Result
import io.github.kobych.sanitly.data.repositories.StatusSurveyData
import io.github.kobych.sanitly.data.repositories.StatusSurveyRepository
import io.github.kobych.sanitly.data.repositories.UserPreferencesRepository
import io.github.kobych.sanitly.ui.models.StatusSurveyStepsState
import io.github.kobych.sanitly.ui.viewmodels.StatusSurveyViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class StatusSurveyViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val statusSurveyRepository = mockk<StatusSurveyRepository>(relaxed = true)
    private val userPreferencesRepository = mockk<UserPreferencesRepository>(relaxed = true)

    private lateinit var viewModel: StatusSurveyViewModel

    /**
     * ViewModel populates its internal state from [StatusSurveyRepository.getSurveyData]
     * inside init{}. The mock must be configured BEFORE the ViewModel is constructed, otherwise the
     * coroutine launched in init already ran against the default relaxed mock.
     */
    private fun createViewModel(
        introductions: List<Introduction> = emptyList(),
        results: List<Result> = emptyList(),
        questions: List<Question> = emptyList()
    ) {
        coEvery { statusSurveyRepository.getSurveyData() } returns
            StatusSurveyData(
                introductions = introductions,
                results = results,
                questions = questions
            )
        viewModel = StatusSurveyViewModel(statusSurveyRepository, userPreferencesRepository)
    }

    @Before
    fun setup() {
        createViewModel()
    }

    @Test
    fun updateStatusSurveyState_goNextState_updatesNormally() {
        viewModel.updateStatusSurveyState()

        assertEquals(StatusSurveyStepsState.Survey, viewModel.uiState.value)
    }

    @Test
    fun updateStatusSurveyState_goToResultsState_stateRemainsResults() {
        viewModel.updateStatusSurveyState()
        viewModel.updateStatusSurveyState()
        viewModel.updateStatusSurveyState()

        assertEquals(StatusSurveyStepsState.Results, viewModel.uiState.value)
    }

    @Test
    fun completeSurvey_goodPoints_goodResult() = runTest {
        val mockResults = listOf(
            Result(id = 0, minPoints = 0, maxPoints = 5, verdict = "Bad", advantages = emptyList()),
            Result(id = 1, minPoints = 6, maxPoints = 10, verdict = "Normal", advantages = emptyList()),
            Result(id = 2, minPoints = 11, maxPoints = 15, verdict = "Good", advantages = emptyList())
        )
        createViewModel(results = mockResults)

        viewModel.updateAnswers(1, Answer(5, 1, "Some answer"))
        viewModel.updateAnswers(2, Answer(5, 2, "Another answer"))
        viewModel.updateAnswers(3, Answer(2, 3, "Some answer"))
        viewModel.updateAnswers(4, Answer(3, 4, "Some answer"))
        viewModel.completeSurvey()

        assertEquals(mockResults[2].verdict, viewModel.finalResult.value?.verdict)
    }

    @Test
    fun completeSurvey_badPoints_badResult() = runTest {
        val mockResults = listOf(
            Result(id = 0, minPoints = 0, maxPoints = 5, verdict = "Bad", advantages = emptyList()),
            Result(id = 1, minPoints = 6, maxPoints = 10, verdict = "Normal", advantages = emptyList()),
            Result(id = 2, minPoints = 11, maxPoints = 15, verdict = "Good", advantages = emptyList())
        )
        createViewModel(results = mockResults)

        viewModel.updateAnswers(1, Answer(1, 1, "Some answer"))
        viewModel.updateAnswers(2, Answer(1, 2, "Another answer"))
        viewModel.updateAnswers(3, Answer(1, 3, "Some answer"))
        viewModel.updateAnswers(4, Answer(1, 4, "Some answer"))
        viewModel.completeSurvey()

        assertEquals(mockResults[0].verdict, viewModel.finalResult.value?.verdict)
    }

    @Test
    fun completeSurvey_noPoints_badResult() = runTest {
        val mockResults = listOf(
            Result(id = 0, minPoints = 0, maxPoints = 5, verdict = "Bad", advantages = emptyList()),
            Result(id = 1, minPoints = 6, maxPoints = 10, verdict = "Normal", advantages = emptyList()),
            Result(id = 2, minPoints = 11, maxPoints = 15, verdict = "Good", advantages = emptyList())
        )
        createViewModel(results = mockResults)

        viewModel.completeSurvey()

        assertEquals(mockResults[0].verdict, viewModel.finalResult.value?.verdict)
    }

    @Test
    fun saveAndCloseSurvey_normalBehaviour_executesCallback() = runTest {
        val onClosedMock = mockk<() -> Unit>(relaxed = true)
        coEvery { userPreferencesRepository.saveSurveyCompletedPreference() } returns Unit

        viewModel.saveAndCloseSurvey(onClosedMock)

        coVerify(exactly = 1) {
            userPreferencesRepository.saveSurveyCompletedPreference()
        }

        verify(exactly = 1) { onClosedMock() }
    }

    @Test
    fun updateAnswers_addAnswerToMap_updatesState() {
        val answer = Answer(1, 1, "Some answer")

        viewModel.updateAnswers(1, answer)

        assertEquals(answer, viewModel.answers.value[1])
        assertEquals(1, viewModel.answers.value.size)
    }

    @Test
    fun updateAnswers_overwritesExistingAnswer_updatesState() {
        val firstAnswer = Answer(1, 1, "First")
        val secondAnswer = Answer(2, 1, "Second")

        viewModel.updateAnswers(1, firstAnswer)
        viewModel.updateAnswers(1, secondAnswer)

        assertEquals(secondAnswer, viewModel.answers.value[1])
    }
}
