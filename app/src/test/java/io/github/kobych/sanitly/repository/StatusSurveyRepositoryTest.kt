package io.github.kobych.sanitly.repository

import android.util.Log
import io.github.kobych.sanitly.data.database.dao.IntroductionDao
import io.github.kobych.sanitly.data.database.dao.QuestionDao
import io.github.kobych.sanitly.data.database.dao.ResultDao
import io.github.kobych.sanitly.data.database.entities.Introduction
import io.github.kobych.sanitly.data.database.entities.Question
import io.github.kobych.sanitly.data.database.entities.Result
import io.github.kobych.sanitly.data.repositories.NetworkRemoteResourcesRepository
import io.github.kobych.sanitly.data.repositories.StatusSurveyRepository
import io.github.kobych.sanitly.model.AnswerResponse
import io.github.kobych.sanitly.model.IntroductionResponse
import io.github.kobych.sanitly.model.QuestionResponse
import io.github.kobych.sanitly.model.StatusSurveyResponse
import io.github.kobych.sanitly.network.ConnectivityChecker
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import java.io.IOException

class StatusSurveyRepositoryTest {
    private val networkGitHubJsonRepository = mockk<NetworkRemoteResourcesRepository>()
    private val questionDao = mockk<QuestionDao>()
    private val introductionDao = mockk<IntroductionDao>()
    private val resultDao = mockk<ResultDao>()

    private val connectivityChecker = mockk<ConnectivityChecker>()
    private val statusSurveyRepository =
        StatusSurveyRepository(
            networkGitHubJsonRepository,
            questionDao,
            introductionDao,
            resultDao,
            connectivityChecker
        )

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        every { connectivityChecker.isOnline() } returns true

        coEvery { introductionDao.replaceAll(any()) } coAnswers { callOriginal() }
        coEvery { questionDao.replaceAll(any()) } coAnswers { callOriginal() }
        coEvery { resultDao.replaceAll(any()) } coAnswers { callOriginal() }

        coEvery { networkGitHubJsonRepository.getStrings() } returns StatusSurveyResponse(
            statusSurvey = listOf(
                QuestionResponse(
                    id = 1,
                    question = "Something",
                    answers = listOf(
                        AnswerResponse(
                            id = 1,
                            answer = "Answer"
                        )
                    )
                )
            ),
            surveyIntroduction = listOf(
                IntroductionResponse(
                    id = 1,
                    text = "Introduction"
                )
            ),
            surveyResults = emptyList()
        )
        coEvery { introductionDao.deleteIntroductions() } returns Unit
        coEvery { questionDao.deleteQuestions() } returns Unit
        coEvery { resultDao.deleteResults() } returns Unit
        coEvery { questionDao.saveQuestions(any()) } returns Unit
        coEvery { introductionDao.saveIntroductions(any()) } returns Unit
        coEvery { resultDao.saveResults(any()) } returns Unit
        coEvery { introductionDao.getAllIntroductions() } returns emptyList()
        coEvery { resultDao.getAllResults() } returns emptyList()
        coEvery { questionDao.getAllQuestions() } returns emptyList()
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    @Test
    fun getIntroduction_defaultList_getsIntroduction() = runTest {
        val expectedIntroductions = listOf(
            Introduction(id = 1, text = "Introduction")
        )

        coEvery { introductionDao.getAllIntroductions() } returns expectedIntroductions

        statusSurveyRepository.getSurveyData()
        val result = statusSurveyRepository.getIntroductionsFromDatabase()

        assertEquals(expectedIntroductions, result)
    }

    @Test
    fun getSurveyResults_defaultList_getsResults() = runTest {
        statusSurveyRepository.getSurveyData()
        val result = statusSurveyRepository.getSurveyResultsFromDatabase()

        assertEquals(emptyList<Result>(), result)
    }

    @Test
    fun getDatabaseResources_oneQuestion_returnsQuestionList() = runTest {
        val question = Question(
            id = 1,
            content = "",
            answers = emptyList()
        )
        val questions = listOf(question)
        coEvery { questionDao.getAllQuestions() } returns questions

        val result = statusSurveyRepository.getQuestionsFromDatabase()

        assertEquals(questions, result)
        coVerify(exactly = 1) { questionDao.getAllQuestions() }
    }

    @Test
    fun getDatabaseResources_noQuestions_returnsEmptyList() = runTest {
        val questions = emptyList<Question>()
        coEvery { questionDao.getAllQuestions() } returns questions

        val result = statusSurveyRepository.getQuestionsFromDatabase()

        assertEquals(questions, result)
        coVerify(exactly = 1) { questionDao.getAllQuestions() }
    }

    @Test
    fun getDatabaseResources_severalQuestions_returnsFullList() = runTest {
        val firstQuestion = Question(id = 1, content = "", answers = emptyList())
        val secondQuestion = Question(id = 2, content = "", answers = emptyList())
        val thirdQuestion = Question(id = 3, content = "", answers = emptyList())
        val questions = listOf(firstQuestion, secondQuestion, thirdQuestion)
        coEvery { questionDao.getAllQuestions() } returns questions

        val result = statusSurveyRepository.getQuestionsFromDatabase()

        assertEquals(questions, result)
        coVerify(exactly = 1) { questionDao.getAllQuestions() }
    }

    @Test
    fun getSaveNetResources_offline_doesNotCallNetworkOrWriteToDb() = runTest {
        every { connectivityChecker.isOnline() } returns false

        statusSurveyRepository.getSurveyData()

        coVerify(exactly = 0) { networkGitHubJsonRepository.getStrings() }
        coVerify(exactly = 0) { introductionDao.replaceAll(any()) }
        coVerify(exactly = 0) { questionDao.saveQuestions(any()) }
        coVerify(exactly = 0) { resultDao.saveResults(any()) }
    }

    @Test
    fun getSaveNetResources_offline_returnsDataFromDatabase() = runTest {
        every { connectivityChecker.isOnline() } returns false
        val cachedIntroductions = listOf(Introduction(id = 1, text = "Cached"))
        coEvery { introductionDao.getAllIntroductions() } returns cachedIntroductions

        val result = statusSurveyRepository.getSurveyData()

        assertEquals(cachedIntroductions, result.introductions)
    }

    @Test
    fun getSaveNetResources_connectivityCheckerThrowsSecurityException_treatedAsOffline() = runTest {
        every { connectivityChecker.isOnline() } throws SecurityException("No permission")

        statusSurveyRepository.getSurveyData()

        coVerify(exactly = 0) { networkGitHubJsonRepository.getStrings() }
        coVerify(exactly = 0) { questionDao.saveQuestions(any()) }
    }

    @Test
    fun getSaveNetResources_networkThrowsIOException_doesNotWriteToDbAndReturnsCache() = runTest {
        coEvery { networkGitHubJsonRepository.getStrings() } throws IOException("No connection")
        val cachedQuestions = listOf(Question(id = 1, content = "Cached", answers = emptyList()))
        coEvery { questionDao.getAllQuestions() } returns cachedQuestions

        val result = statusSurveyRepository.getSurveyData()

        coVerify(exactly = 0) { questionDao.saveQuestions(any()) }
        assertEquals(cachedQuestions, result.questions)
    }

    @Test
    fun getSaveNetResources_networkThrowsHttpException_doesNotWriteToDb() = runTest {
        val httpException = mockk<HttpException>()
        coEvery { networkGitHubJsonRepository.getStrings() } throws httpException

        statusSurveyRepository.getSurveyData()

        coVerify(exactly = 0) { introductionDao.saveIntroductions(any()) }
        coVerify(exactly = 0) { questionDao.saveQuestions(any()) }
        coVerify(exactly = 0) { resultDao.saveResults(any()) }
    }
}
