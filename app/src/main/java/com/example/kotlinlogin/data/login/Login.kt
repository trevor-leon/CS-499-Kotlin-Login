package com.example.kotlinlogin.data.login

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity data class to represent a row of the Login database
 * Set the id and username to be unique
 */
// TODO: Do more research and probably make id and username PK's.
@Entity(indices = [Index(value = ["username"], unique = true)])
data class Login(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val password: String
)