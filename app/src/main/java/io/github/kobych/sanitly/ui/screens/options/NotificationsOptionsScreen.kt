package io.github.kobych.sanitly.ui.screens.options

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.kobych.sanitly.R
import io.github.kobych.sanitly.ui.theme.Dimens
import io.github.kobych.sanitly.ui.theme.Size

@Composable
fun NotificationsOptionsScreen(
    isNotificationsEnabled: Boolean,
    isNotificationsSoundEnabled: Boolean,
    toggleNotifications: (Boolean) -> Unit,
    toggleNotificationsSound: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Dimens.L.padding),
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.L.padding)
    ) {
        SwitchOptionsItem(
            textId = R.string.options_notifications_switch,
            onCheckedChange = { toggleNotifications(it) },
            checked = isNotificationsEnabled,
            switchTag = "enableNotifications"
        )
        SwitchOptionsItem(
            textId = R.string.options_notifications_sound_switch,
            onCheckedChange = { toggleNotificationsSound(it) },
            checked = isNotificationsSoundEnabled,
            switchTag = "enableNotificationsSound"
        )
    }
}

@Composable
private fun SwitchOptionsItem(
    checked: Boolean,
    textId: Int,
    onCheckedChange: (Boolean) -> Unit,
    switchTag: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = modifier
            .height(Size.Button.XL.dp)
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.L.padding)) {
            Text(text = stringResource(textId))
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = checked,
                onCheckedChange = {
                    onCheckedChange(it)
                },
                modifier = Modifier.testTag(switchTag)
            )
        }
    }
}

@Preview(name = "Notifications Options Screen")
@Composable
fun NotificationsOptionsScreenPreview() {
    NotificationsOptionsScreen(
        isNotificationsEnabled = false,
        isNotificationsSoundEnabled = false,
        toggleNotifications = {},
        toggleNotificationsSound = {}
    )
}
