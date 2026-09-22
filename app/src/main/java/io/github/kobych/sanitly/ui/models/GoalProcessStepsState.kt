package io.github.kobych.sanitly.ui.models

sealed class GoalProcessStepsState {
    data object GoalSelect : GoalProcessStepsState()
    data object TaskSelect : GoalProcessStepsState()
}
