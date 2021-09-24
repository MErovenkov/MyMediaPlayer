package com.example.mymediaplayer.ui.player

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import androidx.core.view.forEach
import androidx.core.view.get
import androidx.fragment.app.FragmentActivity
import com.example.mymediaplayer.R
import com.example.mymediaplayer.util.extensions.hideSystemBars
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector

class VideoQualityTrackSelector(context: Context)
    : DefaultTrackSelector(context),  View.OnClickListener {

    companion object {
        private const val SCREEN_RESOLUTION_254x144 = 245 * 144
        private const val SCREEN_RESOLUTION_426x240 = 426 * 240
        private const val SCREEN_RESOLUTION_640x360 = 640 * 360
        private const val SCREEN_RESOLUTION_854x480 = 854 * 480
        private const val SCREEN_RESOLUTION_1280x720 = 1280 * 720
        private const val SCREEN_RESOLUTION_1920x1080 = 1920 * 1080
        private const val SCREEN_RESOLUTION_2560x1440 = 2560 * 1440
        private const val SCREEN_RESOLUTION_3840x2106 = 3840 * 2106

        // Kilobits
        private const val MIN_BITRATE_240p = 400
        private const val MIN_BITRATE_360p = 750
        private const val MIN_BITRATE_480p = 1000
        private const val MIN_BITRATE_720p = 2500
        private const val MIN_BITRATE_1080p = 4500
        private const val MIN_BITRATE_1440p = 6000
        private const val MIN_BITRATE_2160p = 13000
    }

    private var rendererIndex = 0
    private var videoTrackGroupIndex = 0
    private var trackGroupArray: TrackGroupArray? = null
    private var overrides: ArrayList<SelectionOverride> = ArrayList()

    //View
    private var qualityBadge: String = context.getString(R.string.exo_quality_badge)
    private lateinit var qualityViewGroup: ViewGroup

    var indexChoiceQuality = 0
        set(value) {
            if (value >= 0) {
                field = value
            }
        }

    private var qualityDialog: AlertDialog? = null

    fun buildQualityDialog(context: Context) {
        currentMappedTrackInfo?.let {
            if (trackGroupArray != it.getTrackGroups(rendererIndex)) {
                trackGroupArray = it.getTrackGroups(rendererIndex)

                qualityDialog = AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.exo_quality_dialog_title))
                    .setView(buildDialogView(context))
                    .create()

                qualityDialog!!.setOnDismissListener {
                    if (context.resources.configuration
                            .orientation == Configuration.ORIENTATION_LANDSCAPE) {

                            (context as FragmentActivity).hideSystemBars()
                    }
                }
            }
        }
    }

    fun showQualityDialog() = qualityDialog?.show()
    fun dismissQualityDialog() = qualityDialog?.dismiss()

    @SuppressLint("InflateParams")
    private fun buildDialogView(context: Context): View {
        val layoutInflater = LayoutInflater.from(context)

        return layoutInflater.inflate(R.layout.exo_quality_selectior_dialog, null).apply {
            (this as ViewGroup).forEach {
                when (it.id) {
                    R.id.quality_view_group -> qualityViewGroup = it as ViewGroup
                }
            }

            val autoQuality = getQualityView(layoutInflater).apply {
                text = context.getString(R.string.exo_quality_dialog_auto)
                tag = 0
            }
            qualityViewGroup.addView(autoQuality, 0)

            val videoTrackGroup = trackGroupArray!![videoTrackGroupIndex]
            var qualityViewIndex = 1

            for (videoTrackIndex in videoTrackGroup.length - 1 downTo 0) {
                if (isCapableRendering(videoTrackIndex)) {
                    val qualityView = getQualityView(layoutInflater).apply {
                        text = getQuality(videoTrackGroup.getFormat(videoTrackIndex))
                        tag = qualityViewIndex
                    }

                    qualityViewGroup.addView(qualityView, qualityViewIndex)

                    overrides.add(SelectionOverride(videoTrackGroupIndex, videoTrackIndex))
                    qualityViewIndex++
                }
            }

            if (indexChoiceQuality != 0) {
                setParameters(buildUponParameters().setSelectionOverride(videoTrackGroupIndex,
                        this@VideoQualityTrackSelector.trackGroupArray!!,
                        overrides[indexChoiceQuality - 1]))
            }

            (qualityViewGroup[indexChoiceQuality] as CheckedTextView).isChecked = true
        }
    }

    private fun isCapableRendering(trackIndex: Int): Boolean {
        return currentMappedTrackInfo?.getTrackSupport(videoTrackGroupIndex, 0,
            trackIndex) == C.FORMAT_HANDLED
    }

    private fun getQualityView(layoutInflater: LayoutInflater): CheckedTextView {
        return (layoutInflater.inflate(R.layout.quality_selector_item, qualityViewGroup, false)
                as CheckedTextView).apply {

            isFocusable = true
            setOnClickListener(this@VideoQualityTrackSelector)
        }
    }

    private fun getQuality(format: Format): String {
        val quality = getAccurateQuality(getQualityByScreen(format.width * format.height),
            getQualityByBitrate(format.bitrate / 1000))

        return "$quality$qualityBadge"
    }

    private fun getQualityByScreen(pixels: Int): Int {
        return when (pixels) {
            in 2..SCREEN_RESOLUTION_254x144 -> 144
            in (SCREEN_RESOLUTION_254x144 + 1)..SCREEN_RESOLUTION_426x240 -> 240
            in (SCREEN_RESOLUTION_426x240 + 1)..SCREEN_RESOLUTION_640x360 -> 360
            in (SCREEN_RESOLUTION_640x360 + 1)..SCREEN_RESOLUTION_854x480 -> 480
            in (SCREEN_RESOLUTION_854x480 + 1)..SCREEN_RESOLUTION_1280x720 -> 720
            in (SCREEN_RESOLUTION_1280x720 + 1)..SCREEN_RESOLUTION_1920x1080 -> 1080
            in (SCREEN_RESOLUTION_1920x1080 + 1)..SCREEN_RESOLUTION_2560x1440 -> 1440
            in (SCREEN_RESOLUTION_2560x1440 + 1)..SCREEN_RESOLUTION_3840x2106 -> 2160
            else -> 0
        }
    }

    private fun getQualityByBitrate(kbps: Int): Int {
        return when (kbps) {
            in 1 until MIN_BITRATE_240p -> 144
            in MIN_BITRATE_240p until MIN_BITRATE_360p -> 240
            in MIN_BITRATE_360p until MIN_BITRATE_480p -> 360
            in MIN_BITRATE_480p until MIN_BITRATE_720p -> 480
            in MIN_BITRATE_720p until MIN_BITRATE_1080p -> 720
            in MIN_BITRATE_1080p until MIN_BITRATE_1440p -> 1080
            in MIN_BITRATE_1440p until MIN_BITRATE_2160p -> 1440
            in MIN_BITRATE_2160p until 51000 -> 2160
            else -> 0
        }
    }

    private fun getAccurateQuality(screenQuality: Int, bitrateQuality: Int): Int {
        return when {
            bitrateQuality == 0 -> screenQuality
            screenQuality == 0 || screenQuality > bitrateQuality -> bitrateQuality
            else -> screenQuality
        }
    }

    override fun onClick(view: View) {
        val qualityViewIndex = view.tag as Int

        if (qualityViewIndex == 0 && indexChoiceQuality != qualityViewIndex) {
            changeChoicePosition(0)
            setParameters(buildUponParameters().clearSelectionOverrides())
        } else if (indexChoiceQuality != qualityViewIndex) {
            changeChoicePosition(qualityViewIndex)
            setParameters(buildUponParameters()
                .setSelectionOverride(videoTrackGroupIndex,
                                      trackGroupArray!!,
                                      overrides[qualityViewIndex - 1]))
        }

        (qualityViewGroup[indexChoiceQuality] as CheckedTextView).isChecked = true
        qualityDialog?.dismiss()
    }

    private fun changeChoicePosition(newIndexChoiceQuality: Int) {
        (qualityViewGroup[indexChoiceQuality] as CheckedTextView).isChecked = false
        indexChoiceQuality = newIndexChoiceQuality
    }
}