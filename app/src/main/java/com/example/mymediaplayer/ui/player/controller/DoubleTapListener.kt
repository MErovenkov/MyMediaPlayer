package com.example.mymediaplayer.ui.player.controller

import android.os.Handler
import android.os.Looper
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

class DoubleTapListener(private val rootView: View,
                        private val controls: DoubleTapStateListener
) : GestureDetector.SimpleOnGestureListener() {

    private val handler = Handler(Looper.getMainLooper())
    private val runnable = Runnable {
        isDoubleTapping = false
        cancelInDoubleTapMode()
    }

    private var isDoubleTapping = false
    var tapDelay = 650
        set(value) {
            if (value > 0) field = value
        }

    fun keepInDoubleTapMode() {
        isDoubleTapping = true
        handler.removeCallbacks(runnable)
        handler.postDelayed(runnable, tapDelay.toLong())
    }

    fun cancelInDoubleTapMode() {
        isDoubleTapping = false
        handler.removeCallbacks(runnable)
        controls.onDoubleTapFinished()
    }

    override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
        if (isDoubleTapping) return true
        return rootView.performClick()
    }

    override fun onDoubleTap(event: MotionEvent): Boolean {
        if (!isDoubleTapping) {
            controls.onDoubleTapStarted(event)
        }
        return true
    }

    override fun onDoubleTapEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN && isDoubleTapping) {
            controls.onDoubleTapProgress(event)
            return true
        }
        return super.onDoubleTapEvent(event)
    }
}