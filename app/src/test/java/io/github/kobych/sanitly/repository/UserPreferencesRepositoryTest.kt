package io.github.kobych.sanitly.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import io.github.kobych.sanitly.data.repositories.UserPreferencesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class UserPreferencesRepositoryTest {
    private val dataStore = mockk<DataStore<Preferences>>()
    private val preferences = mockk<Preferences>()
    private val mutablePreferences = mockk<MutablePreferences>(relaxed = true)
    private lateinit var repository: UserPreferencesRepository

    @Before
    fun setUp() {
        every { dataStore.data } returns flowOf(preferences)

        coEvery { dataStore.updateData(any()) } coAnswers {
            val transform = firstArg<suspend (Preferences) -> Preferences>()
            transform(mutablePreferences)
            preferences
        }

        repository = UserPreferencesRepository(dataStore)
    }

    @Test
    fun saveNotificationsPreference_callsUpdateData() = runTest {
        repository.saveNotificationsPreference(false)

        coVerify { dataStore.updateData(any()) }
    }

    @Test
    fun notificationsFlow_returnsValueFromPreferences() = runTest {
        every { preferences[any<Preferences.Key<Boolean>>()] } returns false

        val result = repository.notificationsFlow.first()

        assertEquals(false, result)
    }

    @Test
    fun saveNotificationsSoundPreference_callsUpdateData() = runTest {
        repository.saveNotificationsSoundPreference(false)

        coVerify { dataStore.updateData(any()) }
    }

    @Test
    fun saveSurveyCompletedPreference_callsUpdateData() = runTest {
        repository.saveSurveyCompletedPreference()

        coVerify { dataStore.updateData(any()) }
    }

    @Test
    fun saveThemeModePreference_callsUpdateData() = runTest {
        repository.saveThemeModePreference(UserPreferencesRepository.AppTheme.DARK)

        coVerify { dataStore.updateData(any()) }
    }

    @Test
    fun notificationSoundFlow_returnsValueFromPreferences() = runTest {
        every { preferences[any<Preferences.Key<Boolean>>()] } returns false

        val result = repository.notificationSoundFlow.first()

        assertEquals(false, result)
    }

    @Test
    fun themeModeFlow_returnsValueFromPreferences() = runTest {
        every { preferences[any<Preferences.Key<String>>()] } returns UserPreferencesRepository.AppTheme.LIGHT.themeName

        val result = repository.themeModeFlow.first()

        assertEquals(UserPreferencesRepository.AppTheme.LIGHT.themeName, result)
    }

    @Test
    fun surveyCompletedFlow_returnsValueFromPreferences() = runTest {
        every { preferences[any<Preferences.Key<Boolean>>()] } returns true

        val result = repository.surveyCompletedFlow.first()

        assertEquals(true, result)
    }
}
