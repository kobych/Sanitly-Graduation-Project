package io.github.kobych.sanitly.data.repositories

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * Manages persistent app-level preferences backed by Jetpack DataStore.
 *
 * All preference reads are exposed as [Flow]s so the UI can react to changes automatically.
 * Write operations are suspend functions that must be called from a coroutine.
 */
class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>,
) {
    /** Supported app theme modes. The [themeName] string is persisted in DataStore. */
    enum class AppTheme(val themeName: String) {
        SYSTEM("Системная"),
        DARK("Тёмная"),
        LIGHT("Светлая")
    }

    /** Emits whether push notifications are enabled. Defaults to `true`. */
    val notificationsFlow: Flow<Boolean> = observePreference(IS_NOTIFICATIONS_ON, true)
    /** Emits whether notification sound is enabled. Defaults to `true`. */
    val notificationSoundFlow: Flow<Boolean> = observePreference(IS_NOTIFICATIONS_SOUND_ON, true)
    /** Emits the currently selected theme name (one of [AppTheme.themeName]). */
    val themeModeFlow: Flow<String> = observePreference(THEME_MODE, AppTheme.SYSTEM.themeName)
    /** Emits whether the onboarding status survey has been completed. Defaults to `false`. */
    val surveyCompletedFlow: Flow<Boolean> = observePreference(IS_SURVEY_COMPLETED, false)

    private fun <T> observePreference(
        preferenceKey: Preferences.Key<T>,
        defaultValue: T,
    ): Flow<T> =
        dataStore.data
            .catch {
                handleReadingException(it)
                emit(emptyPreferences())
            }
            .map { preferences ->
                preferences[preferenceKey] ?: defaultValue
            }

    private fun handleReadingException(it: Throwable) {
        if (it is IOException) {
            Log.e(TAG, "Error reading preferences.", it)
        } else {
            throw it
        }
    }

    suspend fun saveNotificationsPreference(isNotificationsOn: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_NOTIFICATIONS_ON] = isNotificationsOn
        }
    }

    suspend fun saveNotificationsSoundPreference(isNotificationsSoundOn: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_NOTIFICATIONS_SOUND_ON] = isNotificationsSoundOn
        }
    }

    suspend fun saveSurveyCompletedPreference() {
        dataStore.edit { preferences ->
            preferences[IS_SURVEY_COMPLETED] = true
        }
    }

    suspend fun saveThemeModePreference(theme: AppTheme) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE] = theme.themeName
        }
    }

    private companion object {
        private val IS_NOTIFICATIONS_ON = booleanPreferencesKey("is_notifications_on")
        private val IS_NOTIFICATIONS_SOUND_ON = booleanPreferencesKey("is_notifications_sound_on")
        private val IS_SURVEY_COMPLETED = booleanPreferencesKey("is_survey_completed")

        private val THEME_MODE = stringPreferencesKey("theme_mode")
        private const val TAG = "UserPreferencesRepository"
    }
}
