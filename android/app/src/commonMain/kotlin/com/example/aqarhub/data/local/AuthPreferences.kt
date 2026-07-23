package com.example.aqarhub.data.local

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set

interface AuthPreferences {
    fun saveToken(token: String)
    fun getToken(): String?
    fun clearToken()
}

class AuthPreferencesImpl(
    private val settings: Settings = Settings()
) : AuthPreferences {

    override fun saveToken(token: String) {
        settings[KEY_ACCESS_TOKEN] = token
    }

    override fun getToken(): String? {
        return settings.getStringOrNull(KEY_ACCESS_TOKEN)
    }

    override fun clearToken() {
        settings.remove(KEY_ACCESS_TOKEN)
    }

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
    }
}
