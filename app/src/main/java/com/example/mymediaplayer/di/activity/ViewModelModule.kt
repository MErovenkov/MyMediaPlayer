package com.example.mymediaplayer.di.activity

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.mymediaplayer.data.repository.Repository
import com.example.mymediaplayer.viewmodel.MediaViewModel
import com.example.mymediaplayer.viewmodel.ViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class ViewModelModule {

    @Provides
    @ActivityScope
    fun viewModelFactory(repository: Repository): ViewModelFactory = ViewModelFactory(repository)

    @Provides
    @ActivityScope
    fun mediaViewModel(@ActivityContext context: Context,
                       viewModelFactory: ViewModelFactory): MediaViewModel
    = ViewModelProvider(context as ViewModelStoreOwner,
                        viewModelFactory)[MediaViewModel::class.java]
}