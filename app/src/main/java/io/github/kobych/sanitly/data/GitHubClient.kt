package io.github.kobych.sanitly.data

import io.github.kobych.sanitly.network.StatusSurveyApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GitHubClient {
    private const val BASE_URL =
        "https://api.github.com"

    val api: StatusSurveyApiService by lazy {
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(StatusSurveyApiService::class.java)
    }
}
