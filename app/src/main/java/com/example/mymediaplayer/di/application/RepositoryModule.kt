package com.example.mymediaplayer.di.application

import android.content.Context
import com.example.mymediaplayer.data.repository.MediaLocalData
import com.example.mymediaplayer.data.repository.Repository
import com.example.mymediaplayer.util.Parser
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun repository(mediaLocalData: MediaLocalData): Repository = Repository(mediaLocalData)

    @Provides
    @Singleton
    fun mediaLocalData(parser: Parser): MediaLocalData = MediaLocalData(parser)

    @Provides
    @Singleton
    fun parser(moshi: Moshi, @ApplicationContext context: Context): Parser = Parser(moshi, context)

    @Provides
    @Singleton
    fun moshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
}