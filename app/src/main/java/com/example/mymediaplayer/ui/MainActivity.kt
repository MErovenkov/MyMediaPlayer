package com.example.mymediaplayer.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mymediaplayer.databinding.ActivityMainBinding
import com.example.mymediaplayer.di.activity.ActivityComponent
import com.example.mymediaplayer.util.extensions.getActivityComponent

class MainActivity: AppCompatActivity(), ActivityComponent.Holder {
    private lateinit var binding: ActivityMainBinding

    override val activityComponent: ActivityComponent by lazy {
        getActivityComponent()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}