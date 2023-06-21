package com.example.kotlinlogin.data.login

import kotlinx.coroutines.flow.Flow

/**
 * Offline repository representation of a [LoginsRepository] that uses the [LoginDao] to access logins
 */
class OfflineLoginsRepository(private val loginDao: LoginDao) : LoginsRepository {
    override fun getAllLoginsStream(): Flow<List<Login>> = loginDao.getAllLogins()

    // Determine whether or not the username and password combination exists in the database.
    override suspend fun loginExists(username: String, password: String): Boolean =
        loginDao.loginExists(username, password)

    // Determine whether or not the username exists in the database.
    override suspend fun usernameExists(username: String): Boolean =
        loginDao.usernameExists(username)

    override suspend fun insertLogin(login: Login) = loginDao.insert(login)

    override suspend fun deleteLogin(login: Login) = loginDao.delete(login)

    override suspend fun updateLogin(login: Login) = loginDao.update(login)

}