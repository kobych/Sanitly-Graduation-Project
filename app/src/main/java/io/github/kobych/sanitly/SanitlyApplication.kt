package io.github.kobych.sanitly

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ProcessLifecycleOwner
import dagger.hilt.android.HiltAndroidApp
import io.github.kobych.sanitly.ui.managers.ForegroundServiceManager
import javax.inject.Inject

private const val USER_PREFERENCES = "user_preferences"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    USER_PREFERENCES,
)

/**
 * Application entry point.
 *
 * Responsibilities:
 * - Triggers Hilt component generation (`@HiltAndroidApp`).
 * - Registers [ForegroundServiceManager] as a process-lifecycle observer so it can start/stop
 *   [TaskForegroundService] when the app goes to background or returns to foreground.
 * - Creates the two notification channels required by [TaskForegroundService] on API 26+:
 *   a high-importance channel (with sound) and a silent channel.
 */
@HiltAndroidApp
class SanitlyApplication : Application() {
    @Inject
    lateinit var foregroundServiceManager: ForegroundServiceManager

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(foregroundServiceManager)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "ForegroundServiceChannelId",
                "Service Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            val silentChannel = NotificationChannel(
                "ForegroundServiceChannelSilentId",
                "Service Notifications",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            notificationManager.createNotificationChannel(silentChannel)
        }
    }

    companion object {
        /** True after the initial remote data load completes;
         * prevents redundant network calls across recompositions. */
        var isDataLoaded: Boolean = false
    }
}
