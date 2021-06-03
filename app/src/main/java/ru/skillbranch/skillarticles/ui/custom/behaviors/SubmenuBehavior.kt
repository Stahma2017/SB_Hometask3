package ru.skillbranch.skillarticles.ui.custom.behaviors

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import kotlin.math.max
import kotlin.math.min

class SubmenuBehavior<V : View>() : CoordinatorLayout.Behavior<V>() {

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: V, directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: V, target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
        val bottomBarHeight = coordinatorLayout.getChildAt(3).height
        val coefficient = child.width.toFloat() / bottomBarHeight
        child.translationX = max(0f, min(child.width.toFloat(), child.translationX + (dy.toFloat() * coefficient)))
    }
}