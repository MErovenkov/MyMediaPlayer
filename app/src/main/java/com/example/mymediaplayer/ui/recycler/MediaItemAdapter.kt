package com.example.mymediaplayer.ui.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.mymediaplayer.databinding.RecyclerMediaItemBinding
import com.google.android.exoplayer2.MediaItem

abstract class MediaItemAdapter: ListAdapter<MediaItem, MediaItemHolder>(MediaItemDiffCallback()),
    ItemTouchHelperAdapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaItemHolder {
        return MediaItemHolder(RecyclerMediaItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MediaItemHolder, position: Int) {
        holder.apply {
            bind(getItem(position))
            itemView.setOnClickListener {
                onClickItem(position)
            }
        }
    }

    override fun getItemCount(): Int = currentList.size

    override fun onClickItem(position: Int) {}
}