package io.github.kobych.sanitly.data.database.entities

data class Answer(
    val id: Int,
    val questionId: Int,
    val content: String,
)
