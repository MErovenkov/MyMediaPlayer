package com.example.mymediaplayer.di.application

import android.content.Context
import com.example.mymediaplayer.di.activity.ActivityComponent
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [SubActivityModule::class, RepositoryModule::class])
interface ApplicationComponent {
    @Component.Factory
    interface Factory {
        fun create(@ApplicationContext @BindsInstance context: Context): ApplicationComponent
    }

    fun activityComponent(): ActivityComponent.Factory
}