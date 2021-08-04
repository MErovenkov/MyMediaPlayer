package com.example.mymediaplayer.di.activity

import android.content.Context
import com.example.mymediaplayer.di.fragment.FragmentComponent
import com.example.mymediaplayer.ui.MainActivity
import dagger.BindsInstance
import dagger.Subcomponent

@ActivityScope
@Subcomponent(modules = [SubFragmentModule::class, ViewModelModule::class])
interface ActivityComponent {
    interface Holder {
        val activityComponent: ActivityComponent
    }

    @Subcomponent.Factory
    interface Factory {
        fun create(@ActivityContext @BindsInstance context: Context): ActivityComponent
    }
    fun fragmentComponent(): FragmentComponent.Factory

    fun inject(mainActivity: MainActivity)
}