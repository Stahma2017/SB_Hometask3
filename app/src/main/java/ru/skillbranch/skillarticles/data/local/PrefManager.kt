package ru.skillbranch.skillarticles.data.local

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.preference.PreferenceManager
import ru.skillbranch.skillarticles.App
import ru.skillbranch.skillarticles.data.delegates.PrefDelegate
import ru.skillbranch.skillarticles.data.delegates.PrefLiveDelegate
import ru.skillbranch.skillarticles.data.models.AppSettings

object PrefManager {
    internal val preferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(App.applicationContext())
    }

    var isAuth by PrefDelegate(false)
    var isDarkMode by PrefDelegate(false)
    var isBigText by PrefDelegate(false)

    val isAuthLive: LiveData<Boolean> by PrefDelegate("isAuth", false, preferences)

    val appSettings = MediatorLiveData<AppSettings>().apply {
        val isDarkModeLive: LiveData<Boolean> by PrefLiveDelegate("isDarkMode", false, preferences)
        val isBigTextLive: LiveData<Boolean> by PrefLiveDelegate("isBigText", false, preferences)
        value = AppSettings()

        addSource(isDarkModeLive) {
            value = value!!.copy(isDarkMode = it)
        }

        addSource(isBigTextLive) {
            value = value!!.copy(isBigText = it)
        }
    }.distinctUntilChanged()

    fun isAuth(): MutableLiveData<Boolean> {
        return MutableLiveData(preferences.getBoolean(PrefKeys.DARK_MODE.name, false))
    }

    fun clearAll() {
        preferences.edit().clear().apply()
    }

    /*fun updateAppSettings(appSettings: AppSettings) {
        preferences.edit().putBoolean(PrefKeys.DARK_MODE.name, appSettings.isDarkMode).apply()
        preferences.edit().putBoolean(PrefKeys.BIG_TEXT.name, appSettings.isBigText).apply()
    }

    fun getAppSettings(): LiveData<AppSettings> {
        return MutableLiveData(AppSettings(preferences.getBoolean(PrefKeys.DARK_MODE.name, false), preferences.getBoolean(PrefKeys.BIG_TEXT.name, false)))
    }



    fun setAuth(auth: Boolean) {
        preferences.edit().putBoolean(PrefKeys.AUTH.name, auth).apply()
    }

    private enum class PrefKeys {
        DARK_MODE,
        BIG_TEXT,
        AUTH
    }*/
}