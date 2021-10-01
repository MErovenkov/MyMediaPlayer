package com.example.mymediaplayer.ui.player.selector

import android.app.Dialog
import com.google.android.exoplayer2.Format

interface DialogBuilder {
    fun createDialog(selectedTrackMap: MutableMap<Int, Int>,
                     supportedFormats: ArrayList<Format>): Dialog
}