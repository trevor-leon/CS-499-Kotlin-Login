package com.example.kotlinlogin.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.kotlinlogin.LoginApplication
import com.example.kotlinlogin.ui.login.LoginViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initialize the LoginViewModel
        initializer {
            LoginViewModel(loginApplication().container.loginsRepository)
        }
    }
}

/**
 * Extension function
 */
fun CreationExtras.loginApplication(): LoginApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as LoginApplication)