package com.example.mymediaplayer.data.repository

import com.example.mymediaplayer.util.Parser
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.FileNotFoundException
import kotlin.jvm.Throws

class MediaLocalData(private val parser: Parser) {

    companion object {
        private const val NAME_MEDIA_JSON_FILE = "media.json"
    }

    @Throws(FileNotFoundException::class)
    fun getMediaItemList(): Flow<ArrayList<MediaItem>> = flow {
        val mediaDtoList = runCatching { parser.parseAssetFile(NAME_MEDIA_JSON_FILE) }.getOrThrow()

        val mediaItemList = mediaDtoList
            ?.filter { mediaDto -> mediaDto.url != null }
            ?.map { mediaDto ->
                MediaItem.Builder()
                    .setUri(mediaDto.url)
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setTitle(mediaDto.title)
                            .build()
                    )
                    .build()
            } as ArrayList<MediaItem>

        emit(mediaItemList)
    }
}