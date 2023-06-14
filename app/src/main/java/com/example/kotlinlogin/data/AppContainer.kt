package com.example.kotlinlogin.data

import android.content.Context
import com.example.kotlinlogin.data.login.LoginDatabase
import com.example.kotlinlogin.data.login.LoginsRepository
import com.example.kotlinlogin.data.login.OfflineLoginsRepository

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val loginsRepository: LoginsRepository
}

/**
 * Implement [AppContainer] to provide an instance of [OfflineLoginsRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    override val loginsRepository: LoginsRepository by lazy {
        OfflineLoginsRepository(LoginDatabase.getDatabase(context).loginDao())
    }

}