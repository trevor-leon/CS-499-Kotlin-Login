package com.example.kotlinlogin.data.login

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity data class to represent a row of the Login database
 */
@Entity(tableName = "Login")
data class Login(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val password: String
)