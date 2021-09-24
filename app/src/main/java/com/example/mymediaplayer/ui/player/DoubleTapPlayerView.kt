package com.example.mymediaplayer.ui.player

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import androidx.core.view.forEach
import com.example.mymediaplayer.R
import com.example.mymediaplayer.ui.player.controller.IDoubleTapControlView
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView

class DoubleTapPlayerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : PlayerView(context, attrs, defStyleAttr) {

    private var doubleTapControlView: IDoubleTapControlView? = null
    private var doubleTapControllerId: Int = -1

    private var isDoubleTapEnabled = false

    init {
        attrs?.let {
            context.obtainStyledAttributes(attrs, R.styleable.DoubleTapPlayerView,
                                           defStyleAttr, 0).apply {

                doubleTapControllerId = getResourceId(
                    R.styleable.DoubleTapPlayerView_double_tap_controller_id, doubleTapControllerId)

                if (doubleTapControllerId != -1) {
                    isDoubleTapEnabled = true

                    LayoutInflater.from(context).inflate(doubleTapControllerId,
                        this@DoubleTapPlayerView, true)

                    forEach {
                        if (it.id == R.id.exo_double_tap_control) {
                            doubleTapControlView = it as IDoubleTapControlView
                        }
                    }
                }

                recycle()
            }
        }
    }

    override fun setPlayer(player: Player?) {
        super.setPlayer(player)

        if (player != null) {
            doubleTapControlView?.setPlayer(player)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isDoubleTapEnabled) {
            doubleTapControlView?.handleTouchEvent(event)
            return true
        }
        return super.onTouchEvent(event)
    }
}