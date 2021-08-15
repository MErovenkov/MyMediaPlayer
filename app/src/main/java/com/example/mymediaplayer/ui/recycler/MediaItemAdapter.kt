package com.example.mymediaplayer.ui.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mymediaplayer.databinding.RecyclerMediaItemBinding
import com.google.android.exoplayer2.MediaItem

abstract class MediaItemAdapter: RecyclerView.Adapter<MediaItemHolder>(), ItemTouchHelperAdapter {
    private var itemList: ArrayList<MediaItem> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaItemHolder {
        return MediaItemHolder(RecyclerMediaItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MediaItemHolder, position: Int) {
        holder.apply {
            bind(itemList[position])
            itemView.setOnClickListener {
                onClickItem(position)
            }
        }
    }

    override fun getItemCount(): Int = itemList.size

    override fun onClickItem(position: Int) {}

    fun getItem(position: Int): MediaItem = itemList[position]

    fun updateItem(items: ArrayList<MediaItem>) {
        itemList.clear();
        itemList = items
        notifyDataSetChanged()
    }
}