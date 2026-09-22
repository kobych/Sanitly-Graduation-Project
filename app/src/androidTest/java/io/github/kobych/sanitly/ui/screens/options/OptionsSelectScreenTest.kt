package io.github.kobych.sanitly.ui.screens.options

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import io.github.kobych.sanitly.R
import io.github.kobych.sanitly.ui.models.OptionsState
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test

class OptionsSelectScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun optionsSelectScreen_displaysOptions() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val notificationsString: String = context.getString(R.string.options_notifications_title)
        val themeString: String = context.getString(R.string.options_theme_title)
        val aboutString: String = context.getString(R.string.options_about_title)

        composeTestRule.setContent {
            OptionsSelectScreen(
                onClick = {},
            )
        }

        composeTestRule.onNodeWithText(notificationsString).assertExists()
        composeTestRule.onNodeWithText(themeString).assertExists()
        composeTestRule.onNodeWithText(aboutString).assertExists()
    }

    @Test
    fun optionsSelectScreen_clickNotifications_callsNotifications() {
        var selectedState: OptionsState? = null

        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val notificationsString: String = context.getString(R.string.options_notifications_title)

        composeTestRule.setContent {
            OptionsSelectScreen(
                onClick = { selectedState = it },
            )
        }

        composeTestRule.onNodeWithText(notificationsString).performClick()

        assertEquals(OptionsState.Notifications, selectedState)
    }
}
