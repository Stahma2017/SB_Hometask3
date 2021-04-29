package ru.skillbranch.skillarticles.ui.custom.markdown

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.extensions.setPaddingOptionally
import kotlin.properties.Delegates

class MarkdownContentView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    diffStyleAttr: Int = 0
): ViewGroup(context, attrs, diffStyleAttr) {
    lateinit var elements: List<MarkdownElement>
    private val children : MutableList<View> = mutableListOf()

    //for restore
    private var ids = arrayListOf<Int>()

    var textSize = Delegates.observable(14f) { _, old, value ->
        if (value == old) return@observable
        children.forEach {
            it as IMarkdownView
            it.fontSize = value
        }
    }
    var isLoading: Boolean = true

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var usedHieght = paddingTop
        val width = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)

        children.forEach {
            measureChild(it, widthMeasureSpec, heightMeasureSpec)
            usedHieght += it.measuredHeight
        }

        usedHieght += paddingBottom
        setMeasuredDimension(width, usedHieght)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var usedHeight = paddingTop
        val bodyWidth = right - left - paddingLeft - paddingRight
        val left = paddingLeft
        val right = paddingLeft + bodyWidth

        children.forEach {
            if (it is MarkdownTextView) {
                it.layout(left - paddingLeft / 2,
                usedHeight,
                right - paddingRight/ 2,
                usedHeight + it.measuredHeight)
            } else {
                it.layout(
                    left,
                    usedHeight,
                    right,
                    usedHeight + it.measuredHeight
                )
            }
            usedHeight += it.measuredHeight
        }
    }

    fun setContent(content: List<MarkdownElement>) {
        elements = content
        content.forEach {
            when(it) {
                is MarkdownElement.Text -> {
                    val tv = MarkdownTextView(context, /*textSize*/ 14f).apply {
                        setPaddingOptionally(left = context.dpToIntPx(8), right = context.dpToIntPx(8))
                        setLineSpacing(fontSize*0.5f, 1f)
                    }

                    MarkdownBuilder(context)
                        .markdownToSpan(it)
                        .run { tv.setText(this, TextView.BufferType.SPANNABLE) }

                    addView(tv)
                }

                is MarkdownElement.Image -> {
                    val iv = MarkdownImageView(context, /*textSize*/ 14f , it.image.url, it.image.text, it.image.alt)
                    addView(iv)
                }

                is MarkdownElement.Scroll -> {
                    //TODO implement me
                }
            }
        }
    }

    fun renderSearchResult(searchResults: List<Pair<Int, Int>>) {
        //TODO implement me
    }
}