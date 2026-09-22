package io.github.kobych.sanitly.repository

import io.github.kobych.sanitly.data.repositories.NetworkRemoteResourcesRepository
import io.github.kobych.sanitly.model.StatusSurveyResponse
import io.github.kobych.sanitly.network.StatusSurveyApiService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test

class RemoteResourcesRepositoryTest {
    private val statusSurveyApiService = mockk<StatusSurveyApiService>()
    private val networkRemoteResourcesRepository = NetworkRemoteResourcesRepository(statusSurveyApiService)

    @Test
    fun getString_defaultService_getsResponse() = runTest {
        val expectedResponse = StatusSurveyResponse(
            statusSurvey = emptyList(),
            surveyIntroduction = emptyList(),
            surveyResults = emptyList()
        )
        coEvery { statusSurveyApiService.getStrings() } returns expectedResponse

        val result = networkRemoteResourcesRepository.getStrings()

        assertEquals(expectedResponse, result)
        coVerify(exactly = 1) { statusSurveyApiService.getStrings() }
    }
}
