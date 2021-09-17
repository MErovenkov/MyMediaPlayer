package com.example.mymediaplayer.util.extensions

import android.view.View

fun View.dpToPx(dp: Float): Float = context.resources.displayMetrics.density * dp
fun View.spToPx(sp: Float): Float = context.resources.displayMetrics.scaledDensity * sp