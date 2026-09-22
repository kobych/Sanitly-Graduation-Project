package io.github.kobych.sanitly.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "results")
data class Result(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val minPoints: Int,
    val maxPoints: Int,
    val verdict: String,
    val advantages: List<String>
)
