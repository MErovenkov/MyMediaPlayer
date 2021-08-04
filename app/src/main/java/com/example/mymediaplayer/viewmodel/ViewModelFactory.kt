package com.example.mymediaplayer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mymediaplayer.data.repository.Repository

class ViewModelFactory(private val repository: Repository): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when(modelClass) {
            MediaViewModel::class.java -> MediaViewModel(repository) as T
            else -> super.create(modelClass)
        }
    }
}