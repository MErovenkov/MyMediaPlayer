package com.example.mymediaplayer.ui.player.selector

import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.example.mymediaplayer.util.extensions.getScreenOrientation
import com.example.mymediaplayer.util.extensions.hideSystemBars
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector

class TrackSelector(context: Context): DefaultTrackSelector(context) {

    companion object {
        private const val UNKNOWN_VALUE = -1
    }

    private var rendererIndex = UNKNOWN_VALUE
    private var trackType: Int = UNKNOWN_VALUE
    private var trackGroupArray: TrackGroupArray? = null

    private val overrides: ArrayList<SelectionOverride> = ArrayList()
    private val supportedFormats: ArrayList<Format> = ArrayList()

    /*
    * key - track type
    * */
    private val dialoguesMap: MutableMap<Int, Dialog?> = mutableMapOf()

    /*
    * key - track type
    * value - index of the selected track and dialog item
    * */
    var selectedTrackMap: MutableMap<Int, Int> = mutableMapOf()

    fun createTrackDialog(context: Context, rendererIndex: Int) {
        currentMappedTrackInfo?.apply {
            trackType = getRendererType(rendererIndex)

            if (dialoguesMap[trackType] == null) {
                getDialogBuilder(context)?.let {
                    this@TrackSelector.rendererIndex = rendererIndex
                    trackGroupArray = getTrackGroups(rendererIndex)

                    searchSupportableTracks()

                    dialoguesMap[trackType] = createDialog(context, it)
                    setupParameters()
                }
            }
        }
    }

    private fun searchSupportableTracks() {
        for (groupIndex in 0 until trackGroupArray!!.length) {
            for (trackIndex in 0 until trackGroupArray!![groupIndex].length) {
                if (isCapableRendering(groupIndex, trackIndex)) {
                    supportedFormats.add(trackGroupArray!![groupIndex].getFormat(trackIndex))
                    overrides.add(SelectionOverride(groupIndex, trackIndex))
                }
            }
        }
    }

    private fun isCapableRendering(groupIndex: Int, trackIndex: Int): Boolean {
        return currentMappedTrackInfo
            ?.getTrackSupport(rendererIndex, groupIndex, trackIndex) == C.FORMAT_HANDLED
    }

    private fun getDialogBuilder(context: Context): DialogBuilder? {
        return when(trackType) {
            C.TRACK_TYPE_VIDEO -> QualityDialogBuilder(context)

            else -> {
                Log.e(object{}.javaClass.name,
                    "${object{}.javaClass.enclosingMethod?.name}  " +
                            "Builder not found for this type of track")
                null
            }
        }
    }

    private fun createDialog(context: Context, dialogBuilder: DialogBuilder): Dialog {
        return dialogBuilder.createDialog(selectedTrackMap, supportedFormats).apply {
            setOnDismissListener {
                when(context.getScreenOrientation()) {
                    Configuration.ORIENTATION_LANDSCAPE ->
                        (context as FragmentActivity).hideSystemBars()
                }

                setupParameters()
            }
        }
    }

    private fun setupParameters() {
        selectedTrackMap[trackType]?.let {
            when {
                it >= 0 -> {
                    setParameters(buildUponParameters().setSelectionOverride(rendererIndex,
                        trackGroupArray!!, overrides[selectedTrackMap[trackType]!!]))
                }
                else -> setParameters(buildUponParameters().clearSelectionOverrides())
            }
        }
    }

    fun showDialog(rendererIndex: Int) {
        currentMappedTrackInfo?.apply {
            trackType = getRendererType(rendererIndex)
            dialoguesMap[trackType]?.show()
        }
    }

    fun dismissDialog(){
        dialoguesMap[trackType]?.dismiss()
    }
}