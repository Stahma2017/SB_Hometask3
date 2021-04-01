package ru.skillbranch.skillarticles.ui.delegates

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ViewModelDelegate<T: ViewModel>(private val clazz : Class<T>, private val arg: Any?) : ReadOnlyProperty<FragmentActivity, T> {

    override fun getValue(thisRef: FragmentActivity, property: KProperty<*>): T {
        TODO("Not yet implemented")
    }
}