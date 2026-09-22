package io.github.kobych.sanitly.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class Question(
    @PrimaryKey val id: Int,
    val content: String,
    val answers: List<Answer>,
)
