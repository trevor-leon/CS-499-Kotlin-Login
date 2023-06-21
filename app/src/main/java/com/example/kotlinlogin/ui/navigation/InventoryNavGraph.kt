package com.example.kotlinlogin.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.kotlinlogin.ui.login.LoginDestination
import com.example.kotlinlogin.ui.login.LoginScreen
import com.example.kotlinlogin.ui.login.SuccessDestination
import com.example.kotlinlogin.ui.login.SuccessfulLoginScreen

/**
 * Provide the navigation and composables for the app
 */
@Composable
fun LoginNavHost(
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
                // Set the navigation destination to the successful login screen
                navigateToNextScreen = { navController.navigate(SuccessDestination.route) }
            )
        }
        // Implement other composables
        composable(route = SuccessDestination.route) {
            SuccessfulLoginScreen(
                onNavigateBack = { navController.navigate(LoginDestination.route) },
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}
