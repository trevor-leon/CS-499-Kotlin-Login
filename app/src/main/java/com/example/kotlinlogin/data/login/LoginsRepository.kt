package com.example.kotlinlogin.data.login

import kotlinx.coroutines.flow.Flow


/**
 * Repository to read, insert, update, and delete [Login] from a given data source.
 */
interface LoginsRepository {

    // Retrieve all logins of the given data source
    fun getAllLoginsStream(): Flow<List<Login>>

    // Retrieve a login from the given data source that matches id
    fun getLoginStream(id: Int): Flow<Login?>

    // Insert a login into the data source
    suspend fun insertLogin(login: Login)

    // Delete a login in the data source
    suspend fun deleteLogin(login: Login)

    // Update a login in the data source
    suspend fun updateLogin(login: Login)

}