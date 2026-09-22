package io.github.kobych.sanitly.network

import io.github.kobych.sanitly.BuildConfig
import io.github.kobych.sanitly.model.StatusSurveyResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers

interface StatusSurveyApiService {
    @Headers(
        "Accept: application/vnd.github.raw+json",
        "X-GitHub-Api-Version:2022-11-28",
    )
    @GET("/repos/kobych/Sanitly/contents/resources/strings.json")
    suspend fun getStrings(
        @Header("Authorization") auth: String = "Bearer ${BuildConfig.GITHUB_TOKEN}",
    ): StatusSurveyResponse
}
