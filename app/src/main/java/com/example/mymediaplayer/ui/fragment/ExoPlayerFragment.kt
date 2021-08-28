package com.example.mymediaplayer.ui.fragment

import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mymediaplayer.R
import com.example.mymediaplayer.databinding.ExoplayerControlViewBinding
import com.example.mymediaplayer.databinding.FragmentExoPlayerBinding
import com.example.mymediaplayer.util.CheckStatusNetwork
import com.example.mymediaplayer.util.extensions.*
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.util.ErrorMessageProvider
import com.google.android.exoplayer2.util.Util

class ExoPlayerFragment: Fragment(), ErrorMessageProvider<PlaybackException> {

    companion object {
        private const val TITLE_KEY = "title"
        private const val URL_KEY = "url"

        private const val CONTENT_POSITION_KEY = "contentPosition"
        private const val PLAY_WHEN_READY_KEY = "playerWhenReady"

        fun getNewBundle(title: String, url: Uri): Bundle {
            return Bundle().apply {
                putString(TITLE_KEY, title)
                putString(URL_KEY, url.toString())
            }
        }
    }

    private lateinit var fragmentBinding: FragmentExoPlayerBinding
    private lateinit var exoplayerControlBinding: ExoplayerControlViewBinding

    private var exoPlayer: SimpleExoPlayer? = null
    private var playWhenReady = true
    private var contentPosition = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        fragmentBinding = FragmentExoPlayerBinding.inflate(inflater, container, false)
        
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exoplayerControlBinding = ExoplayerControlViewBinding.bind(view)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        if (savedInstanceState != null) {
            contentPosition = savedInstanceState.getLong(CONTENT_POSITION_KEY, 0)
            playWhenReady = savedInstanceState.getBoolean(PLAY_WHEN_READY_KEY, true)
        }
    }

    override fun onStart() {
        super.onStart()
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
        exoPlayer = SimpleExoPlayer.Builder(requireContext())
            .build()
            .also { exoPlayer ->
                fragmentBinding.playerView.apply {
                    player = exoPlayer
                    setErrorMessageProvider(this@ExoPlayerFragment)
                }

                requireArguments().apply {
                    exoplayerControlBinding.exoVideoTitle.text = getString(TITLE_KEY)

                    exoPlayer.apply {
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
            }
    }

    override fun onPause() {
        super.onPause()
        contentPosition = exoPlayer!!.contentPosition
        playWhenReady = exoPlayer!!.playWhenReady

        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putLong(CONTENT_POSITION_KEY, contentPosition)
        outState.putBoolean(PLAY_WHEN_READY_KEY, playWhenReady)
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    private fun releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer!!.release()
            exoPlayer = null
            fragmentBinding.playerView.player = null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showSystemBars()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            hideSystemBars()
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            showSystemBars()
        }
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