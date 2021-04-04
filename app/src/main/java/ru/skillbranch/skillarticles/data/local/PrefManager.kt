package ru.skillbranch.skillarticles.data.local

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import ru.skillbranch.skillarticles.ui.delegates.PrefDelegate

class PrefManager(context: Context) {
        val preferences : SharedPreferences by lazy { PreferenceManager(context).sharedPreferences }

        fun clearAll() { preferences.edit().clear().apply() }

        var storedBoolean by PrefDelegate(false)
        var storedString by PrefDelegate("test")
        var storedInt by PrefDelegate(Int.MAX_VALUE)
        var storedLong by PrefDelegate(Long.MAX_VALUE)
        var storedFloat by PrefDelegate(100f)

}