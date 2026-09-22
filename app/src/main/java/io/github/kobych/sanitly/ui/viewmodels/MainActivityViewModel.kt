package io.github.kobych.sanitly.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kobych.sanitly.data.repositories.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {
    /**
     * Flow stops after collectAsStateWithLifecycle() in MainActivity stops collecting state
     * and cancels connection with userPreferencesRepository to stop consuming resources
     */
    val surveyCompletedFlow = userPreferencesRepository.surveyCompletedFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null,
        )

    val changeThemeFlow = userPreferencesRepository.themeModeFlow.map { themeName ->
        UserPreferencesRepository.AppTheme.entries.firstOrNull { it.themeName == themeName }
            ?: UserPreferencesRepository.AppTheme.SYSTEM
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = UserPreferencesRepository.AppTheme.SYSTEM
    )
}
