package io.github.kobych.sanitly.ui.viewmodels

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.kobych.sanitly.data.repositories.UserPreferencesRepository
import io.github.kobych.sanitly.ui.models.OptionsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OptionsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    @ApplicationContext context: Context
) :
    ViewModel() {
    private val _uiState = MutableStateFlow<OptionsState>(OptionsState.Options)
    val uiState: StateFlow<OptionsState> = _uiState

    val notificationsState = userPreferencesRepository.notificationsFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(),
        true
    )

    val notificationsSound = userPreferencesRepository.notificationSoundFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(),
        true
    )

    val themeState = userPreferencesRepository.themeModeFlow.map { name ->
        UserPreferencesRepository.AppTheme.entries.firstOrNull { it.themeName == name }
            ?: UserPreferencesRepository.AppTheme.SYSTEM
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        UserPreferencesRepository.AppTheme.SYSTEM
    )

    val appVersion: String = try {
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.PackageInfoFlags.of(0)
            )
        } else {
            context.packageManager.getPackageInfo(context.packageName, 0)
        }
        packageInfo.versionName ?: "0.0.0"
    } catch (e: PackageManager.NameNotFoundException) {
        Log.e("OptionsViewModel", "Version not found", e)
        "0.0.0"
    }

    fun updateOptionsState(state: OptionsState) {
        _uiState.value = state
    }

    fun toPreviousState() {
        _uiState.update { currentState ->
            when (currentState) {
                OptionsState.Notifications, OptionsState.About, OptionsState.Theme -> OptionsState.Options
                OptionsState.Options -> OptionsState.Options
            }
        }
    }

    fun toggleNotifications(isNotificationsOn: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveNotificationsPreference(isNotificationsOn)
        }
    }

    fun toggleNotificationsSound(isNotificationsSoundOn: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveNotificationsSoundPreference(isNotificationsSoundOn)
        }
    }

    fun changeTheme(appTheme: UserPreferencesRepository.AppTheme) {
        viewModelScope.launch {
            userPreferencesRepository.saveThemeModePreference(appTheme)
        }
    }
}
