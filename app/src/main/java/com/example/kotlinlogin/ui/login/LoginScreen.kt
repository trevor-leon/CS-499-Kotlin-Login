package com.example.kotlinlogin.ui.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
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
import com.example.kotlinlogin.LoginAppBar
import com.example.kotlinlogin.R
import com.example.kotlinlogin.ui.AppViewModelProvider
import com.example.kotlinlogin.ui.navigation.NavigationDestination
import com.example.kotlinlogin.ui.theme.brandColor
import kotlinx.coroutines.launch

/**
 * Define the [LoginDestination] specifying the [route] and [titleRes]
 */
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

    Scaffold(
        topBar = {
            LoginAppBar (
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
                    // if findLogin() finds the login, make a Toast stating so
                    if(loginViewModel.findLogin()) {
                        // Insert navigation function here to go to the next screen *Not implemented
                        Toast.makeText(context, "Login found!", Toast.LENGTH_SHORT).show()
                        navigateToNextScreen()
                    } else {
                        Toast.makeText(context, "Login not found!", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onAcctCreateClick = {
                coroutineScope.launch {
                    // If the login is not found in the database, save the login to the database
                    if(loginViewModel.saveLogin()) {
                        // Insert navigation function here to go to the next screen *Not implemented
                        Toast.makeText(context, "Login stored!", Toast.LENGTH_SHORT).show()
                        navigateToNextScreen()
                    } else {
                        Toast.makeText(context, "Account creation failed.", Toast.LENGTH_SHORT).show()
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
            loginUiState = loginUiState,
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
    loginUiState: LoginUiState,
    modifier: Modifier = Modifier,
    onValuesChange: (LoginDetails) -> Unit = {}
) {
    Column(
        modifier = modifier
    ) {
        // Email
        TextField(
            value = loginUiState.loginDetails.username,
            // Pass a copy of loginDetails with username set to the current username text
            onValueChange = { onValuesChange(loginUiState.loginDetails.copy(username = it)) },
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
            isError = loginUiState.isUsernameInvalid,
            supportingText = {
                if (loginUiState.isUsernameInvalid) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Input username should follow an email pattern.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            trailingIcon = {
                if (loginUiState.isUsernameInvalid)
                    Icon(Icons.Filled.Warning,"error", tint = MaterialTheme.colorScheme.error)
            },
            modifier = Modifier
                .padding(bottom = 8.dp)
        )

        // Password
        TextField(
            value = loginUiState.loginDetails.password,
            // Pass a copy of loginDetails with password set to the current password text
            onValueChange = { onValuesChange(loginUiState.loginDetails.copy(password = it)) },
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
            visualTransformation = PasswordVisualTransformation(),
            isError = loginUiState.isPasswordInvalid,
            supportingText = {
                if (loginUiState.isUsernameInvalid) {
                    Text(
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(end = 24.dp),
                        text = stringResource(R.string.password_should_contain),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            trailingIcon = {
                if (loginUiState.isUsernameInvalid)
                    Icon(
                        Icons.Filled.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
            },
        )
    }
}

// Create a group of buttons at the bottom of the screen.
@Composable
private fun FieldOfButtons(
    onLoginClick: () -> Unit,
    onAcctCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(top = 20.dp, start = 24.dp, end = 24.dp, bottom = 24.dp)
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