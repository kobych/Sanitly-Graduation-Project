package io.github.kobych.sanitly.ui.models

import io.github.kobych.sanitly.R

sealed class OptionsState(val titleId: Int) {
    data object Options : OptionsState(titleId = R.string.options_screen_title)
    data object Notifications: OptionsState(titleId = R.string.options_notifications_title)
    data object About : OptionsState(titleId = R.string.options_about_title)
    data object Theme : OptionsState(titleId = R.string.options_theme_title)
}
