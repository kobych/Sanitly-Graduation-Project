package io.github.kobych.sanitly.data.repositories

import io.github.kobych.sanitly.model.StatusSurveyResponse
import io.github.kobych.sanitly.network.StatusSurveyApiService

/** Abstraction over the remote survey data source. Enables mocking in tests. */
interface RemoteResourcesRepository {
    suspend fun getStrings(): StatusSurveyResponse
}

/** Production implementation that fetches survey content from the GitHub API via Retrofit. */
class NetworkRemoteResourcesRepository(
    private val statusSurveyApiService: StatusSurveyApiService,
) : RemoteResourcesRepository {
    override suspend fun getStrings(): StatusSurveyResponse = statusSurveyApiService.getStrings()
}
