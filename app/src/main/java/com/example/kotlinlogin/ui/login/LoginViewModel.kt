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
    fun updateUiState(
        loginDetails: LoginDetails = loginUiState.loginDetails,
        isUsernameInvalid: Boolean = false,
        isPasswordInvalid: Boolean = false
    ) {
        loginUiState = LoginUiState(
            loginDetails = loginDetails,
            isUsernameInvalid = isUsernameInvalid,
            isPasswordInvalid = isPasswordInvalid
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
        return if (validateLogin()) {
            loginsRepository.loginExists(
                uiState.username,
                uiState.password
            )
        } else {
            false
        }
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

            /**
             * If the username matches the username pattern,
             * ensure the UiState's username is considered to be valid.
             */
            if(matcher.matches() && username.trim().isNotBlank()) {
                updateUiState(
                    LoginDetails(
                        username = username,
                        password = password),
                    isUsernameInvalid = false
                )
                validatePassword()
                true
            } else {
                /**
                 * Update the UI state setting the [LoginDetails]' username to "" and password to
                 * the current uiState password. Also, set isUsernameValid to false.
                 */
                updateUiState(
                    LoginDetails(
                        username = "",
                        password = uiState.password
                    ),
                    isUsernameInvalid = true
                )
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

            /**
             * If the password matches the password pattern, set isUsernameInvalid to the current
             * state, and ensure the UiState's password is considered to be valid.
             */
            if(matcher.matches() && password.trim().isNotBlank()) {
                updateUiState(
                    LoginDetails(password = password),
                    isUsernameInvalid = loginUiState.isUsernameInvalid,
                    isPasswordInvalid = false
                )
                true
            } else {
                /**
                 * Update the UI state setting the [LoginDetails]' password to "" and username to
                 * the current uiState password. Also, set isUsernameValid to false.
                 */
                updateUiState(
                    LoginDetails(
                        username = uiState.username,
                        password = ""
                    ),
                    isUsernameInvalid = loginUiState.isUsernameInvalid,
                    isPasswordInvalid = true
                )
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
    val isUsernameInvalid: Boolean = false,
    val isPasswordInvalid: Boolean = false
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
    isUsernameInvalid: Boolean = false,
    isPasswordInvalid: Boolean = false
):LoginUiState = LoginUiState(
        loginDetails = this.toLoginDetails(),
        isUsernameInvalid = isUsernameInvalid,
        isPasswordInvalid = isPasswordInvalid
    )

fun Login.toLoginDetails(): LoginDetails = LoginDetails(
        id = id,
        username = username,
        password = password
    )
