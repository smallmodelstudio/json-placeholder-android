package uk.co.fredjames.jsonplaceholder.navigation

import kotlinx.serialization.Serializable

/** Sealed hierarchy for type-safe navigation routes using kotlinx.serialization. */
sealed interface NavigationRoute {
    @Serializable
    data object Posts : NavigationRoute

    @Serializable
    data object People : NavigationRoute

    @Serializable
    data object Albums : NavigationRoute

    @Serializable
    data object Settings : NavigationRoute
}
