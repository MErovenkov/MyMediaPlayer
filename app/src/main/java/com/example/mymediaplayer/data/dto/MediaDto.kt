package com.example.mymediaplayer.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class MediaDto (
    @Json(name = "title")
    val title: String,

    @Json(name = "url")
    val url: String
)