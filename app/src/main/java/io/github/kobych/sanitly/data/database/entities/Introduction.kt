package io.github.kobych.sanitly.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "introductions")
data class Introduction(
    @PrimaryKey val id: Int,
    val text: String
)
