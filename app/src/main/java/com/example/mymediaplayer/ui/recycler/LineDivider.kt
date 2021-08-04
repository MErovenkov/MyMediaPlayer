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

    override fun onDrawOver(convas: Canvas, parent: RecyclerView) {
        val left = parent.paddingLeft + divider.opticalInsets.left
        val right = parent.width - parent.paddingRight - divider.opticalInsets.right

        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val bottom = top + divider.intrinsicHeight

            divider.apply {
                bounds = Rect(left, top, right, bottom)
                draw(convas)
            }
        }
    }
}