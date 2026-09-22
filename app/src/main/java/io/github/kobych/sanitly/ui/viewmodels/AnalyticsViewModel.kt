package io.github.kobych.sanitly.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.SQLiteException
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kobych.sanitly.data.database.entities.Goal
import io.github.kobych.sanitly.data.database.entities.Task
import io.github.kobych.sanitly.data.database.entities.TaskMood
import io.github.kobych.sanitly.data.repositories.AnalyticsRepository
import io.github.kobych.sanitly.ui.models.AnalyticsState
import io.github.kobych.sanitly.ui.screens.analytics.DayAnalyticsState
import io.github.kobych.sanitly.ui.screens.analytics.MonthAnalyticsState
import io.github.kobych.sanitly.ui.screens.analytics.WeekAnalyticsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

/**
 * ViewModel for the analytics screens (Day / Week / Month).
 *
 * All statistics are derived from the full list of completed [Task]s filtered by [defineStartTime].
 * Switching between time ranges via [updateAnalyticsState] re-filters the task list and recomputes
 * all metrics without hitting the database again. The database is only queried once per screen entry
 * via [getAllTasks].
 */
@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<AnalyticsState>(AnalyticsState.Day)
    val uiState: StateFlow<AnalyticsState> = _uiState.asStateFlow()

    private val _completedTasks = mutableStateOf("")
    val completedTasks: State<String> = _completedTasks

    private val _workTime = mutableStateOf("")
    val workTime: State<String> = _workTime

    private val _averageDifficulty = mutableIntStateOf(0)
    val averageDifficulty: State<Int> = _averageDifficulty

    private val _averageMood = mutableStateOf("")
    val averageMood: State<String> = _averageMood

    private val _productivityPeak = mutableIntStateOf(0)
    val productivityPeak: State<Int> = _productivityPeak

    private val _longestTask = mutableStateOf("")
    val longestTask: State<String> = _longestTask

    private val _completionPercent = mutableIntStateOf(0)
    val completionPercent: State<Int> = _completionPercent

    private val _activeDaysPercentage = mutableIntStateOf(0)
    val activeDaysPercentage: State<Int> = _activeDaysPercentage

    private val _moodPercentage = mutableIntStateOf(0)
    val moodPercentage: State<Int> = _moodPercentage

    private val _goals = mutableStateOf<List<Goal>>(emptyList())
    val goals: State<List<Goal>> = _goals
    private val _tasks = mutableStateOf<List<Task>>(emptyList())
    val tasks: State<List<Task>> = _tasks

    private val _tasksByDays = mutableStateOf<List<DayProgress>>(emptyList())
    val tasksByDays: State<List<DayProgress>> = _tasksByDays

    private val _moodByDays = mutableStateOf<List<DayMood>>(emptyList())
    val moodByDays: State<List<DayMood>> = _moodByDays

    private val _bestDay = mutableStateOf<DaySummary?>(null)
    val bestDay: State<DaySummary?> = _bestDay

    private val _worstDay = mutableStateOf<DaySummary?>(null)
    val worstDay: State<DaySummary?> = _worstDay

    private val zoneId = ZoneId.systemDefault()

    private val filteredTasks: List<Task>
        get() = _tasks.value
            .filter { task ->
                val completedTime = task.completedAt
                completedTime != null && completedTime >= defineStartTime()
            }

    val dayAnalyticsState get() = DayAnalyticsState(
        completedTasks = _completedTasks.value,
        workTime = _workTime.value,
        averageDifficulty = _averageDifficulty.intValue,
        productivityPeak = _productivityPeak.intValue,
        averageMoodString = _averageMood.value,
        longestTask = _longestTask.value
    )

    val weekAnalyticsState get() = WeekAnalyticsState(
        completedTasks = _completedTasks.value,
        workTime = _workTime.value,
        completionPercent = _completionPercent.intValue,
        averageDifficulty = _averageDifficulty.intValue,
        tasksByDays = _tasksByDays.value,
        moodByDays = _moodByDays.value,
        bestDay = DaySummary(
            dayName = _bestDay.value?.dayName ?: "",
            completedTasks = _bestDay.value?.completedTasks ?: 0,
            hours = _bestDay.value?.hours ?: 0.0,
            emoji = _bestDay.value?.emoji ?: ""
        ),
        worstDay = DaySummary(
            dayName = _worstDay.value?.dayName ?: "",
            completedTasks = _worstDay.value?.completedTasks ?: 0,
            hours = _worstDay.value?.hours ?: 0.0,
            emoji = _worstDay.value?.emoji ?: ""
        )
    )

    val monthAnalyticsState get() = MonthAnalyticsState(
        tasksByDays = _tasksByDays.value,
        getColorForTasks = { tasks -> getColorForTasks(tasks) },
        completionPercentage = _completionPercent.intValue,
        moodPercentage = _moodPercentage.intValue,
        activeDaysPercentage = _activeDaysPercentage.intValue,
        moodByDays = _moodByDays.value
    )

    private fun defineStartTime(): Long {
        return when (_uiState.value) {
            AnalyticsState.Day -> {
                LocalDate.now()
                    .convertStartDayMilli()
            }

            AnalyticsState.Week -> {
                LocalDate.now()
                    .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                    .convertStartDayMilli()
            }

            AnalyticsState.Month -> {
                LocalDate.now()
                    .withDayOfMonth(1)
                    .convertStartDayMilli()
            }
        }
    }

    private fun LocalDate.convertStartDayMilli(): Long {
        return this.atStartOfDay(zoneId)
            .toInstant()
            .toEpochMilli()
    }

    private fun calculateCardStats(filteredTasks: List<Task>): CardStats {
        val workMinutes = filteredTasks.fold(Duration.ZERO) { acc, task ->
            acc + task.actualHours.hours + task.actualMinutes.minutes + task.actualSeconds.seconds
        }

        val averageDifficulty = if (filteredTasks.isNotEmpty()) {
            filteredTasks.sumOf { it.difficulty.difficultyLevel } / filteredTasks.size
        } else 0

        val productivityPeak = filteredTasks.groupBy {
            Instant.ofEpochMilli(it.completedAt ?: 0L)
                .atZone(zoneId)
                .hour
        }.maxByOrNull { it.value.size }?.key ?: 0

        val averageMood = if (filteredTasks.isNotEmpty()) {
            filteredTasks.sumOf { it.taskMood.moodValue } / filteredTasks.size
        } else 0

        return CardStats(
            workHours = workMinutes.toDouble(DurationUnit.HOURS),
            averageDifficulty = averageDifficulty,
            productivityPeak = productivityPeak,
            averageMood = averageMood
        )
    }

    /**
     * Groups [filteredTasks] by calendar day and produces per-day progress, mood, and summary lists.
     *
     * Iterates over [dayCount] days starting from [startTime] so that days with no tasks
     * still appear in charts (with zero values).
     */
    private fun buildDailyAnalyticsData(
        startTime: Long,
        filteredTasks: List<Task>,
        dayCount: Int
    ): AnalyticDataState {
        val tasksByDays = mutableListOf<DayProgress>()
        val moodByDays = mutableListOf<DayMood>()
        val daySummaries = mutableListOf<DaySummary>()

        for (i in ONE_DAY..dayCount) {
            val dateOfIteration = Instant.ofEpochMilli(startTime)
                .atZone(zoneId)
                .toLocalDate()
                .plusDays(i.toLong() - ONE_DAY)

            val tasksForDay = filteredTasks.filter {
                val taskDate = Instant.ofEpochMilli(it.completedAt!!)
                    .atZone(zoneId)
                    .toLocalDate()
                taskDate == dateOfIteration
            }

            val dayWorkMinutes =
                tasksForDay.fold(Duration.ZERO) { acc, task ->
                    acc + task.actualHours.hours + task.actualMinutes.minutes + task.actualSeconds.seconds
                }
            val dayWorkHours = dayWorkMinutes.toDouble(DurationUnit.HOURS)

            val completedTasks = tasksForDay.count { it.isCompleted }
            val failedTasks = tasksForDay.count { !it.isCompleted }
            val finishedTasks = tasksForDay.size
            val dayMoods = tasksForDay.map { it.taskMood.moodValue }
            val averageMood = if (dayMoods.isNotEmpty()) {
                (dayMoods.sum().toFloat() / dayMoods.size)
            } else 0f

            val dayName = dateOfIteration.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("ru"))
            val dayEmoji = TaskMood.entries.find { it.moodValue == averageMood.toInt() }?.emoji ?: ""

            val dayProgress = DayProgress(
                dayName = dayName,
                completedTasks = completedTasks,
                failedTasks = failedTasks,
                totalTasks = finishedTasks
            )
            val dayMood = DayMood(
                dayName = dayName,
                averageMood = "%.1f".format(Locale.US, averageMood),
                emoji = dayEmoji
            )
            val daySummary = DaySummary(
                dayName = dayName,
                completedTasks = completedTasks,
                hours = dayWorkHours,
                emoji = dayEmoji
            )

            tasksByDays.add(dayProgress)
            moodByDays.add(dayMood)
            daySummaries.add(daySummary)
        }
        return AnalyticDataState(
            tasksByDays = tasksByDays,
            moodByDays = moodByDays,
            daySummaries = daySummaries,
        )
    }

    private fun updateStats() {
        val startTime = defineStartTime()
        val filteredTasks = filteredTasks
        val filteredTasksCount = filteredTasks.size
        val longestTask =
            filteredTasks.maxByOrNull {
                it.actualHours.hours + it.actualMinutes.minutes + it.actualSeconds.seconds
            }
        val averageMood = calculateCardStats(filteredTasks).averageMood
        val completedTasks = filteredTasks.count { it.isCompleted }

        val dailyData = buildDailyAnalyticsData(
            startTime = startTime,
            filteredTasks = filteredTasks,
            dayCount = _uiState.value.dayCount
        )

        val completionPercentage = if (filteredTasksCount > 0) {
            ((completedTasks / filteredTasksCount.toFloat()) * FULL_PERCENTS).toInt()
        } else 0

        val moodPercentage = ((averageMood / TaskMood.entries.size.toFloat()) * FULL_PERCENTS).toInt()

        val activePercentage = dailyData.daySummaries.filter { it.completedTasks > 0 }.size
            .div(dailyData.daySummaries.size.toFloat())
            .times(FULL_PERCENTS)
            .toInt()


        if (_uiState.value != AnalyticsState.Day) {
            val bestDay = dailyData.daySummaries.maxWithOrNull(compareBy({ it.hours }, { it.completedTasks }))
            val worstDay = dailyData.daySummaries.filter { it.completedTasks > 0 }
                .minWithOrNull(compareBy({ it.hours }, { it.completedTasks }))
            _bestDay.value = bestDay
            _worstDay.value = worstDay
        } else {
            _bestDay.value = null
            _worstDay.value = null
        }

        _completedTasks.value = filteredTasks.size.toString()
        _workTime.value = "%.1f".format(calculateCardStats(filteredTasks).workHours)
        _averageDifficulty.intValue = calculateCardStats(filteredTasks).averageDifficulty
        _productivityPeak.intValue = calculateCardStats(filteredTasks).productivityPeak
        _longestTask.value = longestTask?.name ?: ""
        _averageMood.value = TaskMood.entries.find { it.moodValue == averageMood }?.emoji ?: ""

        _tasksByDays.value = dailyData.tasksByDays
        _moodByDays.value = dailyData.moodByDays

        _completionPercent.intValue = completionPercentage
        _moodPercentage.intValue = moodPercentage
        _activeDaysPercentage.intValue = activePercentage
    }

    fun updateAnalyticsState(state: AnalyticsState) {
        _uiState.value = state
        updateStats()
    }

    fun getAllTasks() {
        viewModelScope.launch {
            try {
                _goals.value = analyticsRepository.getAllGoals()
                _tasks.value = _goals.value.flatMap { it.tasks }
                updateStats()
            } catch (e: SQLiteException) {
                Log.e("AnalyticsViewModel", "Error fetching goals", e)
                _goals.value = emptyList()
                _tasks.value = emptyList()
            }
        }
    }

    fun getColorForTasks(tasks: Int): Float {
        if (tasks <= 0) return NO_PROGRESS_ALPHA
        return (tasks / MAX_TASK_INTENSITY).coerceAtMost(FULL_PROGRESS_ALPHA)
    }

    companion object {
        private const val MAX_TASK_INTENSITY = 5f
        private const val NO_PROGRESS_ALPHA = 0f
        private const val FULL_PROGRESS_ALPHA = 1f
        private const val ONE_DAY = 1
        private const val FULL_PERCENTS = 100
    }
}

data class DayProgress(
    val dayName: String,
    val completedTasks: Int,
    val failedTasks: Int,
    val totalTasks: Int
)

data class DayMood(
    val dayName: String,
    val averageMood: String,
    val emoji: String,
)

data class DaySummary(
    val dayName: String,
    val completedTasks: Int,
    val hours: Double,
    val emoji: String
)

data class AnalyticDataState(
    val tasksByDays: MutableList<DayProgress>,
    val moodByDays: MutableList<DayMood>,
    val daySummaries: MutableList<DaySummary>
)

data class CardStats(
    val workHours: Double,
    val averageDifficulty: Int,
    val productivityPeak: Int,
    val averageMood: Int
)
