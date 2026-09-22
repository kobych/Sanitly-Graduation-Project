package io.github.kobych.sanitly.ui.models

import io.github.kobych.sanitly.R
import io.github.kobych.sanitly.ui.models.AnalyticsState.Day
import io.github.kobych.sanitly.ui.models.AnalyticsState.Month
import io.github.kobych.sanitly.ui.models.AnalyticsState.Week
import java.time.LocalDate

val states = listOf(Day, Week, Month)

sealed class AnalyticsState(val titleId: Int, val dayCount: Int) {
    data object Day : AnalyticsState(titleId = R.string.analytics_screen_day_chip, dayCount = ONE_DAY)
    data object Week : AnalyticsState(titleId = R.string.analytics_screen_week_chip, dayCount = DAYS_IN_WEEK)
    data object Month : AnalyticsState(titleId = R.string.analytics_screen_month_chip, LocalDate.now().lengthOfMonth())

    companion object {
        const val DAYS_IN_WEEK = 7
        const val ONE_DAY = 1
    }
}
