package com.example.mymediaplayer.util.extensions

import android.app.AppOpsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
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

@RequiresApi(Build.VERSION_CODES.O)
fun Fragment.isPipModeEnable(): Boolean {
    val appOps = requireContext().getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val op = AppOpsManager.OPSTR_PICTURE_IN_PICTURE
    val uid = android.os.Process.myUid()
    val packageName = requireContext().packageName

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(op, uid, packageName) == AppOpsManager.MODE_ALLOWED
        } else {
            @Suppress("DEPRECATION")
            appOps.checkOpNoThrow(op, uid, packageName) == AppOpsManager.MODE_ALLOWED
        }
    } else false
}