package com.example.mymediaplayer.ui.navigation

import androidx.navigation.NavController
import com.example.mymediaplayer.R
import com.example.mymediaplayer.ui.fragment.ExoPlayerFragment
import com.google.android.exoplayer2.MediaItem.PlaybackProperties

class Navigation(private var navController: NavController): IMediaNavigation {
    override fun openMedia(title: String, playbackProperties: PlaybackProperties) {
        navController.popBackStack(R.id.MediaFragment, false)

        navController.navigate(R.id.ExoPlayerFragment,
            ExoPlayerFragment.getNewBundle(title, playbackProperties))
    }
}