package ru.skillbranch.skillarticles.extensions

import android.text.Layout

fun Layout.getLineHeight(line: Int): Int {
    return getLineTop(line.inc()) - getLineTop(line)
}

fun Layout.getLineTopWithoutPadding(line: Int) : Int {
    var lineTop = getLineTop(line)
    if (lineTop == 0) {
        lineTop -= topPadding
    }
    return lineTop
}

fun Layout.getLineBottomWithoutPadding(line: Int) : Int {
    var lineBottom = getLingBottomWithoutSpacing(line)
    if (line == lineCount.dec()) {
        lineBottom -= bottomPadding
    }
    return lineBottom
}

fun Layout.getLingBottomWithoutSpacing(line: Int): Int {
    val lineBottom = getLineBottom(line)
    val isLastLine = line == lineCount.dec()
    val hasLineSpacing = spacingAdd != 0f

    return if (!hasLineSpacing || isLastLine) {
        lineBottom + spacingAdd.toInt()
    } else {
        lineBottom - spacingAdd.toInt()
    }
}