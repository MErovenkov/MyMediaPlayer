package com.example.mymediaplayer.ui.player.view

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.mymediaplayer.R
import com.example.mymediaplayer.util.extensions.dpToPx

class SemicircleRippleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthView = context.resources.displayMetrics.widthPixels
    private var heightView = context.resources.displayMetrics.heightPixels

    // Paint semicircle
    private var semicircleColor = Color.GRAY
        set(value) {
            semicirclePaint.color = value
            field = value
        }
    private var semicirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        .apply {
            style = Paint.Style.FILL
            color = semicircleColor
        }

    // Paint ripple
    private var rippleColor = Color.GRAY
        set(value) {
            ripplePaint.color = value
            field = value
        }
    private var ripplePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        .apply {
            style = Paint.Style.FILL
            color = rippleColor
        }

    // Semicircle
    private var semicirclePath = Path()
    private var semicircleArcSize = dpToPx(36f)

    // Ripple
    private var rippleX = 0f
    private var rippleY = 0f
    private var rippleRadius = 0f

    private var rippleMinRadius = 30 * context.resources.displayMetrics.density
    private var rippleMaxRadius = 400 * context.resources.displayMetrics.density

    // Rotation semicircle
    private var isLeft = true

    // Animation ripple
    private var animationDuration = 650
        set(value) {
            if (value > 0) field = value
        }

    private val rippleAnimation: ValueAnimator by lazy {
        ValueAnimator.ofFloat(0f, 1f).setDuration(animationDuration.toLong()).apply {
            addUpdateListener {
                rippleRadius = rippleMinRadius +
                        ((rippleMaxRadius - rippleMinRadius) * it.animatedValue as Float)
                invalidate()
            }
        }
    }

    init {
        attrs?.let {
            context.theme.obtainStyledAttributes(attrs, R.styleable.SemicircleRippleView,
                defStyleAttr, 0).apply {

                semicircleColor = getColor(R.styleable.SemicircleRippleView_semicircleColor,
                    semicircleColor)

                semicircleArcSize = getDimension(R.styleable.SemicircleRippleView_semicircleArcSize,
                    semicircleArcSize)

                rippleColor = getColor(R.styleable.SemicircleRippleView_rippleColor,
                    semicircleColor)

                animationDuration = getInt(R.styleable.SemicircleRippleView_animationDuration,
                    animationDuration)

                recycle()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(widthMeasureSpec)
            MeasureSpec.AT_MOST -> resolveSize(widthView, MeasureSpec.getSize(widthMeasureSpec))
            else -> widthView
        }

        val viewHeight = when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(heightMeasureSpec)
            MeasureSpec.AT_MOST -> resolveSize(heightView, MeasureSpec.getSize(heightMeasureSpec))
            else -> heightView
        }

        setMeasuredDimension(viewWidth, viewHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        widthView = w
        heightView = h

        settingPathRipple()
    }

    private fun settingPathRipple() {
        val widthShare = widthView * 0.40f
        val startPositionX = if (isLeft) 0f + paddingLeft else widthView.toFloat() - paddingRight
        val rotationIndex = if (isLeft) 1 else -1

        semicirclePath.apply {
            reset()
            moveTo(startPositionX, 0f + paddingTop)

            lineTo(rotationIndex * (widthShare - semicircleArcSize) + startPositionX,
                0f + paddingTop)

            quadTo(rotationIndex * (widthShare + semicircleArcSize) + startPositionX,
                heightView.toFloat() / 2,
                rotationIndex * (widthShare - semicircleArcSize) + startPositionX,
                heightView.toFloat()
            )

            lineTo(startPositionX, heightView.toFloat() - paddingBottom)
            close()
        }

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.apply {
            clipPath(semicirclePath)
            drawPath(semicirclePath, semicirclePaint)
            drawCircle(rippleX, rippleY, rippleRadius, ripplePaint)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        updateCircleAnimPosition(event.x, event.y)
        return super.onTouchEvent(event)
    }

    private fun updateCircleAnimPosition(x: Float, y: Float) {
        if (x <= widthView * 0.40f || x >= widthView * 0.60f) {
            rippleX = x
            rippleY = y

            rippleAnimation.start()

            val newIsLeft = x <= widthView / 2
            if (isLeft != newIsLeft) {
                isLeft = newIsLeft
                settingPathRipple()
            }
        }
    }
}