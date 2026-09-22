package io.github.kobych.sanitly.data.repositories

import android.util.Log
import io.github.kobych.sanitly.data.database.dao.IntroductionDao
import io.github.kobych.sanitly.data.database.dao.QuestionDao
import io.github.kobych.sanitly.data.database.dao.ResultDao
import io.github.kobych.sanitly.data.database.entities.Answer
import io.github.kobych.sanitly.data.database.entities.Introduction
import io.github.kobych.sanitly.data.database.entities.Question
import io.github.kobych.sanitly.data.database.entities.Result
import io.github.kobych.sanitly.network.ConnectivityChecker
import retrofit2.HttpException
import java.io.IOException

/**
 * Coordinates survey data between the remote GitHub API and the local Room database.
 *
 * Attempts to refresh survey data from the network when an internet connection is available.
 * Successfully fetched data is mapped to Room entities and stored in the local database.
 *
 * Regardless of network availability or request failures, survey data is returned from the
 * local database.
 */
class StatusSurveyRepository(
    private val networkGitHubJsonRepository: NetworkRemoteResourcesRepository,
    private val questionDao: QuestionDao,
    private val introductionDao: IntroductionDao,
    private val resultsDao: ResultDao,
    private val connectivityChecker: ConnectivityChecker
) {
    /**
     * Refreshes survey data from the network if possible and returns the current data
     * stored in the local database.
     *
     * If the device is offline or a network request fails, the existing database contents
     * are returned without modification.
     */
    suspend fun getSurveyData(): StatusSurveyData {
        val isOnline: Boolean = try {
            connectivityChecker.isOnline()
        } catch (e: SecurityException) {
            Log.e("ConnectivityChecker", "Unknown net problem", e)

            false
        }

        if (isOnline) {
            val response = try {
                networkGitHubJsonRepository.getStrings()
            } catch (e: IOException) {
                Log.e("NetworkGitHubJsonRepository", "Unknown net problem while getting survey data", e)
                null
            } catch (e: HttpException) {
                Log.e("NetworkGitHubJsonRepository", "Unknown http exception", e)
                null
            }

            val introductionStrings = response?.surveyIntroduction?.map { introductionResponse ->
                Introduction(
                    id = introductionResponse.id,
                    text = introductionResponse.text
                )
            }

            val questionsStrings = response?.statusSurvey?.map { questionResponse ->
                val answers = questionResponse.answers
                    .map { answerResponse ->
                        Answer(
                            answerResponse.id,
                            questionResponse.id,
                            answerResponse.answer,
                        )
                    }
                Question(id = questionResponse.id, content = questionResponse.question, answers = answers)
            }

            val resultStrings = response?.surveyResults?.map { resultResponse ->
                val advantages = resultResponse.advantages
                Result(
                    id = 0,
                    minPoints = resultResponse.minPoints,
                    maxPoints = resultResponse.maxPoints,
                    verdict = resultResponse.verdict,
                    advantages = advantages
                )
            }
            if (introductionStrings != null && questionsStrings != null && resultStrings != null) {
                introductionDao.replaceAll(introductionStrings)
                questionDao.replaceAll(questionsStrings)
                resultsDao.replaceAll(resultStrings)
            }
        }
        return StatusSurveyData(
            getIntroductionsFromDatabase(),
            getSurveyResultsFromDatabase(),
            getQuestionsFromDatabase()
        )
    }

    suspend fun getIntroductionsFromDatabase(): List<Introduction> = introductionDao.getAllIntroductions()
    suspend fun getSurveyResultsFromDatabase(): List<Result> = resultsDao.getAllResults()

    suspend fun getQuestionsFromDatabase(): List<Question> = questionDao.getAllQuestions()
}

/**
 * Aggregates all survey-related data loaded from the local database.
 */
data class StatusSurveyData(
    val introductions: List<Introduction>,
    val results: List<Result>,
    val questions: List<Question>
)
