package com.example.kotlinlogin

import android.app.Application
import com.example.kotlinlogin.data.AppContainer
import com.example.kotlinlogin.data.AppDataContainer

class LoginApplication : Application() {
    /**
     * Instance of AppContainer (in data) to be used by the app's classes
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}