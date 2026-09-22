package io.github.kobych.sanitly.ui.screens.options

import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import io.github.kobych.sanitly.data.repositories.UserPreferencesRepository
import org.junit.Rule
import org.junit.Test

class ThemeSelectScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun themeSelectScreen_clickLight_callsOnSelectedWithLight() {
        val lightThemeString = UserPreferencesRepository.AppTheme.LIGHT.themeName
        var currentTheme = UserPreferencesRepository.AppTheme.SYSTEM

        composeTestRule.setContent {
            ThemeSelectScreen(
                selectedOption = currentTheme,
                onSelected = { currentTheme = it }
            )
        }

        composeTestRule.onNodeWithText(lightThemeString).performClick()

        assert(currentTheme == UserPreferencesRepository.AppTheme.LIGHT)
    }

    @Test
    fun themeSelectScreen_lightThemeAssigned_lightRadioButtonSelected() {
        var selectedTheme = UserPreferencesRepository.AppTheme.LIGHT

        composeTestRule.setContent {
            ThemeSelectScreen(
                selectedOption = selectedTheme,
                onSelected = { selectedTheme = it }
            )
        }

        composeTestRule
            .onNodeWithTag(selectedTheme.themeName)
            .assertIsSelected()

        assert(selectedTheme == UserPreferencesRepository.AppTheme.LIGHT)
    }
}
