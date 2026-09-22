package io.github.kobych.sanitly.util

import io.github.kobych.sanitly.data.database.entities.Goal
import io.github.kobych.sanitly.data.database.entities.Task
import io.github.kobych.sanitly.data.database.entities.TaskDifficulty
import io.github.kobych.sanitly.data.database.entities.TaskMood
@Suppress("LongParameterList")
fun createDefaultGoal(
    id: Int,
    goalName: String = "Goal",
    goalCategory: String = "Category",
    hours: Long = 1L,
    deadline: Long = 1L,
    description: String? = "",
    isCompleted: Boolean = false,
    successNote: String = "",
    failureNote: String = "",
    summaryNote: String = "",
    tasks: List<Task> = emptyList()
): Goal {
    return Goal(
        id = id,
        name = goalName,
        category = goalCategory,
        hours = hours,
        deadline = deadline,
        description = description,
        tasks = tasks,
        isCompleted = isCompleted,
        successNote = successNote,
        failureNote = failureNote,
        summaryNote = summaryNote
    )
}

@Suppress("LongParameterList")
fun createDefaultTask(
    name: String = "Task",
    hours: Long = 1L,
    difficulty: TaskDifficulty = TaskDifficulty.MEDIUM,
    isFinished: Boolean = false,
    isCompleted: Boolean = false,
    taskMood: TaskMood = TaskMood.NEUTRAL,
    actualHours: Long = 1L,
    actualMinutes: Int = 1,
    actualSeconds: Int = 1,
    completedAt: Long? = 1000L
): Task {
    return Task(
        name = name,
        hours = hours,
        difficulty = difficulty,
        isFinished = isFinished,
        isCompleted = isCompleted,
        taskMood = taskMood,
        actualHours = actualHours,
        actualMinutes = actualMinutes,
        actualSeconds = actualSeconds,
        completedAt = completedAt
    )
}
