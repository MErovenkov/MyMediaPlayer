package com.example.mymediaplayer.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class MediaDto (
    @Json(name = "title")
    val title: String = DEFAULT_MEDIA_NAME,

    @Json(name = "uri")
    val uri: String? = null,

    @Json(name = "ad_tag_uri")
    val adTagUri: String? = null
) {
    companion object {
        private const val DEFAULT_MEDIA_NAME = "MEDIA_NAME"
    }
}