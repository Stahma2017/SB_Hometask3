package ru.skillbranch.skillarticles.ui.custom.markdown

import android.content.Context
import androidx.core.graphics.ColorUtils
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.extensions.attrValue
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.extensions.dpToPx

class SearchBgHelper(
    context: Context,
    private val focusListener: (Int) -> Unit
) {
    private val padding: Int = context.dpToIntPx(4)
    private val borderWidth: Int = context.dpToIntPx(1)
    private val radius: Float = context.dpToPx(8)


    private val secondaryColor: Int = context.attrValue(R.attr.colorSecondary)
    private val alphaColor : Int = ColorUtils.setAlphaComponent(secondaryColor, 160)


}