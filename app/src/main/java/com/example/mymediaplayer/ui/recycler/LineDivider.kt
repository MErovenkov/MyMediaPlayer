package com.example.mymediaplayer.ui.recycler

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class LineDivider(context: Context, @DrawableRes dividerRes: Int): RecyclerView.ItemDecoration() {
    private val divider: Drawable = ContextCompat.getDrawable(context, dividerRes)!!
    private val dividerRect = Rect()

    override fun onDrawOver(convas: Canvas, parent: RecyclerView) {
        dividerRect.apply {
            left = parent.paddingLeft
            right = parent.width - parent.paddingRight
        }

        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams

            dividerRect.apply {
                top = child.bottom + params.bottomMargin
                bottom = dividerRect.top + divider.intrinsicHeight
            }

            divider.apply {
                bounds = dividerRect
                draw(convas)
            }
        }
    }
}