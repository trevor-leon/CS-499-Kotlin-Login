package com.example.kotlinlogin.ui.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlinlogin.InventoryAppBar
import com.example.kotlinlogin.R
import com.example.kotlinlogin.ui.AppViewModelProvider
import com.example.kotlinlogin.ui.navigation.NavigationDestination
import com.example.kotlinlogin.ui.theme.brandColor
import kotlinx.coroutines.launch


object LoginDestination : NavigationDestination {
    override val route = "Login"
    override val titleRes = R.string.app_name
}

/**
 * Display the Login screen
 */
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navigateToNextScreen: () -> Unit = {},
    loginViewModel: LoginViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val loginUiState = loginViewModel.loginUiState

    // Get a CoroutineScope bound to this composition to save and get data later
    val coroutineScope = rememberCoroutineScope()

    // Get the current context for the onClick functions
    val context = LocalContext.current
    // TODO: Probably remove Scaffold from Login
    Scaffold(
        topBar = {
            InventoryAppBar (
                title = stringResource(id = LoginDestination.titleRes),
                canNavigateBack = false
            )
        }
    ) { innerPadding ->
        LoginBody(
            loginUiState = loginUiState,
            // Pass the updateUiState function as the onValuesChange parameter
            onValuesChange = loginViewModel::updateUiState,
            onLoginClick = {
                coroutineScope.launch {
                    /* TODO: implement the loginClick function */
                    // if findLogin() finds the login, make a Toast stating so
                    if(loginViewModel.findLogin()) {
                        // Insert navigation function here to go to the next screen *Not implemented
                        Toast.makeText(context, "Login found!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Login not found!", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onAcctCreateClick = {
                coroutineScope.launch {
                    // If the login is not found, save the login to the database
                    if(!loginViewModel.findLogin()) {
                        // Insert navigation function here to go to the next screen *Not implemented
                        Toast.makeText(context, "Login stored!", Toast.LENGTH_SHORT).show()
                        loginViewModel.saveLogin()
                    } else {
                        Toast.makeText(context, "Login already exists!", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = modifier.padding(innerPadding)
        )
    }


}

/**
 * LoginBody
 */
@Composable
private fun LoginBody(
    loginUiState: LoginUiState,
    onValuesChange: (LoginDetails) -> Unit,
    onLoginClick: () -> Unit,
    onAcctCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
    ) {
        LogoAndAppName()
        UsernameAndPasswordTexts(
            loginDetails = loginUiState.loginDetails,
            onValuesChange = onValuesChange,
        )
        FieldOfButtons(
            onLoginClick = onLoginClick,
            onAcctCreateClick = onAcctCreateClick
        )
    }
}

// Set up the Composable that contains the Logo and App name.
@Composable
private fun LogoAndAppName(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = R.drawable.baseline_login_24),
            contentDescription = stringResource(id = R.string.logoDescription),
            modifier = Modifier
                .size(200.dp)
        )
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.displaySmall,
            color = brandColor,
            modifier = Modifier
                .padding(bottom = 32.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

/**
 * Create the Composable that holds the username and password text fields.
  */
@Composable
private fun UsernameAndPasswordTexts(
    loginDetails: LoginDetails,
    modifier: Modifier = Modifier,
    onValuesChange: (LoginDetails) -> Unit = {}
) {
    Column(
        modifier = modifier
    ) {
        // Email
        TextField(
            value = loginDetails.username,
            // Pass a copy of loginDetails with username set to the current username text
            onValueChange = { onValuesChange( loginDetails.copy(username = it))},
            singleLine = true,
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_email_24),
                    contentDescription = null
                )
            },
            label = { Text(text = stringResource(id = R.string.email)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier
                .padding(bottom = 12.dp)
        )

        // Password
        TextField(
            value = loginDetails.password,
            // Pass a copy of loginDetails with password set to the current password text
            onValueChange = { onValuesChange( loginDetails.copy(password = it)) },
            singleLine = true,
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_password_24),
                    contentDescription = null
                )
            },
            label = { Text(text = stringResource(id = R.string.password)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            // Hide the characters in the TextField
            visualTransformation = PasswordVisualTransformation()
        )
    }
}

// Create a group of buttons at the bottom of the screen.
// TODO: Implement LoginClick and AcctCreateClick later
// TODO: Create onLoginClick and onAcctCreateClick method
@Composable
private fun FieldOfButtons(
    onLoginClick: () -> Unit,
    onAcctCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(top = 40.dp, start = 24.dp, end = 24.dp, bottom = 24.dp)
    ) {
        // Login button
        ElevatedButton(
            onClick = onLoginClick,
            elevation = ButtonDefaults.elevatedButtonElevation(4.dp),
            modifier = Modifier
                .size(width = 160.dp, height = 48.dp)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = stringResource(id = R.string.login),
            )
        }

        // Create Account button
        ElevatedButton(
            onClick = onAcctCreateClick,
            elevation = ButtonDefaults.elevatedButtonElevation(4.dp),
            modifier = Modifier
                .size(width = 200.dp, height = 48.dp)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = stringResource(id = R.string.createAccount),
            )
        }
    }
    // Put the "Forgot Password?" button below the Login and Create Acct buttons.
    ElevatedButton(
        onClick = { /* Not being implemented */ },
        elevation = ButtonDefaults.elevatedButtonElevation(4.dp),
        modifier = Modifier
            .size(width = 200.dp, height = 48.dp)
    ) {
        Text(
            text = stringResource(id = R.string.forgotPassword),
        )
    }
}