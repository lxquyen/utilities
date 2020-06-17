package com.steve.utilities.common.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Pair
import android.view.View
import com.steve.utilities.R

class SudokuBoardView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var lineColor = Color.BLACK

    private var cellWidth = 0f
    private var cellHeight = 0f

    private val linePaint: Paint by lazy {
        return@lazy Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
                color = lineColor
            }
    }

    init {
        setupAttrs(attrs)
    }

    private fun setupAttrs(attrs: AttributeSet?) {
        context.obtainStyledAttributes(attrs, R.styleable.SudokuBoardView)
            .let {
                lineColor = it.getColor(R.styleable.SudokuBoardView_lineColor, Color.BLACK)
                return@let it
            }
            .recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val min = widthSize.coerceAtMost(heightSize)

        cellWidth = (min - paddingLeft - paddingRight) / 9f
        cellHeight = (min - paddingTop - paddingBottom) / 9f

        super.onMeasure(MeasureSpec.makeMeasureSpec(min, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(min, MeasureSpec.EXACTLY))
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //Draw vertical lines
        (0..9).map {
            val x = it * cellWidth + paddingLeft
            val y = it * cellHeight + paddingTop
            return@map Pair.create(x, y)
        }.forEach {
            canvas?.drawLine(it, paddingTop.toFloat(), it, height.toFloat(), linePaint)
        }

    }


}