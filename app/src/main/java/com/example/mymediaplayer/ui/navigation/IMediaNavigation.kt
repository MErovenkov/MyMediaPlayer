package com.example.mymediaplayer.ui.navigation

import com.google.android.exoplayer2.MediaItem.PlaybackProperties

interface IMediaNavigation {
    fun openMedia(title: String, playbackProperties: PlaybackProperties)
}