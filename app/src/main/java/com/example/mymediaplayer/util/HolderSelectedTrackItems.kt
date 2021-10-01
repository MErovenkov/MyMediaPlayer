package com.example.mymediaplayer.util

import java.io.Serializable

/*
* Needed to save index the selected tracks.
* */
class HolderSelectedTrackItems: Serializable {
    /*
    * key - track type
    * value - index of the selected track and dialog item
    * */
    var selectedTrackMap: MutableMap<Int, Int> = mutableMapOf()
}