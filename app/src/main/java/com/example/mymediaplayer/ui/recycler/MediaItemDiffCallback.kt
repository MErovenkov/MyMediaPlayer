package com.example.mymediaplayer.ui.recycler

import androidx.recyclerview.widget.DiffUtil
import com.google.android.exoplayer2.MediaItem

class MediaItemDiffCallback: DiffUtil.ItemCallback<MediaItem>() {

    override fun areItemsTheSame(oldItem: MediaItem, newItem: MediaItem): Boolean =
        oldItem.playbackProperties?.uri == newItem.playbackProperties?.uri

    override fun areContentsTheSame(oldItem: MediaItem, newItem: MediaItem): Boolean =
        oldItem == newItem
}