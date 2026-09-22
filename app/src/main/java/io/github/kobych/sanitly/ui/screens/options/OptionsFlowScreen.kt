package io.github.kobych.sanitly.ui.screens.options

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import io.github.kobych.sanitly.R
import io.github.kobych.sanitly.ui.models.OptionsState
import io.github.kobych.sanitly.ui.viewmodels.OptionsViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun OptionsFlowScreen(modifier: Modifier = Modifier) {
    val viewModel: OptionsViewModel = hiltViewModel()
    val uiState: OptionsState by viewModel.uiState.collectAsState()
    val isNotificationsEnabled by viewModel.notificationsState.collectAsState()
    val isNotificationsSoundEnabled by viewModel.notificationsSound.collectAsState()
    val themeState by viewModel.themeState.collectAsState()

    Scaffold(
        topBar = {
            OptionsTopAppBar(
                uiState = uiState,
                onPrevious = { viewModel.toPreviousState() }
            )
        },
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.background)
            .semantics {
                testTagsAsResourceId = true
            }
    ) { innerPadding ->
        when (uiState) {
            OptionsState.Options -> OptionsSelectScreen(
                onClick = { state -> viewModel.updateOptionsState(state) },
                modifier = Modifier.padding(innerPadding)
            )

            OptionsState.Notifications -> NotificationsOptionsScreen(
                toggleNotifications = { viewModel.toggleNotifications(it) },
                toggleNotificationsSound = { viewModel.toggleNotificationsSound(it) },
                isNotificationsEnabled = isNotificationsEnabled,
                isNotificationsSoundEnabled = isNotificationsSoundEnabled,
                modifier = Modifier.padding(innerPadding)
            )

            OptionsState.Theme -> ThemeSelectScreen(
                selectedOption = themeState,
                onSelected = { viewModel.changeTheme(it) },
                modifier = Modifier.padding(innerPadding)
            )

            OptionsState.About -> VersionScreen(
                version = viewModel.appVersion,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OptionsTopAppBar(
    uiState: OptionsState,
    onPrevious: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        navigationIcon = {
            when (uiState) {
                OptionsState.About, OptionsState.Notifications, OptionsState.Theme -> {
                    IconButton(onClick = { onPrevious() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "To previous state"
                        )
                    }
                }

                OptionsState.Options -> {}
            }
        },
        title = {
            Text(
                text = when (uiState) {
                    OptionsState.Options -> stringResource(R.string.options_screen_title)
                    OptionsState.Notifications -> stringResource(R.string.options_notifications_title)
                    OptionsState.Theme -> stringResource(R.string.options_theme_title)
                    OptionsState.About -> stringResource(R.string.options_about_title)
                },
                style = MaterialTheme.typography.titleLarge
            )
        },
        modifier = modifier
    )
}

@Preview(name = "Options Screen")
@Composable
fun OptionsFlowScreenPreview() {
    OptionsFlowScreen()
}
