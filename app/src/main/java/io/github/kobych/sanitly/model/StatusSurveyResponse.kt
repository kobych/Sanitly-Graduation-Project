package io.github.kobych.sanitly.model

import com.google.gson.annotations.SerializedName

data class StatusSurveyResponse(
    @SerializedName("status_survey")
    val statusSurvey: List<QuestionResponse>,

    @SerializedName("survey_introduction")
    val surveyIntroduction: List<IntroductionResponse>,

    @SerializedName("survey_results")
    val surveyResults: List<ResultsResponse>
)
