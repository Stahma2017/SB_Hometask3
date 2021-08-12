package ru.skillbranch.skillarticles.data.delegates

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.squareup.moshi.JsonAdapter
import ru.skillbranch.skillarticles.data.models.User
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class PrefLiveObjDelegate<T>(
    private val fieldKey: String,
    private val adapter: JsonAdapter<T>,
    private val preferences: SharedPreferences
) : ReadOnlyProperty<Any?, LiveData<T>> {
    private var storedValue: LiveData<T>? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): LiveData<T> {
        if (storedValue == null) {
            storedValue = SharedPreferenceObjLiveData(preferences, fieldKey, adapter)
        }
        return storedValue!!
    }
}

internal class SharedPreferenceObjLiveData<T>(
    var sharedPrefs: SharedPreferences,
    var key: String,
    var adapter: JsonAdapter<T>
) : LiveData<T>() {
    private val preferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, shKey ->
            if (shKey == key) {
                value = readObj(adapter)
            }
        }

    override fun onActive() {
        super.onActive()
        value = readObj(adapter)
        sharedPrefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    override fun onInactive() {
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
        super.onInactive()
    }

    private fun readObj(adapter: JsonAdapter<T>): T {
        return  sharedPrefs.getString(key, null)?.let { adapter.fromJson(it) } as T
    }

}