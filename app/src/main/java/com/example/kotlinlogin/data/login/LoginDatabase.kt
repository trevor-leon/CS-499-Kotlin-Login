package com.example.kotlinlogin.data.login

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kotlinlogin.data.login.CryptoManager.Companion.readEncryptedFile
import com.example.kotlinlogin.data.login.CryptoManager.Companion.writeEncryptedFile
import net.sqlcipher.database.SupportFactory

/**
 * Specify the database with the Login class as the entity; without keeping a version backup history
 */
@Database(entities = [Login::class], version = 3, exportSchema = false)
abstract class LoginDatabase : RoomDatabase() {
    // Initialize the LoginDao()
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
            // Initialize/write a generated encrypted key to the encrypted file
            writeEncryptedFile(context, CryptoManager.getKey())
            val passphrase = readEncryptedFile(context)
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context = context,
                    klass = LoginDatabase::class.java,
                    name = "login_database"
                )
                    // Implement SQLCipher to encrypt the database
                    .openHelperFactory(SupportFactory(passphrase))
                    // Simply destroy and recreate to migrate later if needed.
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}