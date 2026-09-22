package io.github.kobych.sanitly.ui.screens.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.kobych.sanitly.R
import io.github.kobych.sanitly.ui.theme.Dimens
import io.github.kobych.sanitly.ui.theme.Typography

@Composable
fun DayAnalyticsScreen(
    state: DayAnalyticsState,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.background(MaterialTheme.colorScheme.background)) {
        BaseStatsGrid(
            states = listOf(
                BaseStatsGridState(
                    labelId = R.string.analytics_day_week_completed_tasks_label,
                    cardInfo = state.completedTasks
                ),
                BaseStatsGridState(
                    labelId = R.string.analytics_day_week_work_time_label,
                    cardInfo = state.workTime
                ),
                BaseStatsGridState(
                    labelId = R.string.analytics_day_week_average_difficulty_label,
                    cardInfo = "${state.averageDifficulty}"
                ),
                BaseStatsGridState(
                    labelId = R.string.analytics_day_productivity_peak_label,
                    cardInfo = "${state.productivityPeak}"
                ),
            ),
        )
        Spacer(modifier = Modifier.height(Dimens.XL.padding))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(Dimens.L.padding)) {
                Text(text = stringResource(R.string.analytics_day_longest_task_label), style = Typography.titleMedium)
                Spacer(modifier = Modifier.height(Dimens.S.padding))
                Text(text = state.longestTask, style = Typography.bodyLarge)
                Spacer(modifier = Modifier.height(Dimens.S.padding))
                Text(text = stringResource(R.string.analytics_day_longest_task_hint), style = Typography.bodyMedium)
            }
        }
        Spacer(modifier = Modifier.height(Dimens.XL.padding))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(Dimens.L.padding)) {
                Text(text = stringResource(R.string.analytics_day_average_mood_label), style = Typography.titleMedium)
                Spacer(modifier = Modifier.height(Dimens.S.padding))
                Text(text = state.averageMoodString, style = Typography.bodyLarge)
            }
        }
    }
}

data class DayAnalyticsState(
    val completedTasks: String,
    val workTime: String,
    val averageDifficulty: Int,
    val productivityPeak: Int,
    val averageMoodString: String,
    val longestTask: String,
)

@Preview(name = "Day Analytics Screen")
@Composable
fun DayAnalyticsScreenPreview() {
    DayAnalyticsScreen(
        state = DayAnalyticsState(
            completedTasks = "3",
            workTime = "5",
            averageDifficulty = 3,
            productivityPeak = 4,
            averageMoodString = "\uD83D\uDE10",
            longestTask = "Project"
        )
    )
}
