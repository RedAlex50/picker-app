package com.picker.app.infrastructure.crypto

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

data class JwtToken(val accessToken: String, val expiresAt: Long, val shiftId: String)

/**
 * Хранение JWT в EncryptedSharedPreferences, ключ AES-GCM в Android Keystore.
 * Счётчик неудачных попыток PIN — для блокировки после 5 подряд (Ф1).
 */
@Singleton
class SessionStore @Inject constructor(@ApplicationContext private val ctx: Context) {
    private val prefs by lazy {
        val masterKey = MasterKey.Builder(ctx)
            .setKeyGenParameterSpec(
                KeyGenParameterSpec.Builder("picker_master", KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(256)
                    .build()
            )
            .build()
        EncryptedSharedPreferences.create(
            ctx, "picker_session", masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    fun persist(token: JwtToken, pickerId: String) {
        prefs.edit()
            .putString(KEY_TOKEN, token.accessToken)
            .putLong(KEY_EXPIRES, token.expiresAt)
            .putString(KEY_SHIFT, token.shiftId)
            .putString(KEY_PICKER, pickerId)
            .apply()
    }

    fun activeToken(): String? = prefs.getString(KEY_TOKEN, null)?.takeIf {
        prefs.getLong(KEY_EXPIRES, 0L) > System.currentTimeMillis()
    }

    fun clear() = prefs.edit().clear().apply()

    fun failedAttempts(): Int = prefs.getInt(KEY_FAILS, 0)
    fun recordFailure() = prefs.edit().putInt(KEY_FAILS, failedAttempts() + 1).apply()
    fun resetFailures() = prefs.edit().putInt(KEY_FAILS, 0).apply()

    private companion object {
        const val KEY_TOKEN = "jwt"
        const val KEY_EXPIRES = "jwt_exp"
        const val KEY_SHIFT = "shift_id"
        const val KEY_PICKER = "picker_id"
        const val KEY_FAILS = "pin_fails"
    }
}
