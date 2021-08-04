package com.example.mymediaplayer.util

import androidx.annotation.StringRes

sealed class UiState<out T> {
    data class Success<out T>(val resources: T): UiState<T>()
    data class Error(@StringRes val message: Int): UiState<Nothing>()
}