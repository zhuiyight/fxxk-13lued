package icu.fuck.blued.util

import android.view.View
import android.view.ViewGroup

object ViewUtil {
    fun enumerateChildViews(parent: ViewGroup): List<View> {
        val views = mutableListOf<View>()

        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)

            views.add(child)

            if (child is ViewGroup) {
                views.addAll(enumerateChildViews(child))
            }
        }

        return views
    }
}