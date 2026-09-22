package io.github.kobych.sanitly.ui.models

sealed class StatusSurveyStepsState {
    data object Introduction: StatusSurveyStepsState()
    data object Survey: StatusSurveyStepsState()
    data object Results: StatusSurveyStepsState()
}
