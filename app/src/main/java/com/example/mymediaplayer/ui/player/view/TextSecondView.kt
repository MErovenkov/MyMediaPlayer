package com.example.mymediaplayer.ui.player.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.example.mymediaplayer.R
import com.example.mymediaplayer.util.extensions.spToPx

class TextSecondView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var textWidth = 0f
    private var textHeight = 0f

    private var textPositionX = 0f
    private var textPositionY = 0f

    private val textBounds = Rect()

    private var textColor = Color.BLACK
    private var textSize: Float = spToPx(13f)
    private var textFont: Typeface = Typeface.defaultFromStyle(Typeface.NORMAL)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var secondText = context.resources.getString(R.string.second)

    var second: Int = 0
        set(value) {
            if (value >= 0) {
                text = "$value $secondText"

                settingTextSize()
                field = value

                requestLayout()
            }
        }

    private var text: String = "$second $secondText"

    init {
        attrs?.let {
            context.theme.obtainStyledAttributes(attrs, R.styleable.TextSecondView,
                defStyleAttr, 0).apply {

                textColor = getColor(R.styleable.TextSecondView_textColor, textColor)
                textSize = getDimension(R.styleable.TextSecondView_textSize, textSize)
                textFont = getTextFont(getResourceId(R.styleable.TextSecondView_android_fontFamily, 0))

                recycle()
            }
        }

        paint.apply {
            textSize = this@TextSecondView.textSize
            color = this@TextSecondView.textColor
            typeface = this@TextSecondView.textFont
        }

        settingTextSize()
    }

    private fun getTextFont(textFontId: Int): Typeface {
        return when(textFontId) {
            0 -> Typeface.defaultFromStyle(Typeface.NORMAL)
            else -> ResourcesCompat.getFont(context, textFontId)!!
        }
    }

    private fun settingTextSize() {
        paint.getTextBounds(text, 0, text.length, textBounds)

        textHeight = textBounds.height().toFloat()
        textWidth = textBounds.width().toFloat()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(widthMeasureSpec).toFloat()
            MeasureSpec.AT_MOST -> resolveSize((textWidth + paddingLeft + paddingRight).toInt(),
                widthMeasureSpec).toFloat()
            else -> textWidth + paddingLeft + paddingRight
        }

        val viewHeight = when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(heightMeasureSpec).toFloat()
            MeasureSpec.AT_MOST -> resolveSize((textHeight + paddingTop + paddingBottom).toInt(),
                heightMeasureSpec).toFloat()
            else -> textHeight + paddingTop + paddingBottom
        }

        setMeasuredDimension(viewWidth.toInt(), viewHeight.toInt())
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        textPositionX = (-textBounds.left).toFloat() + paddingLeft
        textPositionY = textHeight - textBounds.bottom + paddingTop
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawText(text, textPositionX, textPositionY, paint)
    }
}