package com.example.kotlinlogin.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.kotlinlogin.ui.login.LoginDestination
import com.example.kotlinlogin.ui.login.LoginScreen

/**
 * Provide the navigation and composables for the app
 */
@Composable
fun InventoryNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    /**
     * Set up the NavHost; specifying the start destination as Login
     */
    NavHost(
        navController = navController,
        startDestination = LoginDestination.route,
        modifier = modifier
    ) {
        composable(route = LoginDestination.route) {
            LoginScreen(
                // Implement navigation function here
            )
        }
        // Implement other composables
    }
}