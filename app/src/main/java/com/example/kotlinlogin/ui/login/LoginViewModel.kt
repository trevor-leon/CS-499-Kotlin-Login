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
    suspend fun saveLogin(uiState: LoginDetails = loginUiState.loginDetails): Boolean {
        // Validate the login before checking it
        return if (validateLogin()) {
            // If the username was not found in the database, store the login, and return true
            if(!findUsername()) {
                loginsRepository.insertLogin(uiState.toLogin())
                true
            } else {
                false
            }
        } else {
            false
        }
    }

    /**
     * Get the login from the database according to the username and password
     * and verify that it matches the UI state
     */
    suspend fun findLogin(uiState: LoginDetails = loginUiState.loginDetails): Boolean {
        // First, validate the login as an invalid login should never be entered.
        // If the login exists, return true. Else, return false.
            return loginsRepository.loginExists(
                uiState.username,
                uiState.password
            )
    }

    /**
     * Determine if a username is already stored in the database
     */
    private suspend fun findUsername(username: String = loginUiState.loginDetails.username): Boolean {
        return loginsRepository.usernameExists(username)
    }

    /**
     * Validate the login by checking for an email pattern in the username, and then checking
     * the password for the password pattern
     */
    private fun validateLogin(uiState: LoginDetails = loginUiState.loginDetails): Boolean {
        return with(uiState) {
            // This represents a pattern for an email address
            val emailPattern = Patterns.EMAIL_ADDRESS
            // Check if the provided username matches a valid email pattern
            val matcher: Matcher = emailPattern.matcher(uiState.username.trim())

            // If the username matches the username pattern, return true
            if(matcher.matches() && username.trim().isNotBlank()) {
                validatePassword()
                true
            } else {
                // Else, update the UI state with the username reset to "" and return false
                updateUiState(LoginDetails(password = uiState.password))
                validatePassword()
                false
            }
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
                "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()+=]).{10,}$"

            pattern = Pattern.compile(passwordPattern)
            val matcher: Matcher = pattern.matcher(uiState.password.trim())

            // If the username matches the username pattern, return true
            if(matcher.matches() && password.trim().isNotBlank()) {
                true
            } else {
                // Else, update the UI state with the password reset to "" and return false
                updateUiState(LoginDetails(username = uiState.username))
                false
            }
        }
    }

}

/**
 * Data class representation of the current UI State.
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
