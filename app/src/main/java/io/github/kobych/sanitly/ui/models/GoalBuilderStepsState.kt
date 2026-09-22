package io.github.kobych.sanitly.ui.models

sealed class GoalBuilderStepsState {
    /**
     * data keyword has toString(), hashcode(), equals() and copy() functions
     * Function toString() is useful for objects here
     */
    data object Builder : GoalBuilderStepsState()
    data object Creation : GoalBuilderStepsState()
    data object GoalIntoTasks : GoalBuilderStepsState()
    data object Created : GoalBuilderStepsState()
}
