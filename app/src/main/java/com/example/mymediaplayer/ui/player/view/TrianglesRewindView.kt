package com.example.mymediaplayer.ui.player.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import com.example.mymediaplayer.R
import com.example.mymediaplayer.util.extensions.dpToPx
import kotlin.math.cos

class TrianglesRewindView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val PI = 3.1416
        private const val PAINT_MAX_ALPHA = 255
        private const val PAINT_MIN_ALPHA = 0
    }

    // Triangle argument
    private var degree = 50.0
        set(value) {
            radian = value * PI / 180
            field = value
        }
    private var radian: Double = degree * PI / 180
    private var triangleBase = dpToPx(15f)
        set(value) {
            triangleSide = value / (2 * cos(radian)).toFloat()
            field = value
        }
    private var triangleSide = triangleBase / (2 * cos(radian)).toFloat()

    // Path triangles
    private val pathOne = Path()
    private val pathSecond = Path()
    private val pathThree = Path()

    // Paint
    private var triangleColor = Color.BLACK
        set(value) {
            paintOne.color = value
            paintSecond.color = value
            paintThree.color = value
            field = value
        }

    private var paintOne = antiAliasPaint()
    private var paintSecond = antiAliasPaint()
    private var paintThree = antiAliasPaint()

    // Animation triangles
    private var animationDuration = 130
        set(value) {
            if (value > 0) field = value
        }

    private val firstAnimator: ValueAnimator by lazy {
        getTriangleAnimator(PAINT_MIN_ALPHA, PAINT_MIN_ALPHA, PAINT_MIN_ALPHA).apply {
            addUpdateListener {
                paintOne.alpha = it.animatedValue as Int
                invalidate()
            }
            doOnEnd {
                secondAnimator.start()
            }
        }
    }

    private val secondAnimator: ValueAnimator by lazy {
        getTriangleAnimator(PAINT_MAX_ALPHA, PAINT_MIN_ALPHA, PAINT_MIN_ALPHA).apply {
            addUpdateListener {
                paintSecond.alpha = it.animatedValue as Int
                invalidate()
            }
            doOnEnd {
                thirdAnimator.start()
            }
        }
    }

    private val thirdAnimator: ValueAnimator by lazy {
        getTriangleAnimator(PAINT_MAX_ALPHA, PAINT_MAX_ALPHA, PAINT_MIN_ALPHA).apply {
            addUpdateListener {
                paintThree.alpha = it.animatedValue as Int
                paintOne.alpha = PAINT_MAX_ALPHA - it.animatedValue as Int
                invalidate()
            }
            doOnEnd {
                fourthAnimator.start()
            }
        }
    }

    private val fourthAnimator: ValueAnimator by lazy {
        getTriangleAnimator(PAINT_MIN_ALPHA, PAINT_MAX_ALPHA, PAINT_MAX_ALPHA).apply {
            addUpdateListener {
                paintSecond.alpha = PAINT_MAX_ALPHA - it.animatedValue as Int
                invalidate()
            }
            doOnEnd {
                fifthAnimator.start()
            }
        }
    }

    private val fifthAnimator: ValueAnimator by lazy {
        getTriangleAnimator(PAINT_MIN_ALPHA, PAINT_MIN_ALPHA, PAINT_MAX_ALPHA).apply {
            addUpdateListener {
                paintThree.alpha = PAINT_MAX_ALPHA - it.animatedValue as Int
                invalidate()
            }
            doOnEnd {
                firstAnimator.start()
            }
        }
    }

    init {
        attrs?.let {
            context.theme.obtainStyledAttributes(attrs, R.styleable.TrianglesRewindView,
                defStyleAttr, 0).apply {

                triangleColor = getColor(R.styleable.TrianglesRewindView_triangleColor, triangleColor)
                animationDuration = getInt(R.styleable.TrianglesRewindView_animationDuration,
                    animationDuration)
                triangleBase = getDimension(R.styleable.TrianglesRewindView_sideTriangle, triangleBase)

                recycle()
            }
        }
    }

    private fun antiAliasPaint(): Paint {
        return Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = triangleColor
            style = Paint.Style.FILL
        }
    }

    private fun getTriangleAnimator(alphaOne: Int, alphaTwo: Int, alphaThree: Int): ValueAnimator {
        return ValueAnimator.ofInt(PAINT_MIN_ALPHA, PAINT_MAX_ALPHA)
            .setDuration(animationDuration.toLong())
            .apply {
                doOnStart {
                    paintOne.alpha = alphaOne
                    paintSecond.alpha = alphaTwo
                    paintThree.alpha = alphaThree
                }
            }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(widthMeasureSpec).toFloat()
            MeasureSpec.AT_MOST -> resolveSize((triangleSide * 3 + paddingLeft + paddingRight).toInt(),
                widthMeasureSpec).toFloat()
            else -> triangleSide * 3 + paddingLeft + paddingRight
        }

        val viewHeight = when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(heightMeasureSpec).toFloat()
            MeasureSpec.AT_MOST -> resolveSize((triangleBase + paddingTop + paddingBottom).toInt(),
                heightMeasureSpec).toFloat()
            else -> triangleBase + paddingTop + paddingBottom
        }

        setMeasuredDimension(viewWidth.toInt(), viewHeight.toInt())
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        pathSetting(pathOne)
        pathSetting(pathSecond)
        pathSetting(pathThree)
    }

    private fun pathSetting(path: Path) {
        val numberTriangle = when(path) {
            pathOne -> 1
            pathSecond -> 2
            pathThree -> 3
            else -> -1
        }

        path.apply {
            moveTo(triangleSide * (numberTriangle - 1) + paddingLeft, 0f + paddingTop)
            lineTo(triangleSide * numberTriangle + paddingLeft, triangleBase / 2 + paddingTop)
            lineTo(triangleSide * (numberTriangle - 1) + paddingLeft, triangleBase + paddingTop)
        }
    }

    private fun start() {
        firstAnimator.start()
    }

    private fun stop() {
        firstAnimator.pause()
        secondAnimator.pause()
        thirdAnimator.pause()
        fourthAnimator.pause()
        fifthAnimator.pause()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.apply {
            drawPath(pathOne, paintOne)
            drawPath(pathSecond, paintSecond)
            drawPath(pathThree, paintThree)
        }
    }

    override fun onVisibilityAggregated(isVisible: Boolean) {
        super.onVisibilityAggregated(isVisible)

        when(isVisible) {
            true -> start()
            false -> stop()
        }
    }
}