package com.example.mymediaplayer.util

import android.content.Context
import com.example.mymediaplayer.data.dto.MediaDto
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.io.FileNotFoundException

class Parser(moshi: Moshi, context: Context) {
    private val applicationContext = context.applicationContext
    private val listTypes = Types.newParameterizedType(List::class.java, MediaDto::class.java)
    private val parserAdapter: JsonAdapter<List<MediaDto>> = moshi.adapter(listTypes)

    fun parseAssetFile(nameFile: String): List<MediaDto>? {
        try {
            var json: String

            applicationContext.assets.open(nameFile)
                .bufferedReader()
                .use { json = it.readText() }

            return parserAdapter.fromJson(json)
       } catch (e: FileNotFoundException) {
           throw e
       }
    }
}