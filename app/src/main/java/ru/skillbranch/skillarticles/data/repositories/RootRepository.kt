package ru.skillbranch.skillarticles.data.repositories

import androidx.lifecycle.LiveData
import ru.skillbranch.skillarticles.data.local.PrefManager

object RootRepository {

    val prefs = PrefManager
    fun isAuth() : LiveData<Boolean> = prefs.isAuthLive
    fun setAuth(auth:Boolean) {
        prefs.isAuth = auth
    }
}