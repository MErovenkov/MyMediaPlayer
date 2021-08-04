package com.example.mymediaplayer.ui.navigation

import android.net.Uri
import androidx.navigation.NavController
import com.example.mymediaplayer.R
import com.example.mymediaplayer.ui.fragment.ExoPlayerFragment

class Navigation(private var navController: NavController): IMediaNavigation {
    override fun openMedia(title: String, uri: Uri) {
        navController.popBackStack(R.id.MediaFragment, false)
        navController.navigate(R.id.ExoPlayerFragment, ExoPlayerFragment.getNewBundle(title, uri))
    }
}