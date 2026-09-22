package io.github.kobych.sanitly.navkeys

import androidx.navigation3.runtime.NavKey
import io.github.kobych.sanitly.BottomNavItem
import io.github.kobych.sanitly.R
import kotlinx.serialization.Serializable

sealed class BottomBarNavKey :
    BottomNavItem,
    NavKey {
    @Serializable
    data object AnalyticsNavKey : BottomBarNavKey() {
        override val iconId: Int = R.drawable.analytics
        override val titleId: Int = R.string.analytics_screen_title
        override val title: String = "Analytics"
    }

    @Serializable
    data object DiaryNavKey : BottomBarNavKey() {
        override val iconId: Int = R.drawable.book
        override val titleId: Int = R.string.diary_screen_title
        override val title: String = "Diary"
    }

    @Serializable
    data object GoalsNavKey : BottomBarNavKey() {
        override val iconId: Int = R.drawable.trophy
        override val titleId: Int = R.string.goal_process_title
        override val title: String = "Goals"
    }

    @Serializable
    data object GoalBuilderNavKey : BottomBarNavKey() {
        override val iconId: Int = R.drawable.table_edit
        override val titleId: Int = R.string.goal_builder_screen_title
        override val title: String = "Builder"
    }

    @Serializable
    data object OptionsNavKey : BottomBarNavKey() {
        override val iconId: Int = R.drawable.settings
        override val titleId: Int = R.string.options_screen_title
        override val title: String = "Options"
    }
}
