package com.example.kotlinlogin.data.login

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class CryptoManager {
    companion object {
        // Initialize the keystore and then load it
        private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }
        /**
         * Get the secret key, or create a new key if none exists yet.
         */
        fun getKey(): SecretKey {
            val existingKey = keyStore.getEntry("secret", null) as? KeyStore.SecretKeyEntry
            return existingKey?.secretKey ?: createKey()
        }

        /**
         * Create a cryptographic key to store and use by the SQLCipher database
         */
        private fun createKey(): SecretKey {
            return KeyGenerator.getInstance(ALGORITHM). apply {
                init(
                    KeyGenParameterSpec.Builder(
                        "secret",
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    )
                        .setBlockModes(BLOCK_MODE)
                        .setEncryptionPaddings(PADDING)
                        .setUserAuthenticationRequired(false)
                        .setRandomizedEncryptionRequired(true)
                        .build()
                )
            }.generateKey()
        }

        /**
         * Read an encrypted file; used to get the key from the encrypted file
         * Redesigned from: https://developer.android.com/topic/security/data
         */
        fun readEncryptedFile(context: Context): ByteArray {
            // Key generation parameter specification
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
        fun writeEncryptedFile(context: Context, stringToWrite: SecretKey) {
            // Create a file with this name or replace an entire existing file
            // that has the same name. Note that you cannot append to an existing file,
            // and the filename cannot contain path separators.
            val fileToWrite = "encrypted.txt"
            // If the file to write to does not exist, create it.
            if (!File(context.filesDir, fileToWrite).exists()) {
                val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
                val mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)
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

        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7

        }
}