package com.example.mymediaplayer.data.repository

import com.google.android.exoplayer2.MediaItem
import kotlinx.coroutines.flow.Flow
import java.io.FileNotFoundException

class Repository(private val mediaLocalData: MediaLocalData) {

    @Throws(FileNotFoundException::class)
    fun getMediaItems(): Flow<ArrayList<MediaItem>> = mediaLocalData.getMediaItemList()
}