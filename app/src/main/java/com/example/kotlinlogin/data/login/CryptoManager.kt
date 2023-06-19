package com.example.kotlinlogin.data.login

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class CryptoManager {
    private val encryptCipher = Cipher.getInstance(TRANSFORMATION).apply {
        init(Cipher.ENCRYPT_MODE, getKey())
    }

    // IV describes the initial state of the cipher
    private fun getDecryptionCipherForIv(iv: ByteArray): Cipher {
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, getKey(), IvParameterSpec(iv))
        }
    }

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

        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"

    }
}