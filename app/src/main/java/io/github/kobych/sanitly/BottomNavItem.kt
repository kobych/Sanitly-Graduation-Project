package io.github.kobych.sanitly

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

interface BottomNavItem {
    /**
     * Denotes that iconId returns drawable resource reference
     */
    @get:DrawableRes
    val iconId: Int
    @get:StringRes
    val titleId: Int
    val title: String
}
