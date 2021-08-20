package com.example.mymediaplayer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymediaplayer.R
import com.example.mymediaplayer.data.repository.Repository
import com.example.mymediaplayer.util.UiState
import com.google.android.exoplayer2.MediaItem
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.FileNotFoundException

class MediaViewModel(private val repository: Repository): ViewModel() {
    private var _recyclerState: MutableStateFlow<UiState<ArrayList<MediaItem>>>
        = MutableStateFlow(UiState.Success(ArrayList()))

    val recyclerState: StateFlow<UiState<ArrayList<MediaItem>>> = _recyclerState.asStateFlow()

    private val handler = CoroutineExceptionHandler { _, exception ->
        when(exception) {
            is FileNotFoundException -> _recyclerState.value = UiState.Error(R.string.error_media_json_not_found)
        }
    }

    init {
        CoroutineScope(viewModelScope.coroutineContext + handler).launch(Dispatchers.IO) {
            repository.getMediaItems().collect {
                _recyclerState.value = UiState.Success(it)
            }
        }
    }
}