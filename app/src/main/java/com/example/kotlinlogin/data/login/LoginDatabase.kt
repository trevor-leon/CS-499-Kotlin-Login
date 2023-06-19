package com.example.kotlinlogin.data.login

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import net.sqlcipher.database.SupportFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.charset.StandardCharsets
import javax.crypto.SecretKey

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
            // Initialize/write to the encrypted file a generated encrypted key
            // TODO: fix writeEncryptedFile crash caused by an already-existing file.
            //writeEncryptedFile(context, CryptoManager.getKey())
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

        /**
         * Read an encrypted file; used to get the key from the encrypted file
         * Redesigned from: https://developer.android.com/topic/security/data
         */
        private fun readEncryptedFile(context: Context): ByteArray {
            // Although you can define your own key generation parameter specification, it's
            // recommended that you use the value specified here.
            val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
            val mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

            val fileToRead = "encrypted.txt"
            val encryptedFile = EncryptedFile.Builder(
                File(context.filesDir, fileToRead),
                context,
                mainKeyAlias,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()

            val inputStream = encryptedFile.openFileInput()
            val byteArrayOutputStream = ByteArrayOutputStream()
            var nextByte: Int = inputStream.read()
            while (nextByte != -1) {
                byteArrayOutputStream.write(nextByte)
                nextByte = inputStream.read()
            }

            return byteArrayOutputStream.toByteArray()

        }

        /**
         * Write to an encrypted file; used to store the key
         * Redesigned from: https://developer.android.com/topic/security/data
         * @param stringToWrite the string to write to the encrypted file
         */
        private fun writeEncryptedFile(context: Context, stringToWrite: SecretKey) {
            val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
            val mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

            // Create a file with this name or replace an entire existing file
            // that has the same name. Note that you cannot append to an existing file,
            // and the filename cannot contain path separators.
            val fileToWrite = "encrypted.txt"
            val encryptedFile = EncryptedFile.Builder(
                File(context.filesDir, fileToWrite),
                context,
                mainKeyAlias,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()

            val fileContent = stringToWrite
                .toString()
                .toByteArray(StandardCharsets.UTF_8)
            encryptedFile.openFileOutput().apply {
                write(fileContent)
                flush()
                close()
            }

        }
    }
}