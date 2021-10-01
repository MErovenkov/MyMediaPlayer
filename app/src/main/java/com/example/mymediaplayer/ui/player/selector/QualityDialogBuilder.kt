package com.example.mymediaplayer.ui.player.selector

import android.app.Dialog
import android.content.Context
import com.example.mymediaplayer.R
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Format

class QualityDialogBuilder(context: Context): AlertDialogBuilder(context) {

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

        private const val QUALITY_144p = 140
        private const val QUALITY_240p = 240
        private const val QUALITY_360p = 360
        private const val QUALITY_480p = 480
        private const val QUALITY_720p = 720
        private const val QUALITY_1080p = 1080
        private const val QUALITY_1440p = 1440
        private const val QUALITY_2160p = 2160

        private const val AUTO_QUALITY_TAG = -1
    }

    private val qualityBadge: String = context.getString(R.string.exo_quality_badge)
    private val dialogTitle = context.getString(R.string.exo_quality_dialog_title)
    private val autoQualityTitle = context.getString(R.string.exo_quality_dialog_auto)

    override fun createDialog(selectedTrackMap: MutableMap<Int, Int>,
                              supportedFormats: ArrayList<Format>): Dialog {
        if (!selectedTrackMap.containsKey(C.TRACK_TYPE_VIDEO)) {
            selectedTrackMap[C.TRACK_TYPE_VIDEO] = AUTO_QUALITY_TAG
        }

        supportedFormats.forEachIndexed { index, format ->
            checkedTextViewGroup
                .addView(createCheckedTextView(C.TRACK_TYPE_VIDEO, index, getQuality(format),
                                               selectedTrackMap), 0)
        }

        checkedTextViewGroup
            .addView(createCheckedTextView(C.TRACK_TYPE_VIDEO, AUTO_QUALITY_TAG, autoQualityTitle,
                                           selectedTrackMap), 0)

        getCheckedTextViewByTag(selectedTrackMap[C.TRACK_TYPE_VIDEO]!!).isChecked = true

        return super.createDialog(dialogTitle)
    }

    private fun getQuality(format: Format): String {
        val quality = getAccurateQuality(getQualityByScreen(format.width * format.height),
            getQualityByBitrate(format.bitrate / 1000))

        return "$quality$qualityBadge"
    }

    private fun getQualityByScreen(pixels: Int): Int {
        return when (pixels) {
            in 2..SCREEN_RESOLUTION_254x144 -> QUALITY_144p
            in (SCREEN_RESOLUTION_254x144 + 1)..SCREEN_RESOLUTION_426x240 -> QUALITY_240p
            in (SCREEN_RESOLUTION_426x240 + 1)..SCREEN_RESOLUTION_640x360 -> QUALITY_360p
            in (SCREEN_RESOLUTION_640x360 + 1)..SCREEN_RESOLUTION_854x480 -> QUALITY_480p
            in (SCREEN_RESOLUTION_854x480 + 1)..SCREEN_RESOLUTION_1280x720 -> QUALITY_720p
            in (SCREEN_RESOLUTION_1280x720 + 1)..SCREEN_RESOLUTION_1920x1080 -> QUALITY_1080p
            in (SCREEN_RESOLUTION_1920x1080 + 1)..SCREEN_RESOLUTION_2560x1440 -> QUALITY_1440p
            in (SCREEN_RESOLUTION_2560x1440 + 1)..SCREEN_RESOLUTION_3840x2106 -> QUALITY_2160p
            else -> 0
        }
    }

    private fun getQualityByBitrate(kbps: Int): Int {
        return when (kbps) {
            in 1 until MIN_BITRATE_240p -> QUALITY_144p
            in MIN_BITRATE_240p until MIN_BITRATE_360p -> QUALITY_240p
            in MIN_BITRATE_360p until MIN_BITRATE_480p -> QUALITY_360p
            in MIN_BITRATE_480p until MIN_BITRATE_720p -> QUALITY_480p
            in MIN_BITRATE_720p until MIN_BITRATE_1080p -> QUALITY_720p
            in MIN_BITRATE_1080p until MIN_BITRATE_1440p -> QUALITY_1080p
            in MIN_BITRATE_1440p until MIN_BITRATE_2160p -> QUALITY_1440p
            in MIN_BITRATE_2160p until 51000 -> QUALITY_2160p
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
}