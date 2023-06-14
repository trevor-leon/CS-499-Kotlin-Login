package com.example.kotlinlogin.data.login

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Specify the database with the Login class as the entity; without keeping a version backup history
 */
@Database(entities = [Login::class], version = 1, exportSchema = false)
abstract class LoginDatabase : RoomDatabase() {
    abstract fun loginDao(): LoginDao
    // Companion object allows access to database methods
    companion object {
        /**
         * Set up an instance of LoginDatabase; annotated Volatile to ensure all reads and writes
         * come from the main memory and is always up-to-date
         */
        @Volatile
        private var Instance: LoginDatabase? = null

        /**
         * Return the instance of the database; otherwise, build the database with the
         * synchronized block to ensure only one instance of the database can be created.
         */
        fun getDatabase(context: Context): LoginDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context = context,
                    klass = LoginDatabase::class.java,
                    name = "login_database"
                )
                    // Simply destroy and recreate to migrate later if needed.
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}