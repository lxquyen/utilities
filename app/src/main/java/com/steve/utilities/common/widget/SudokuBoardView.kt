package com.steve.utilities.common.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.steve.utilities.R

class SudokuBoardView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var cellWidth = 0f
    private var cellHeight = 0f

    private var boardColor = 0
    private var backgroundSelectedColor = 0

    private var lineColorPrimary = 0
    private var lineColorSecondary = 0

    private var lineStrokePrimary = 0f
    private var lineStrokeSecondary = 0f

    private val linePaint: Paint by lazy {
        return@lazy Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
                color = lineColorPrimary
            }
    }
    private val boardPaint: Paint by lazy {
        return@lazy Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = boardColor
        }
    }
    private val selectPaint: Paint by lazy {
        return@lazy Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.YELLOW
        }
    }

    init {
        setupAttrs(attrs)
    }

    private fun setupAttrs(attrs: AttributeSet?) {
        context.obtainStyledAttributes(attrs, R.styleable.SudokuBoardView)
            .let {
                boardColor = it.getColor(R.styleable.SudokuBoardView_boardColor, Color.WHITE)
                backgroundSelectedColor = it.getColor(R.styleable.SudokuBoardView_backgroundSelectedColor, Color.YELLOW)
                lineColorPrimary = it.getColor(R.styleable.SudokuBoardView_lineColorPrimary, Color.BLUE)
                lineColorSecondary = it.getColor(R.styleable.SudokuBoardView_lineColorSecondary, Color.BLACK)
                lineStrokePrimary = it.getDimension(R.styleable.SudokuBoardView_lineStrokePrimary, 10f)
                lineStrokeSecondary = it.getDimension(R.styleable.SudokuBoardView_lineStrokeSecondary, 5f)
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //Draw boardBackground
        canvas?.drawRect(cellWidth * 3, 0f, cellWidth * 6, cellHeight * 3, boardPaint)
        canvas?.drawRect(0f, cellWidth * 3, cellWidth * 3, cellHeight * 6, boardPaint)
        canvas?.drawRect(cellWidth * 6, cellWidth * 3, cellWidth * 12, cellHeight * 6, boardPaint)
        canvas?.drawRect(cellWidth * 3, cellWidth * 6, cellWidth * 6, cellHeight * 12, boardPaint)

        //Draw lines
        (0..9).forEach {
            val x = it * cellWidth + paddingLeft
            val y = it * cellHeight + paddingTop
            canvas?.save()

            canvas?.restore()

            linePaint.apply {
                color = if (it % 3 == 0) lineColorPrimary else lineColorSecondary
                strokeWidth = if (it % 3 == 0) lineStrokePrimary else lineStrokeSecondary
            }

            canvas?.drawLine(x, 0f, x, height.toFloat(), linePaint)
            canvas?.drawLine(0f, y, width.toFloat(), y, linePaint)


        }

        //Draw Selected background
        val bgSelectedX = cellWidth * 0f + lineStrokeSecondary
        val bgSelectedY = cellHeight * 0f + lineStrokeSecondary
        val bgSelectedWidth = cellWidth - lineStrokeSecondary
        val bgSelectedHeight = cellHeight - lineStrokeSecondary

        canvas?.drawRect(bgSelectedX, bgSelectedY, bgSelectedWidth, bgSelectedHeight, selectPaint)

    }


}