package com.example.aqarhub.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.russhwolf.settings.SharedPreferencesSettings

object AndroidAuthPreferencesFactory {
    fun create(context: Context): AuthPreferences {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val encryptedPrefs = EncryptedSharedPreferences.create(
            "aqar_hub_secure_prefs",
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        val settings = SharedPreferencesSettings(encryptedPrefs)
        return AuthPreferencesImpl(settings)
    }
}
