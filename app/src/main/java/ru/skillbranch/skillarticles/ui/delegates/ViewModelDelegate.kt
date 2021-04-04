package ru.skillbranch.skillarticles.ui.delegates

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.skillbranch.skillarticles.viewmodels.ArticleViewModel
import ru.skillbranch.skillarticles.viewmodels.base.ViewModelFactory
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ViewModelDelegate<T: ViewModel>(private val clazz : Class<T>, private val arg: Any?) : ReadOnlyProperty<FragmentActivity, T> {

    private lateinit var value : T

    override fun getValue(thisRef: FragmentActivity, property: KProperty<*>): T {
        if (!::value.isInitialized) {
            value = when (arg) {
                null -> ViewModelProvider(thisRef).get(clazz)
                else -> ViewModelProvider(thisRef, ViewModelFactory(arg)).get(clazz)
            }
        }
        return value
    }
}