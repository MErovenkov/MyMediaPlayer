package com.example.mymediaplayer.di.fragment

import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.mymediaplayer.ui.navigation.IMediaNavigation
import com.example.mymediaplayer.ui.navigation.Navigation
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class NavigationFragmentModule {

    companion object {
        private const val FRAGMENT_NAV_CONTROLLER = "FragmentNavController"
    }

    @Provides
    @FragmentScope
    @Named(FRAGMENT_NAV_CONTROLLER)
    fun navController(@FragmentQualifier fragment: Fragment)
        : NavController = fragment.findNavController()

    @Provides
    @FragmentScope
    fun mediaNavigation(@Named(FRAGMENT_NAV_CONTROLLER) navController: NavController)
        : IMediaNavigation = Navigation(navController)
}