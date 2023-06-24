package com.example.kotlinlogin.data.login

/**
 * Repository to read, insert, update, and delete [Login] from a given data source.
 */
interface LoginsRepository {
    // Retrieve a login from the given data source that matches the username and password
    suspend fun loginExists(username: String, password: String): Boolean

    // Determine if a username already exists within the database
    suspend fun usernameExists(username: String): Boolean

    // Insert a login into the data source
    suspend fun insertLogin(login: Login)

    // Delete a login in the data source
    suspend fun deleteLogin(login: Login)

    // Update a login in the data source
    suspend fun updateLogin(login: Login)

}