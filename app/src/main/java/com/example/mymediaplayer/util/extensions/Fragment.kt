package com.example.mymediaplayer.util.extensions

import android.app.AppOpsManager
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.AppOpsManagerCompat
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

fun Fragment.showToast(event: Int) {
    Toast.makeText(requireContext(), this.getString(event), Toast.LENGTH_SHORT).show()
}

fun Fragment.isSupportPipMod(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        requireContext().packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
    } else false
}

fun Fragment.isPipModeEnable(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)  {
        val uid = android.os.Process.myUid()
        val op = AppOpsManager.OPSTR_PICTURE_IN_PICTURE
        val packageName = requireContext().packageName

        when (AppOpsManagerCompat.noteOpNoThrow(requireContext(), op, uid, packageName)) {
            AppOpsManagerCompat.MODE_ALLOWED -> true
            else ->  false
        }
    } else false
}