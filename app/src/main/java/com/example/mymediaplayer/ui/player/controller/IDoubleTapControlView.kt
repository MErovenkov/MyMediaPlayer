package com.example.mymediaplayer.ui.player.controller

import android.view.MotionEvent
import com.google.android.exoplayer2.Player

interface IDoubleTapControlView {
    fun setPlayer(player: Player)
    fun handleTouchEvent(event: MotionEvent)
}