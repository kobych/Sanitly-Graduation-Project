package io.github.kobych.sanitly.data.database.entities

private const val LEVEL_1 = 1
private const val LEVEL_2 = 2
private const val LEVEL_3 = 3
private const val LEVEL_4 = 4
private const val LEVEL_5 = 5


/**
 * Represents a single subtask within a [Goal].
 *
 * Not a Room entity — stored as JSON inside [Goal.tasks] via [ListConverter].
 *
 * @property name Display name of the task.
 * @property hours Planned duration in hours.
 * @property difficulty Subjective difficulty level chosen at creation time.
 * @property isFinished True once the user has pressed "finish" on this task (timer stopped).
 * @property isCompleted True if the user marked the task as successfully completed (vs. abandoned).
 * @property taskMood Mood recorded by the user at the moment of finishing.
 * @property actualHours Actual hours measured by the in-app timer.
 * @property actualMinutes Actual minutes component of the measured duration.
 * @property actualSeconds Actual seconds component of the measured duration.
 * @property completedAt Unix epoch millisecond timestamp of when the task was finished; null if not yet finished.
 */
data class Task(
    val name: String,
    val hours: Long,
    val difficulty: TaskDifficulty,
    val isFinished: Boolean,
    val isCompleted: Boolean,
    val taskMood: TaskMood,
    val actualHours: Long,
    val actualMinutes: Int,
    val actualSeconds: Int,
    val completedAt: Long?
)

/** Five-level difficulty scale for a task, from very easy (green) to critical (red). */
enum class TaskDifficulty(val difficultyName: String, val difficultyLevel: Int, val emoji: String) {
    VERY_EASY(
        "Очень лёгкая",
        LEVEL_1,
        "\uD83D\uDFE2"
    ),
    EASY(
        "Лёгкая",
        LEVEL_2,
        "\uD83D\uDD35"
    ),
    MEDIUM(
        "Средняя",
        LEVEL_3,
        "\uD83D\uDFE1"
    ),
    HARD(
        "Тяжелая",
        LEVEL_4,
        "\uD83D\uDFE0"
    ),
    VERY_HARD(
        "Критическая",
        LEVEL_5,
        "\uD83D\uDD34"
    )
}

/** Five-level mood scale recorded by the user after finishing a task. */
enum class TaskMood(val moodName: String, val emoji: String, val moodValue: Int) {
    AWFUL("Ужасно", "\uD83D\uDE2B", LEVEL_1),
    BAD("Плохо", "\uD83D\uDE1F", LEVEL_2),
    NEUTRAL("Нейтрально", "\uD83D\uDE10", LEVEL_3),
    GOOD("Хорошо", "\uD83D\uDE42", LEVEL_4),
    EXCELLENT("Прекрасно", "\uD83D\uDE03", LEVEL_5)
}
