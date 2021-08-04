package com.example.mymediaplayer

import android.app.Application
import com.example.mymediaplayer.di.application.ApplicationComponent
import com.example.mymediaplayer.di.application.DaggerApplicationComponent
import com.example.mymediaplayer.util.CheckStatusNetwork

class MyApplication: Application() {
    val applicationComponent: ApplicationComponent by lazy {
        DaggerApplicationComponent.factory().create(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()
        CheckStatusNetwork.registerNetworkCallback(applicationContext)
    }
}