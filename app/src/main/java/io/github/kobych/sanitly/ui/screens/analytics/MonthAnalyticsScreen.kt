package io.github.kobych.sanitly.ui.screens.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.kobych.sanitly.R
import io.github.kobych.sanitly.data.database.entities.TaskMood
import io.github.kobych.sanitly.ui.models.AnalyticsState.Companion.DAYS_IN_WEEK
import io.github.kobych.sanitly.ui.models.AnalyticsState.Companion.ONE_DAY
import io.github.kobych.sanitly.ui.theme.Dimens
import io.github.kobych.sanitly.ui.theme.Size
import io.github.kobych.sanitly.ui.theme.Typography
import io.github.kobych.sanitly.ui.viewmodels.DayMood
import io.github.kobych.sanitly.ui.viewmodels.DayProgress
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

private const val CALENDAR_DAY_BOX_ASPECT_RATIO = 1.5f

@Composable
fun MonthAnalyticsScreen(
    state: MonthAnalyticsState,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        WorkCalendar(state)
        Spacer(modifier = Modifier.height(Dimens.L.padding))
        Card(
            shape = MaterialTheme.shapes.small, colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(Dimens.L.padding)) {
                CustomProgressIndicator(
                    labelId = R.string.analytics_month_completion_label,
                    percentage = state.completionPercentage,
                )
                Spacer(modifier = Modifier.height(Dimens.L.padding))
                CustomProgressIndicator(
                    labelId = R.string.analytics_month_mood_ratio_label,
                    percentage = state.moodPercentage,
                )
                Spacer(modifier = Modifier.height(Dimens.L.padding))
                CustomProgressIndicator(
                    labelId = R.string.analytics_month_active_days_label,
                    percentage = state.activeDaysPercentage,
                )
            }
        }
        Spacer(modifier = Modifier.height(Dimens.L.padding))
        MoodDynamicsCard(
            moodByDays = state.moodByDays,
        )
    }
}

@Composable
private fun WorkCalendar(
    state: MonthAnalyticsState,
    modifier: Modifier = Modifier
) {
    val currentMonth = LocalDate.now()

    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(Dimens.L.padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = currentMonth.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale.forLanguageTag("ru"))
                    .uppercase(),
                style = Typography.titleMedium
            )
            Spacer(modifier = Modifier.height(Dimens.S.padding))
            CalendarGrid(state)
        }
    }
}

@Composable
private fun CalendarGrid(
    state: MonthAnalyticsState,
    modifier: Modifier = Modifier
) {
    val currentMonth = LocalDate.now()
    val firstDayOfMonth = currentMonth.withDayOfMonth(ONE_DAY)
    val emptyDaysBefore = firstDayOfMonth.dayOfWeek.value - ONE_DAY
    LazyVerticalGrid(
        columns = GridCells.Fixed(DAYS_IN_WEEK),
        horizontalArrangement = Arrangement.spacedBy(Dimens.S.padding),
        verticalArrangement = Arrangement.spacedBy(Dimens.S.padding),
        content = {
            items(DAYS_IN_WEEK) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = DayOfWeek.entries[it].getDisplayName(
                            TextStyle.SHORT,
                            Locale.forLanguageTag("ru")
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = Typography.labelLarge
                    )
                }
            }
            items(
                emptyDaysBefore + currentMonth.month.length(currentMonth.isLeapYear)
            ) { item ->
                if (item >= emptyDaysBefore) {
                    val dayNumber = item - emptyDaysBefore + 1
                    val tasksByDays = state.tasksByDays
                    val completedTasks = if (dayNumber - 1 < tasksByDays.size) {
                        tasksByDays[dayNumber - 1].completedTasks
                    } else 0
                    CalendarCell(
                        completedTasks = completedTasks,
                        dayNumber = dayNumber,
                        state = state
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(Size.Button.S.dp)
                            .aspectRatio(CALENDAR_DAY_BOX_ASPECT_RATIO)
                    )
                }
            }
        },
        modifier = modifier
    )
}

@Composable
private fun CalendarCell(
    completedTasks: Int,
    dayNumber: Int,
    state: MonthAnalyticsState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(CALENDAR_DAY_BOX_ASPECT_RATIO)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.small
            )
            .background(
                color = MaterialTheme.colorScheme.surface
                    .copy(alpha = state.getColorForTasks(completedTasks)),
                shape = MaterialTheme.shapes.small
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = dayNumber.toString(),
            style = Typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun CustomProgressIndicator(
    labelId: Int,
    percentage: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(labelId),
                style = Typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(R.string.analytics_screen_percentage_common, percentage),
                style = Typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(Dimens.S.padding))
        LinearProgressIndicator(
            progress = { percentage.toFloat() / 100f },
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun MoodDynamicsCard(
    moodByDays: List<DayMood>,
    modifier: Modifier = Modifier
) {
    val lineColor = MaterialTheme.colorScheme.primary

    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(Dimens.L.padding)
        ) {
            Text(
                text = stringResource(R.string.analytics_month_mood_dynamics_label),
                style = Typography.labelLarge,
            )
            Spacer(modifier = Modifier.height(Dimens.S.padding))
            Box {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                )
                {
                    if (moodByDays.size > 1) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height

                        val padding = Dimens.S.padding.toPx()
                        val usableWidth = canvasWidth - (padding * 2)
                        val horizontalStep = usableWidth / (moodByDays.size - 1)
                        val verticalStep = canvasHeight / (TaskMood.entries.size - 1)

                        val path = Path()

                        moodByDays.forEachIndexed { index, dayMood ->
                            val x = padding + (index * horizontalStep)
                            val y = canvasHeight - (dayMood.averageMood.toFloatOrNull() ?: 0f) * verticalStep

                            if (index == 0) {
                                path.moveTo(x, y)
                            } else {
                                path.lineTo(x, y)
                            }
                        }

                        drawPath(
                            path = path,
                            color = lineColor,
                            style = Stroke(
                                width = 3.dp.toPx(),
                                join = StrokeJoin.Round,
                                cap = StrokeCap.Round
                            )
                        )
                    }
                }
            }
        }
    }
}

data class MonthAnalyticsState(
    val tasksByDays: List<DayProgress>,
    val getColorForTasks: (Int) -> Float,
    val completionPercentage: Int,
    val moodPercentage: Int,
    val activeDaysPercentage: Int,
    val moodByDays: List<DayMood>
)

@Preview(name = "Month Analytics Screen")
@Composable
fun MonthAnalyticsScreenPreview() {
    MonthAnalyticsScreen(
        state = MonthAnalyticsState(
            tasksByDays = emptyList(),
            getColorForTasks = { 5f },
            completionPercentage = 50,
            moodPercentage = 50,
            activeDaysPercentage = 80,
            moodByDays = emptyList()
        )
    )
}
