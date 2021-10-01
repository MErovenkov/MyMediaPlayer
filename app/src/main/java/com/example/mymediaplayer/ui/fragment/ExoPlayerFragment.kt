package com.example.mymediaplayer.ui.fragment

import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Rect
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.example.mymediaplayer.R
import com.example.mymediaplayer.databinding.ExoPlayerControlViewBinding
import com.example.mymediaplayer.databinding.FragmentExoPlayerBinding
import com.example.mymediaplayer.ui.player.selector.TrackSelector
import com.example.mymediaplayer.util.CheckStatusNetwork
import com.example.mymediaplayer.util.HolderSelectedTrackItems
import com.example.mymediaplayer.util.extensions.*
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.*
import com.google.android.exoplayer2.util.ErrorMessageProvider
import com.google.android.exoplayer2.util.Util

class ExoPlayerFragment: Fragment(), Player.Listener, ErrorMessageProvider<PlaybackException> {

    companion object {
        private const val TITLE_KEY = "title"
        private const val URL_KEY = "url"

        private const val CONTENT_POSITION_KEY = "contentPosition"
        private const val PLAY_WHEN_READY_KEY = "playerWhenReady"

        private const val SELECTED_TRACK_ITEMS_KEY = "holderSelectedTrackItems"
        private const val TRACK_TYPE_VIDEO_INDEX = 0

        private const val ACTION_PIP_CONTROL = "pip_control"
        private const val EXTRA_CONTROL_TYPE = "control_type"
        private const val CONTROL_TYPE_PLAY_OR_PAUSE = 2
        private const val REQUEST_PLAY_OR_PAUSE = 4

        fun getNewBundle(title: String, url: Uri): Bundle {
            return Bundle().apply {
                putString(TITLE_KEY, title)
                putString(URL_KEY, url.toString())
            }
        }
    }

    private lateinit var fragmentBinding: FragmentExoPlayerBinding
    private lateinit var exoPlayerControlBinding: ExoPlayerControlViewBinding

    private var exoPlayer: SimpleExoPlayer? = null
    private var playWhenReady = true
    private var contentPosition = 0L
    private var trackSelector: TrackSelector? = null
    private var holderSelectedTrackItems: HolderSelectedTrackItems = HolderSelectedTrackItems()

    private lateinit var broadcastReceiver: BroadcastReceiver
    private lateinit var actionPlay: RemoteAction
    private lateinit var actionPause: RemoteAction
    private val pipLaunchBounds = Rect()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentExoPlayerBinding.inflate(inflater, container, false)
        
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exoPlayerControlBinding = ExoPlayerControlViewBinding.bind(view)
            .apply {
                exoChangeQuality.setOnClickListener {
                    trackSelector?.showDialog(TRACK_TYPE_VIDEO_INDEX)
                }
            }

        @RequiresApi(Build.VERSION_CODES.O)
        if (isSupportPipMod()) {
            exoPlayerControlBinding.pictureInPicture.setOnClickListener {
                enterPipMode()
            }
            
            broadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    when (intent.getIntExtra(EXTRA_CONTROL_TYPE, 0)) {
                        CONTROL_TYPE_PLAY_OR_PAUSE -> {
                            exoPlayer?.apply {
                                if (isPlaying) pause() else play()
                                requireActivity().setPictureInPictureParams(buildPipParams(isPlaying))
                            }
                        }
                    }
                }
            }

            actionPlay = createRemoteAction(R.drawable.exo_icon_play, R.string.exo_controls_play_description)
            actionPause = createRemoteAction(R.drawable.exo_icon_pause, R.string.exo_controls_pause_description)
        } else {
            exoPlayerControlBinding.pictureInPicture.visibility = View.GONE
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    private fun enterPipMode() {
        fragmentBinding.playerView.getGlobalVisibleRect(pipLaunchBounds)
        fragmentBinding.playerView.useController = false
        requireActivity().enterPictureInPictureMode(buildPipParams(exoPlayer!!.isPlaying))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun buildPipParams(isPlaying: Boolean): PictureInPictureParams {
        return PictureInPictureParams.Builder()
            .setActions(listOf(if (isPlaying) actionPause else actionPlay))
            .setSourceRectHint(pipLaunchBounds)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createRemoteAction(@DrawableRes iconResId: Int, @StringRes titleResId: Int
        ): RemoteAction {
        return RemoteAction(
            Icon.createWithResource(requireContext(), iconResId),
            getString(titleResId),
            getString(titleResId),
            PendingIntent.getBroadcast(
                requireContext(), REQUEST_PLAY_OR_PAUSE,
                Intent(ACTION_PIP_CONTROL).putExtra(EXTRA_CONTROL_TYPE, CONTROL_TYPE_PLAY_OR_PAUSE),
                PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode)
        if (isInPictureInPictureMode) {
            fragmentBinding.playerView.useController = false
            requireActivity().registerReceiver(broadcastReceiver, IntentFilter(ACTION_PIP_CONTROL))
        } else {
            fragmentBinding.playerView.useController = true
            requireActivity().unregisterReceiver(broadcastReceiver)
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        if (savedInstanceState != null) {
            contentPosition = savedInstanceState.getLong(CONTENT_POSITION_KEY, 0)
            playWhenReady = savedInstanceState.getBoolean(PLAY_WHEN_READY_KEY, true)
            holderSelectedTrackItems = savedInstanceState
                .getSerializable(SELECTED_TRACK_ITEMS_KEY) as HolderSelectedTrackItems
        }
    }

    override fun onStart() {
        super.onStart()
        changeVisibilitySystemBars(requireContext().resources.configuration)

        when (isPipModeEnable()) {
            true -> exoPlayerControlBinding.pictureInPicture.visibility = View.VISIBLE
            false -> exoPlayerControlBinding.pictureInPicture.visibility = View.GONE
        }

        if (Util.SDK_INT > 23) {
            initializePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT <= 23) {
            initializePlayer()
        }
    }

    private fun initializePlayer() {
        trackSelector = TrackSelector(requireContext())
            .apply {
                selectedTrackMap = this@ExoPlayerFragment.holderSelectedTrackItems.selectedTrackMap
            }

        exoPlayer = SimpleExoPlayer.Builder(requireContext())
            .setTrackSelector(trackSelector!!)
            .build()
            .also { exoPlayer ->
                requireArguments().apply {
                    exoPlayerControlBinding.exoVideoTitle.text = getString(TITLE_KEY)

                    exoPlayer.apply {
                        addListener(this@ExoPlayerFragment)

                        setMediaItem(
                            MediaItem.Builder()
                                .setUri(getString(URL_KEY))
                                .build()
                        )

                        seekTo(this@ExoPlayerFragment.contentPosition)
                        playWhenReady = this@ExoPlayerFragment.playWhenReady

                        prepare()
                    }
                }

                fragmentBinding.playerView.apply {
                    player = exoPlayer
                    setErrorMessageProvider(this@ExoPlayerFragment)
                }
            }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putLong(CONTENT_POSITION_KEY, contentPosition)
        outState.putBoolean(PLAY_WHEN_READY_KEY, playWhenReady)
        outState.putSerializable(SELECTED_TRACK_ITEMS_KEY, holderSelectedTrackItems)
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    private fun releasePlayer() {
        exoPlayer?.apply {
            this@ExoPlayerFragment.contentPosition = contentPosition
            this@ExoPlayerFragment.playWhenReady = playWhenReady
            release()
        }
        exoPlayer = null
        fragmentBinding.playerView.player = null

        holderSelectedTrackItems.selectedTrackMap = trackSelector!!.selectedTrackMap
        trackSelector!!.dismissDialog()
        trackSelector = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().showSystemBars()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        changeVisibilitySystemBars(newConfig)
    }

   private fun changeVisibilitySystemBars(newConfig: Configuration) {
       if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
           requireActivity().hideSystemBars()
       } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
           requireActivity().showSystemBars()
       }
   }

    override fun onTracksChanged(trackGroups: TrackGroupArray,
                                 trackSelections: TrackSelectionArray) {
        trackSelector?.createTrackDialog(requireContext(), TRACK_TYPE_VIDEO_INDEX)
    }

    override fun getErrorMessage(error: PlaybackException): Pair<Int, String> {
        val errorString: String = when (error.errorCode) {
            PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED -> {
                if (!CheckStatusNetwork.isActive) getString(R.string.error_no_internet_access)
                else getString(R.string.error_media_host_unavailable)
            }
            PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT -> getString(R.string.error_network_connection_timeout)
            PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND -> getString(R.string.error_file_not_found)
            else -> error.errorCodeName
        }

        return Pair.create(0, errorString)
    }
}