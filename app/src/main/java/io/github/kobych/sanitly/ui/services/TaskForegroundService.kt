package io.github.kobych.sanitly.ui.services

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import io.github.kobych.sanitly.MainActivity
import io.github.kobych.sanitly.R
import io.github.kobych.sanitly.data.repositories.GoalListRepository
import io.github.kobych.sanitly.data.repositories.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

/**
 * Foreground service that shows a persistent notification while a task timer is active.
 *
 * Started and stopped by [ForegroundServiceManager] based on app lifecycle. The notification
 * text updates in milestones (< 1 h, < 2 h, …) as [GoalListRepository.currentSeconds] ticks up.
 * If the user has disabled notifications in preferences, the notification content is not updated
 * (but the service itself keeps running so the timer is not lost).
 *
 * Two notification channels are used: a high-importance channel with sound and a silent channel.
 * The channel is chosen once at service start based on [UserPreferencesRepository.notificationSoundFlow].
 */
@AndroidEntryPoint
class TaskForegroundService : Service() {
    /** Intents sent to this service must carry one of these actions. */
    enum class Action {
        START, STOP
    }

    private var serviceJob: Job? = null
    private var coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository
    @Inject
    lateinit var goalListRepository: GoalListRepository

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Action.START.toString() -> start()
            Action.STOP.toString() -> stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    @SuppressLint("ForegroundServiceType")
    private fun start() {
        serviceJob?.cancel()

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        fun createNotification(
            contentText: String,
            channelId: String
        ) =
            NotificationCompat.Builder(this@TaskForegroundService, channelId)
                .setSmallIcon(R.drawable.trophy)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(contentText)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .build()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        serviceJob = coroutineScope.launch {
            var lastText = ""

            val channel = if (userPreferencesRepository.notificationSoundFlow.first()) {
                "ForegroundServiceChannelId"
            } else {
                "ForegroundServiceChannelSilentId"
            }

            startForeground(1, createNotification(
                getString(R.string.notification_status_just_started),
                channelId = channel
            ))

            goalListRepository.currentSeconds.collect { seconds ->
                val duration = seconds.seconds

                val updatedText = when {
                    duration < 1.hours -> getString(R.string.notification_status_just_started)
                    duration < 2.hours -> getString(R.string.notification_status_one_hour)
                    duration < 3.hours -> getString(R.string.notification_status_two_hours)
                    duration < 4.hours -> getString(R.string.notification_status_three_hours)
                    else -> getString(R.string.notification_status_long_time)
                }

                if (!goalListRepository.isTimerRunning.value) {
                    stopSelf()
                    return@collect
                }

                if (updatedText != lastText) {
                    if (userPreferencesRepository.notificationsFlow.first()) {
                        lastText = updatedText
                        notificationManager.notify(1, createNotification(updatedText, channelId = channel))
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}
