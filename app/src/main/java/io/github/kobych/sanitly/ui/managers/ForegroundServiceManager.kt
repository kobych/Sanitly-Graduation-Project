package io.github.kobych.sanitly.ui.managers

import android.content.Context
import android.content.Intent
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.kobych.sanitly.data.repositories.GoalListRepository
import io.github.kobych.sanitly.ui.services.TaskForegroundService
import javax.inject.Inject

/**
 * Process-lifecycle observer that bridges the app visibility state and [TaskForegroundService].
 *
 * When the app moves to the **foreground** ([onStart]), the persistent notification is no longer
 * needed, so the service is stopped. When the app moves to the **background** ([onStop]) and a
 * task timer is still running, the foreground service is started so the user can track progress
 * via the notification without keeping the app open.
 */
class ForegroundServiceManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val goalListRepository: GoalListRepository
) : DefaultLifecycleObserver {
    override fun onStart(owner: LifecycleOwner) {
        val intent = Intent(context, TaskForegroundService::class.java)
        intent.action = TaskForegroundService.Action.STOP.toString()
        context.startService(intent)
    }

    override fun onStop(owner: LifecycleOwner) {
        if (goalListRepository.isTimerRunning.value) {
            val intent = Intent(context, TaskForegroundService::class.java)
            intent.action = TaskForegroundService.Action.START.toString()
            context.startForegroundService(intent)
        }
    }
}
