package io.github.kobych.sanitly

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import dagger.hilt.android.AndroidEntryPoint
import io.github.kobych.sanitly.data.repositories.UserPreferencesRepository
import io.github.kobych.sanitly.navkeys.SanitlyNavKey
import io.github.kobych.sanitly.ui.navigation.Navigator
import io.github.kobych.sanitly.ui.navigation.rememberNavigationState
import io.github.kobych.sanitly.ui.navigation.toEntries
import io.github.kobych.sanitly.ui.screens.BottomBarMainScreen
import io.github.kobych.sanitly.ui.screens.statussurvey.StatusSurveyFlowScreen
import io.github.kobych.sanitly.ui.theme.SanitlyTheme
import io.github.kobych.sanitly.ui.viewmodels.MainActivityViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }

        enableEdgeToEdge()
        setContent {
            val mainActivityViewModel: MainActivityViewModel = hiltViewModel()
            val isSurveyCompleted by mainActivityViewModel.surveyCompletedFlow
                .collectAsStateWithLifecycle(initialValue = null)
            val changeThemeFlow by mainActivityViewModel.changeThemeFlow
                .collectAsStateWithLifecycle(initialValue = UserPreferencesRepository.AppTheme.SYSTEM)

            val surveyStatus = isSurveyCompleted ?: return@setContent

            val startRoute = if (!surveyStatus) {
                SanitlyNavKey.StatusSurveyNavKey
            } else {
                SanitlyNavKey.SanitlyMainScreenNavKey
            }

            val navigationState = rememberNavigationState(
                startRoute = startRoute,
                topLevelRoutes = mainNavItems,
            )
            val navigator = remember(startRoute) {
                Navigator(navigationState)
            }

            val entryProvider = entryProvider<NavKey> {
                entry<SanitlyNavKey.StatusSurveyNavKey> {
                    StatusSurveyFlowScreen(onSurveyCompleted = {
                        navigator.navigate(SanitlyNavKey.SanitlyMainScreenNavKey)
                    })
                }
                entry<SanitlyNavKey.SanitlyMainScreenNavKey> {
                    BottomBarMainScreen()
                }
            }


            val isDarkTheme = when (changeThemeFlow) {
                UserPreferencesRepository.AppTheme.SYSTEM -> isSystemInDarkTheme()
                UserPreferencesRepository.AppTheme.DARK -> true
                UserPreferencesRepository.AppTheme.LIGHT -> false
            }

            SanitlyTheme(darkTheme = isDarkTheme) {
                NavDisplay(
                    entries = navigationState.toEntries(entryProvider),
                    onBack = { navigator.goBack() },
                    sceneStrategy = remember { DialogSceneStrategy() },
                    modifier = Modifier.statusBarsPadding()
                )
            }
        }
    }
}

private val mainNavItems =
    setOf(
        SanitlyNavKey.StatusSurveyNavKey,
        SanitlyNavKey.SanitlyMainScreenNavKey,
    )
