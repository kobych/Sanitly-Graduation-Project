package io.github.kobych.sanitly.viewmodel

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import io.github.kobych.sanitly.MainDispatcherRule
import io.github.kobych.sanitly.data.repositories.UserPreferencesRepository
import io.github.kobych.sanitly.ui.models.OptionsState
import io.github.kobych.sanitly.ui.viewmodels.OptionsViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class OptionsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<UserPreferencesRepository>()
    private val context = mockk<Context>()
    private lateinit var viewModel: OptionsViewModel

    @Before
    fun setup() {
        every { repository.notificationsFlow } returns MutableStateFlow(true)
        every { repository.notificationSoundFlow } returns MutableStateFlow(true)
        every { repository.themeModeFlow } returns MutableStateFlow("SYSTEM")

        val packageManager = mockk<PackageManager>()
        val packageInfo = PackageInfo().apply { versionName = "1.0.0" }

        every { context.packageName } returns "io.github.kobych.sanitly"
        every { context.packageManager } returns packageManager
        every { packageManager.getPackageInfo(any<String>(), any<Int>()) } returns packageInfo

        viewModel = OptionsViewModel(repository, context)
    }

    @Test
    fun updateOptionsState_normalState_stateUpdates() {
        viewModel.updateOptionsState(OptionsState.Notifications)

        assertEquals(OptionsState.Notifications, viewModel.uiState.value)
    }

    @Test
    fun toPreviousState_NotificationsState_stateUpdatesToOptions() {
        viewModel.updateOptionsState(OptionsState.Notifications)
        viewModel.toPreviousState()

        assertEquals(OptionsState.Options, viewModel.uiState.value)
    }

    @Test
    fun toPreviousState_OptionsState_stateRemainsOptions() {
        viewModel.updateOptionsState(OptionsState.Notifications)
        viewModel.toPreviousState()

        assertEquals(OptionsState.Options, viewModel.uiState.value)
    }

    @Test
    fun toggleNotifications_setToFalse_disablesNotifications() = runTest {
        val notificationsFlow = MutableStateFlow(true)
        every { repository.notificationsFlow } returns notificationsFlow
        coEvery { repository.saveNotificationsPreference(any()) } answers {
            notificationsFlow.value = firstArg()
        }

        viewModel.toggleNotifications(false)

        assertEquals(true, viewModel.notificationsState.value)
    }

    @Test
    fun toggleNotifications_setToTrue_enablesNotifications() = runTest {
        val notificationsFlow = MutableStateFlow(false)
        every { repository.notificationsFlow } returns notificationsFlow
        coEvery { repository.saveNotificationsPreference(any()) } answers {
            notificationsFlow.value = firstArg()
        }

        viewModel.toggleNotifications(true)

        assertEquals(true, viewModel.notificationsState.value)
    }

    @Test
    fun toggleNotificationsSound_setToTrue_enablesNotificationsSound() = runTest {
        val notificationsSoundFlow = MutableStateFlow(false)
        every { repository.notificationSoundFlow } returns notificationsSoundFlow
        coEvery { repository.saveNotificationsSoundPreference((any())) } answers {
            notificationsSoundFlow.value = firstArg()
        }

        viewModel.toggleNotificationsSound(true)

        assertEquals(true, viewModel.notificationsSound.value)
    }
}
