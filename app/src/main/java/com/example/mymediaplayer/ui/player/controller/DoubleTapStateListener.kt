package com.example.mymediaplayer.ui.player.controller

import android.view.MotionEvent

interface DoubleTapStateListener {
    fun onDoubleTapStarted(event: MotionEvent)
    fun onDoubleTapProgress(event: MotionEvent)
    fun onDoubleTapFinished()
}