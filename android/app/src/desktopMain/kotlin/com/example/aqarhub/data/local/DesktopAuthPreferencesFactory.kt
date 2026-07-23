package com.example.aqarhub.data.local

import com.russhwolf.settings.PreferencesSettings
import java.util.prefs.Preferences

object DesktopAuthPreferencesFactory {
    fun create(): AuthPreferences {
        val userPrefs = Preferences.userRoot().node("aqar_hub_prefs")
        val settings = PreferencesSettings(userPrefs)
        return AuthPreferencesImpl(settings)
    }
}
