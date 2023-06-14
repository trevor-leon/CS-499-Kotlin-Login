package com.example.kotlinlogin.ui.navigation

/**
 * Interface describes each app screen destination based on route and title
 */
interface NavigationDestination {
    // Unique route name defining the Composable path
    val route: String
    // Title of the screen to be displayed in the app bar
    val titleRes: Int
}