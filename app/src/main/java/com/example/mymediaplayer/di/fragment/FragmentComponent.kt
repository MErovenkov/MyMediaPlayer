package com.example.mymediaplayer.di.fragment

import androidx.fragment.app.Fragment
import com.example.mymediaplayer.ui.fragment.ExoPlayerFragment
import com.example.mymediaplayer.ui.fragment.MediaFragment
import dagger.BindsInstance
import dagger.Subcomponent

@FragmentScope
@Subcomponent(modules = [NavigationFragmentModule::class])
interface FragmentComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(@FragmentQualifier @BindsInstance fragment: Fragment): FragmentComponent
    }

    fun inject(mediaFragment: MediaFragment)
    fun inject(exoPlayerFragment: ExoPlayerFragment)
}