package com.example.mymediaplayer.util.extensions

import android.content.Context

fun Context.getScreenOrientation(): Int {
    return resources.configuration.orientation
}