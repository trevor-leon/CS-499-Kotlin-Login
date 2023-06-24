package com.example.kotlinlogin.data.login

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

/**
 * Define the Data Access Object (DAO) for the Login DB
 */
@Dao
interface LoginDao {

    // Specify the OnConflictStrategy to ignore conflicting inserts into the Login DB
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(login: Login)

    @Update
    suspend fun update(login: Login)

    @Delete
    suspend fun delete(login: Login)

    // Determine if a login exists in the database; used for validating logins
    @Query("SELECT EXISTS(SELECT * FROM Login WHERE username = :username AND password = :password)")
    suspend fun loginValid(username: String, password: String): Boolean

    // Determine if a username already exists in the database; used to confirm storage
    @Query("SELECT EXISTS(SELECT * FROM Login WHERE username = :username)")
    suspend fun usernameExists(username: String): Boolean
}