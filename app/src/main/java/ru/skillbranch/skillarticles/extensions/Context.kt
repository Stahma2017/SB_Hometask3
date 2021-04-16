package ru.skillbranch.skillarticles.extensions

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue

fun Context.dpToIntPx(dp : Int) : Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        this.resources.displayMetrics).toInt()
}

fun Context.dpToPx(dp: Int): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        this.resources.displayMetrics
    )
}

fun Context.attrValue(res: Int): Int {
    val value: Int
    val tv = TypedValue()
    if (this.theme.resolveAttribute(res, tv, true)) value = tv.data
    else throw Resources.NotFoundException("Resource with id $res not found")
    return value
}