package io.github.kobych.sanitly.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import io.github.kobych.sanitly.navkeys.BottomBarNavKey
import io.github.kobych.sanitly.ui.navigation.Navigator
import io.github.kobych.sanitly.ui.navigation.rememberNavigationState
import io.github.kobych.sanitly.ui.navigation.toEntries
import io.github.kobych.sanitly.ui.screens.analytics.AnalyticsFlowScreen
import io.github.kobych.sanitly.ui.screens.diary.DiaryFlowScreen
import io.github.kobych.sanitly.ui.screens.goalbuilder.GoalBuilderFlowScreen
import io.github.kobych.sanitly.ui.screens.goallist.GoalListFlowScreen
import io.github.kobych.sanitly.ui.screens.options.OptionsFlowScreen

val LocalNavigator = compositionLocalOf<Navigator> {
    error("No navigator provided")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomBarMainScreen(
    modifier: Modifier = Modifier
) {
    /**
     * Bottom bar items are passed as topLevelRoute
     *
     * Inside these items can be their own transition, but they'll not be top level route
     */
    val navigationState = rememberNavigationState(
        startRoute = BottomBarNavKey.GoalsNavKey,
        topLevelRoutes = bottomNavItems,
    )
    val navigator = remember(BottomBarNavKey.GoalsNavKey) {
        Navigator(navigationState)
    }

    CompositionLocalProvider(LocalNavigator provides navigator) {
        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                ) {
                    bottomNavItems.forEach { item ->
                        val isSelected = item == navigationState.topLevelRoute
                        NavigationBarItem(
                            selected = isSelected,
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            onClick = { navigator.navigate(item) },
                            icon = {
                                Icon(
                                    painter = painterResource(item.iconId),
                                    contentDescription = item.title,
                                )
                            },
                            label = {
                                Text(
                                    item.title,
                                )
                            },
                        )
                    }
                }
            },
            modifier = modifier.fillMaxSize()
        ) { innerPadding ->
            val entryProvider = buildEntryProvider(
                onCreatedToGoals = { navigator.navigate(BottomBarNavKey.GoalsNavKey) },
                innerPadding = innerPadding
            )
            NavDisplay(
                entries = navigationState.toEntries(entryProvider),
                onBack = { navigator.goBack() },
                sceneStrategy = remember { DialogSceneStrategy() },
            )
        }
    }
}

private val bottomNavItems =
    setOf(
        BottomBarNavKey.AnalyticsNavKey,
        BottomBarNavKey.DiaryNavKey,
        BottomBarNavKey.GoalsNavKey,
        BottomBarNavKey.GoalBuilderNavKey,
        BottomBarNavKey.OptionsNavKey,
    )

fun buildEntryProvider(
    onCreatedToGoals: () -> Unit,
    innerPadding: PaddingValues,
): (NavKey) -> NavEntry<NavKey> {
    val innerPaddingModifier = Modifier.padding(innerPadding)
    val entryProvider = entryProvider<NavKey> {
        entry<BottomBarNavKey.AnalyticsNavKey> {
            AnalyticsFlowScreen(modifier = innerPaddingModifier)
        }
        entry<BottomBarNavKey.DiaryNavKey> {
            DiaryFlowScreen(modifier = innerPaddingModifier)
        }
        entry<BottomBarNavKey.GoalsNavKey> {
            GoalListFlowScreen(modifier = innerPaddingModifier)
        }
        entry<BottomBarNavKey.GoalBuilderNavKey> {
            GoalBuilderFlowScreen(
                onCreatedToGoals = { onCreatedToGoals() },
                modifier = innerPaddingModifier
            )
        }
        entry<BottomBarNavKey.OptionsNavKey> {
            OptionsFlowScreen(modifier = innerPaddingModifier)
        }
    }
    return entryProvider
}
