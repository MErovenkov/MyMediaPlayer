package com.example.mymediaplayer.util.extensions

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mymediaplayer.MyApplication
import com.example.mymediaplayer.di.activity.ActivityComponent
import com.example.mymediaplayer.di.fragment.FragmentComponent

fun Fragment.getFragmentComponent(): FragmentComponent {
    return (this.requireContext() as ActivityComponent.Holder)
        .activityComponent.fragmentComponent().create(this)
}

fun AppCompatActivity.getActivityComponent(): ActivityComponent {
    return (this.applicationContext as MyApplication).applicationComponent
        .activityComponent().create(this)
}