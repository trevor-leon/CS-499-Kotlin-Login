package com.example.kotlinlogin.ui.login

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.kotlinlogin.data.login.Login
import com.example.kotlinlogin.data.login.LoginsRepository
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * ViewModel uses the UI State, repositories, and generally represents the functions of the app
 */
class LoginViewModel (private val loginsRepository: LoginsRepository) : ViewModel() {

    // Hold the current Login UI state
    var loginUiState by mutableStateOf(LoginUiState())
        private set

    /**
     * Update the private set loginUiState to the passed loginDetails
     */
    fun updateUiState(loginDetails: LoginDetails) {
        loginUiState = LoginUiState(
            loginDetails = loginDetails
        )
    }

    /**
     * Validate and save the current loginUiState details if the username and password
     */
    suspend fun saveLogin() {
        if (validateUsername() && validatePassword()) {
            // TODO: implement ENCRYPTION and store securely
            loginsRepository.insertLogin(loginUiState.loginDetails.toLogin())
        }
    }

    /**
     * Get the login from the database according to the username and password, decrypt it,
     * and verify that it matches the UI state
     */
    suspend fun findLogin() {
        if (validateUsername() && validatePassword()) {
            // TODO: implement ENCRYPTION and store securely
            loginsRepository.insertLogin(loginUiState.loginDetails.toLogin())
        }
    }

    /**
     * Validate the username by checking for an email pattern
     */
    private fun validateUsername(uiState: LoginDetails = loginUiState.loginDetails): Boolean {
        return with(uiState) {
            // This represents a pattern for an email address
            val emailPattern = Patterns.EMAIL_ADDRESS
            // Check if the provided username matches a valid email pattern
            val matcher: Matcher = emailPattern.matcher(uiState.username.trim())

            matcher.matches()
                    && username.trim().isNotBlank()
        }
    }

    private fun validatePassword(uiState: LoginDetails = loginUiState.loginDetails): Boolean {
        return with(uiState) {
            val pattern: Pattern

            /**
             * Password checking code derived from the solution here:
             * https://stackoverflow.com/questions/23214434/regular-expression-in-android-for-password-field
             * Check if the password contains a number, lowercase and uppercase letters, a symbol,
             * and has at least ten characters
             */
            val passwordPattern =
                "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{10,}$"

            pattern = Pattern.compile(passwordPattern)
            val matcher: Matcher = pattern.matcher(uiState.password.trim())

            matcher.matches()
                    && password.trim().isNotBlank()
        }
    }

}

/**
 * data class representation of the current UI State.
 * TODO: Changed Valid Booleans to be initialized as null 6/7. Double check later
 */
data class LoginUiState(
    val loginDetails: LoginDetails = LoginDetails(),
    val isUsernameValid: Boolean? = null,
    val isPasswordValid: Boolean? = null
)

data class LoginDetails(
    val id: Int = 0,
    val username: String = "",
    val password: String = ""
)

fun LoginDetails.toLogin(): Login = Login(
    id = id,
    username = username,
    password = password
)

// TODO: I added '= null' to is__Valid for initialization on 6/7. Remove later?
fun Login.toLoginUiState(
    isUsernameValid: Boolean? = null,
    isPasswordValid: Boolean? = null
):LoginUiState = LoginUiState(
        loginDetails = this.toLoginDetails(),
        isUsernameValid = isUsernameValid,
        isPasswordValid = isPasswordValid
    )

fun Login.toLoginDetails(): LoginDetails = LoginDetails(
        id = id,
        username = username,
        password = password
    )
