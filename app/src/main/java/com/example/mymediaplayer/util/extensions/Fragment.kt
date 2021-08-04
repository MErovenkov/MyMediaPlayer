package com.example.mymediaplayer.util.extensions

import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment

fun Fragment.hideSystemBars() {
    WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
    WindowInsetsControllerCompat(requireActivity().window, requireActivity().window.decorView)
       .apply {
           systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
           hide(WindowInsetsCompat.Type.systemBars())
       }
}

fun Fragment.showSystemBars() {
    WindowCompat.setDecorFitsSystemWindows(requireActivity().window, true)
    WindowInsetsControllerCompat(requireActivity().window, requireActivity().window.decorView)
        .show(WindowInsetsCompat.Type.systemBars())
}