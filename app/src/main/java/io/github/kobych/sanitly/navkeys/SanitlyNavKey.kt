package io.github.kobych.sanitly.navkeys

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed class SanitlyNavKey : NavKey {
    @Serializable
    data object StatusSurveyNavKey : SanitlyNavKey()

    @Serializable
    data object SanitlyMainScreenNavKey : SanitlyNavKey()
}
