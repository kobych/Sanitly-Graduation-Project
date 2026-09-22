package io.github.kobych.sanitly.model

data class QuestionResponse(
    val id: Int,
    val question: String,
    val answers: List<AnswerResponse>,
)
