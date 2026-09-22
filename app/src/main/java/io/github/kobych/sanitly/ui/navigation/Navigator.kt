package io.github.kobych.sanitly.ui.navigation

import androidx.navigation3.runtime.NavKey

/**
 * Class responsible for navigation in the app
 *
 * @property state state of NavigationState type
 */
class Navigator(
    val state: NavigationState,
) {
    /**
     * Moves to given route
     *
     * @param route required route
     */
    fun navigate(route: NavKey) {
        if (route in state.backStacks.keys) {
            state.topLevelRoute = route
        } else {
            state.backStacks[state.topLevelRoute]?.add(route)
        }
    }

    /**
     * Returns to previous route
     */
    fun goBack() {
        val currentStack = state.backStacks[state.topLevelRoute] ?:
            error("Stack for ${state.topLevelRoute} not found")
        val currentRoute = currentStack.last()

        if (currentRoute == state.topLevelRoute) {
            state.topLevelRoute = state.startRoute
        } else {
            currentStack.removeLastOrNull()
        }
    }

    /**
     * Cleans current stack to 1 element
     * Returns topLevelRoute back to startRoute
     */
    fun resetRoute() {
        val currentStack = state.backStacks[state.topLevelRoute] ?:
        error("Stack for ${state.topLevelRoute} not found")
        while (currentStack.size > 1) {
            currentStack.removeLastOrNull()
        }
        state.topLevelRoute = state.startRoute
    }
}
