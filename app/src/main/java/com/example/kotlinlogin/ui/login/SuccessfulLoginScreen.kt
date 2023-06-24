package com.example.kotlinlogin.ui.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.kotlinlogin.LoginAppBar
import com.example.kotlinlogin.R
import com.example.kotlinlogin.ui.navigation.NavigationDestination

object SuccessDestination : NavigationDestination {
    override val route = "Success"
    override val titleRes = R.string.app_name
}

/**
 * Login Success Screen
 * @param onNavigateUp the function used to navigate up using the navigation bar at the bottom
 */
@Composable
fun SuccessfulLoginScreen(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            LoginAppBar(
                title = stringResource(R.string.login),
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
            .padding(innerPadding)
            .fillMaxSize()
        ) {
            Text(
                text = stringResource(R.string.successful_login),
                modifier = modifier
                    .align(Alignment.Center)
            )
        }
    }
}