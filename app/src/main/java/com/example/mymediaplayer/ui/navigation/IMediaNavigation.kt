package com.example.mymediaplayer.ui.navigation

import android.net.Uri

interface IMediaNavigation {
    fun openMedia(title: String, uri: Uri)
}