package io.github.kobych.sanitly.ui.screens.options

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import io.github.kobych.sanitly.R
import org.junit.Rule
import org.junit.Test

class NotificationsOptionsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun notificationsScreen_displaysOptions() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val notificationDisplayString: String = context.getString(R.string.options_notifications_switch)
        val notificationSoundString: String = context.getString(R.string.options_notifications_sound_switch)

        composeTestRule.setContent {
            NotificationsOptionsScreen(
                isNotificationsEnabled = false,
                isNotificationsSoundEnabled = false,
                toggleNotifications = {},
                toggleNotificationsSound = {}
            )
        }

        composeTestRule
            .onNodeWithText(notificationDisplayString)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(notificationSoundString)
            .assertIsDisplayed()
    }

    @Test
    fun notificationsScreen_click_callsToggleNotifications() {
        var notificationsValue: Boolean? = null
        var notificationsSoundsValue: Boolean? = null

        composeTestRule.setContent {
            NotificationsOptionsScreen(
                isNotificationsEnabled = false,
                isNotificationsSoundEnabled = false,
                toggleNotifications = { notificationsValue = it },
                toggleNotificationsSound = { notificationsSoundsValue = it }
            )
        }

        composeTestRule.onNodeWithTag("enableNotifications").performClick()
        composeTestRule.onNodeWithTag("enableNotificationsSound").performClick()

        assert(notificationsValue == true)
        assert(notificationsSoundsValue == true)
    }
}
