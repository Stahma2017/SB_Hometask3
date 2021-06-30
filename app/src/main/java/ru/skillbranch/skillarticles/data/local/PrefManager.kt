package ru.skillbranch.skillarticles.data.local

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import ru.skillbranch.skillarticles.App
import ru.skillbranch.skillarticles.data.models.AppSettings

object PrefManager {
    internal val preferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(App.applicationContext())
    }

    fun clearAll() {
        preferences.edit().clear().apply()
    }

    fun updateAppSettings(appSettings: AppSettings) {
        preferences.edit().putBoolean(PrefKeys.DARK_MODE.name, appSettings.isDarkMode).apply()
        preferences.edit().putBoolean(PrefKeys.BIG_TEXT.name, appSettings.isBigText).apply()
    }

    fun getAppSettings(): LiveData<AppSettings> {
        return MutableLiveData(AppSettings(preferences.getBoolean(PrefKeys.DARK_MODE.name, false), preferences.getBoolean(PrefKeys.BIG_TEXT.name, false)))
    }

    fun isAuth(): MutableLiveData<Boolean> {
        return MutableLiveData(preferences.getBoolean(PrefKeys.DARK_MODE.name, false))
    }

    fun setAuth(auth: Boolean) {
        preferences.edit().putBoolean(PrefKeys.AUTH.name, auth).apply()
    }

    private enum class PrefKeys {
        DARK_MODE,
        BIG_TEXT,
        AUTH
    }
}