package com.example.mymediaplayer.ui.player.controller

import android.content.Context
import android.media.session.PlaybackState
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.forEach
import com.example.mymediaplayer.R
import com.example.mymediaplayer.ui.player.view.SemicircleRippleView
import com.example.mymediaplayer.ui.player.view.TextSecondView
import com.example.mymediaplayer.ui.player.view.TrianglesRewindView
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView

class DoubleTapControlView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr), IDoubleTapControlView, DoubleTapStateListener {

    private var gestureDetector: GestureDetectorCompat? = null
    private var gestureListenerDouble: DoubleTapListener? = null

    // Views
    private var semicircleRippleView: SemicircleRippleView? = null
    private var textSecondView: TextSecondView? = null
    private var trianglesRewindView: TrianglesRewindView? = null

    // Player
    private var playerView: PlayerView? = null
    private var player: Player? = null

    private var isForwardRewind = false

    private var rewindTime = 10
    private var finalRewindTime = 0

    private var tapDelay = 0

    init {
        attrs?.let {
            context.theme.obtainStyledAttributes(attrs, R.styleable.DoubleTapControlView,
                defStyleAttr, 0).apply {

                rewindTime = getInt(R.styleable.DoubleTapControlView_rewindTime, rewindTime)
                tapDelay = getInt(R.styleable.DoubleTapControlView_tapDelay, tapDelay)
                recycle()
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        forEach {
            when (it.id) {
                R.id.semicircle_ripple -> semicircleRippleView = it as SemicircleRippleView
                R.id.text_second -> textSecondView = it as TextSecondView
                R.id.triangle_rewind -> trianglesRewindView = it as TrianglesRewindView
            }
        }

        changeVisible(GONE)

        this.playerView = this.parent as PlayerView

        gestureListenerDouble = DoubleTapListener(this.parent as PlayerView, this)
            .also { it.tapDelay = this.tapDelay }

        gestureDetector = GestureDetectorCompat(context, gestureListenerDouble as DoubleTapListener)
    }

    private fun changeVisible(visibility: Int) {
        this.visibility = visibility
        semicircleRippleView?.visibility = visibility
        trianglesRewindView?.visibility = visibility
        textSecondView?.visibility = visibility
    }

    override fun setPlayer(player: Player) {
        this.player = player
    }

    override fun handleTouchEvent(event: MotionEvent) {
        gestureDetector?.onTouchEvent(event)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        semicircleRippleView?.let { measureChild(it, widthMeasureSpec, heightMeasureSpec) }
        trianglesRewindView?.let { measureChild(it, widthMeasureSpec, heightMeasureSpec) }
        textSecondView?.let { measureChild(it, widthMeasureSpec, heightMeasureSpec) }

        val width = when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(widthMeasureSpec)
            MeasureSpec.AT_MOST -> playerView!!.measuredWidth
            else -> playerView!!.measuredWidth
        }

        val height = when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(heightMeasureSpec)
            MeasureSpec.AT_MOST -> playerView!!.measuredHeight
            else -> trianglesRewindView!!.measuredHeight
        }

        setMeasuredDimension(width, height)
    }

    override fun onLayout(p0: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val widthViewGroup = r - l
        val heightViewGroup = b - t

        semicircleRippleView?.layout(paddingLeft, paddingTop,
            widthViewGroup - paddingRight, heightViewGroup - paddingBottom)

        if (isForwardRewind) {
            val forwardX = widthViewGroup / 5 * 4 + paddingLeft - paddingRight
            val forwardY = heightViewGroup / 2 + paddingTop - paddingBottom

            trianglesRewindView?.apply {
                layout(forwardX - measuredWidth / 2, forwardY - measuredHeight,
                    forwardX + measuredWidth / 2, forwardY)
                this.rotation = 0f
            }

            textSecondView?.apply {
                layout(forwardX - measuredWidth / 2, forwardY,
                    forwardX + measuredWidth / 2, forwardY + measuredHeight)
            }
        } else {
            val rewindX = widthViewGroup / 5 + paddingLeft - paddingRight
            val rewindY = heightViewGroup / 2 + paddingTop - paddingBottom

            trianglesRewindView?.apply {
                layout(rewindX - measuredWidth / 2, rewindY - measuredHeight,
                    rewindX + measuredWidth / 2, rewindY + paddingTop)
                this.rotation = 180f
            }

            textSecondView?.apply {
                layout(rewindX - measuredWidth / 2, rewindY,
                    rewindX + measuredWidth / 2, rewindY + measuredHeight)
            }
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                onDoubleTapProgress(event)
                return true
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onDoubleTapStarted(event: MotionEvent) {
        if (player != null && playerView != null) {
            if (isShowControls(event) && isRewindPositionInside(rewindTime)
                && visibility != View.VISIBLE) {

                playerView!!.useController = false
                player!!.pause()
                textSecondView?.second = 0
                finalRewindTime = 0
                changeVisible(VISIBLE)
            }
        }
    }

    override fun onDoubleTapProgress(event: MotionEvent) {
        if (player != null && playerView != null) {
            if (isShowControls(event) && isRewindPositionInside(finalRewindTime)) {
                finalRewindTime += rewindTime
                textSecondView?.second = finalRewindTime
            }
        }
    }

    private fun isShowControls(event: MotionEvent): Boolean {
        return if (isValidPlaybackState() && isTapInsideController(event)) {
            semicircleRippleView?.onTouchEvent(event)
            gestureListenerDouble?.keepInDoubleTapMode()
            true
        } else false
    }

    private fun isValidPlaybackState(): Boolean {
        return player!!.playbackState != PlaybackState.STATE_ERROR
                && player!!.playbackState != PlaybackState.STATE_NONE
                && player!!.playbackState != PlaybackState.STATE_STOPPED
    }

    private fun isTapInsideController(event: MotionEvent): Boolean {
        return when {
            event.x < playerView!!.width * 0.4 -> {
                if (isForwardRewind) {
                    changeRewindDirection()
                }
                true
            }

            event.x > playerView!!.width * 0.6 -> {
                if (!isForwardRewind) {
                    changeRewindDirection()
                }
                true
            }

            else -> false
        }
    }

    private fun changeRewindDirection() {
        isForwardRewind = !isForwardRewind
        textSecondView?.second = 0
        finalRewindTime = 0
    }

    private fun isRewindPositionInside(newPosition: Int): Boolean {
        player!!.apply {
            when(isForwardRewind) {
                true -> {
                    if (currentPosition + newPosition * 1000 >= duration) {
                        gestureListenerDouble?.cancelInDoubleTapMode()
                        return false
                    }
                }

                false -> {
                    if (currentPosition - newPosition * 1000 < 0) {
                        gestureListenerDouble?.cancelInDoubleTapMode()
                        return false
                    }
                }
            }
        }

        return true
    }

    override fun onDoubleTapFinished() {
        if (this.visibility == View.VISIBLE && player != null || playerView != null) {
            changeVisible(INVISIBLE)

            player!!.apply {
                when (isForwardRewind) {
                    true -> seekTo(currentPosition.plus(finalRewindTime * 1000))
                    false -> seekTo(currentPosition.minus(finalRewindTime * 1000))
                }
                play()
            }

            playerView!!.useController = true
        }
    }
}