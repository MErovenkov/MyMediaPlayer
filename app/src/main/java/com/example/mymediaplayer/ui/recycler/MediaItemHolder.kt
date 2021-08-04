package com.example.mymediaplayer.ui.recycler

import androidx.recyclerview.widget.RecyclerView
import com.example.mymediaplayer.databinding.RecyclerMediaItemBinding
import com.google.android.exoplayer2.MediaItem

class MediaItemHolder(private val recyclerMediaItemBinding: RecyclerMediaItemBinding)
    : RecyclerView.ViewHolder(recyclerMediaItemBinding.root) {

    fun bind(data: MediaItem) {
        recyclerMediaItemBinding.titleMedia.text = data.mediaMetadata.title
    }
}