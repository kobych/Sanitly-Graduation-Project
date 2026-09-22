package io.github.kobych.sanitly.model

import com.google.gson.annotations.SerializedName

data class ResultsResponse(
    @SerializedName("min_points")
    val minPoints: Int,
    @SerializedName("max_points")
    val maxPoints: Int,
    val verdict: String,
    val advantages: List<String>
)
