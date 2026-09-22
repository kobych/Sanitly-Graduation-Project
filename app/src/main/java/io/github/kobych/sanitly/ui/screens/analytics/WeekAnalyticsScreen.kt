package io.github.kobych.sanitly.ui.screens.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.kobych.sanitly.R
import io.github.kobych.sanitly.ui.theme.Dimens
import io.github.kobych.sanitly.ui.theme.Typography
import io.github.kobych.sanitly.ui.viewmodels.DayMood
import io.github.kobych.sanitly.ui.viewmodels.DayProgress
import io.github.kobych.sanitly.ui.viewmodels.DaySummary

@Composable
fun WeekAnalyticsScreen(
    state: WeekAnalyticsState,
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
                    labelId = R.string.analytics_day_week_percentage_label,
                    cardInfo = "${state.completionPercent}%"
                ),
                BaseStatsGridState(
                    labelId = R.string.analytics_day_week_average_difficulty_label,
                    cardInfo = state.averageDifficulty.toString()
                ),
            ),
        )
        Spacer(modifier = Modifier.height(Dimens.XL.padding))

        Card(
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(Dimens.L.padding)) {
                Text(
                    text = stringResource(R.string.analytics_week_tasks_by_days_label),
                    style = Typography.labelLarge
                )
                Spacer(modifier = Modifier.height(Dimens.S.padding))
                FinishedTasksRow(weekAnalyticsState = state)
            }
        }
        Spacer(modifier = Modifier.height(Dimens.XL.padding))

        Card(
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(Dimens.L.padding)) {
                Text(
                    text = stringResource(R.string.analytics_week_mood_by_days_label),
                    style = Typography.labelLarge
                )
                Spacer(modifier = Modifier.height(Dimens.S.padding))
                WeekMoodRow(weekAnalyticsState = state)
            }
        }
        Spacer(modifier = Modifier.height(Dimens.XL.padding))

        DaysCardsRow(weekAnalyticsState = state)
    }
}

@Composable
private fun FinishedTasksRow(
    weekAnalyticsState: WeekAnalyticsState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.L.padding),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            items(weekAnalyticsState.tasksByDays) {
                DayTaskItem(
                    dayProgress = it,
                )
            }
        }
    }
}

@Composable
private fun WeekMoodRow(
    weekAnalyticsState: WeekAnalyticsState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.L.padding),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            items(weekAnalyticsState.moodByDays) {
                DayMoodItem(
                    dayMood = it,
                )
            }
        }
    }
}

@Composable
private fun DayTaskItem(
    dayProgress: DayProgress,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(Dimens.S.padding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = dayProgress.completedTasks.toString(), style = Typography.labelLarge)
        Text(text = dayProgress.dayName, style = Typography.labelLarge)
    }
}

@Composable
private fun DayMoodItem(
    dayMood: DayMood,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(Dimens.S.padding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = dayMood.emoji)
        Text(text = dayMood.dayName, style = Typography.labelLarge)
    }
}

@Composable
private fun DaysCardsRow(
    weekAnalyticsState: WeekAnalyticsState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.S.padding)
    ) {
        WeekDayCard(
            labelId = R.string.analytics_week_best_day_label,
            completedTasks = weekAnalyticsState.bestDay.completedTasks,
            dayName = weekAnalyticsState.bestDay.dayName,
            emoji = weekAnalyticsState.bestDay.emoji,
            modifier = Modifier.weight(1f)
        )
        WeekDayCard(
            labelId = R.string.analytics_week_worst_day_label,
            completedTasks = weekAnalyticsState.worstDay.completedTasks,
            dayName = weekAnalyticsState.worstDay.dayName,
            emoji = weekAnalyticsState.worstDay.emoji,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun WeekDayCard(
    labelId: Int,
    completedTasks: Int,
    dayName: String,
    emoji: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(Dimens.L.padding)) {
            Text(
                text = "${stringResource(labelId)}: $dayName",
                style = Typography.labelLarge
            )
            Spacer(modifier = Modifier.height(Dimens.S.padding))
            Text(
                text = stringResource(R.string.analytics_screen_completed_tasks_label, completedTasks),
                style = Typography.bodyLarge
            )
            Text(
                text = stringResource(R.string.analytics_screen_summary_mood_label, emoji),
                style = Typography.bodyLarge
            )
        }
    }
}

data class WeekAnalyticsState(
    val completedTasks: String,
    val workTime: String,
    val completionPercent: Int,
    val averageDifficulty: Int,
    val tasksByDays: List<DayProgress>,
    val moodByDays: List<DayMood>,
    val bestDay: DaySummary,
    val worstDay: DaySummary,
)

@Preview(name = "Week Analytics Screen")
@Composable
fun WeekAnalyticsScreenPreview() {
    WeekAnalyticsScreen(
        state = WeekAnalyticsState(
            completedTasks = "100",
            workTime = "100",
            completionPercent = 100,
            averageDifficulty = 5,
            tasksByDays = emptyList(),
            moodByDays = emptyList(),
            bestDay = DaySummary(
                dayName = "Day",
                completedTasks = 4,
                hours = 2.0,
                emoji = "😃"
            ),
            worstDay = DaySummary(
                dayName = "Day",
                completedTasks = 4,
                hours = 2.0,
                emoji = "😃"
            )
        ),
    )
}
