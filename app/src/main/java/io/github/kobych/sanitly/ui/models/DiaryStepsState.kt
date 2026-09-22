package io.github.kobych.sanitly.ui.models

sealed class DiaryStepsState {
    data object GoalSelect : DiaryStepsState()
    data object GoalNote : DiaryStepsState()
}
