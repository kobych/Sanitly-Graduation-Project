package io.github.kobych.sanitly.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a user goal stored in the "goals" table.
 *
 * @property id Auto-generated primary key.
 * @property name Display name of the goal.
 * @property category User-defined category label (e.g., "Health", "Career").
 * @property hours Total estimated hours, computed as the sum of all task hours.
 * @property deadline Optional deadline as a Unix epoch millisecond timestamp.
 * @property description Optional freeform description of the goal.
 * @property tasks List of [Task] subtasks; stored as JSON via [ListConverter].
 * @property isCompleted True when all tasks have been finished and the goal is marked complete.
 * @property successNote Diary note written after the goal succeeds.
 * @property failureNote Diary note written after the goal fails.
 * @property summaryNote General diary summary note for the goal.
 */
@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val category: String,
    val hours: Long,
    val deadline: Long?,
    val description: String?,
    val tasks: List<Task>,
    val isCompleted: Boolean,
    val successNote: String,
    val failureNote: String,
    val summaryNote: String
)
