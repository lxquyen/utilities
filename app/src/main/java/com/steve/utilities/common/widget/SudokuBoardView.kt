package com.steve.utilities.common.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.util.AttributeSet
import android.view.GestureDetector
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

    private val selectedPoint = Point(-1, -1)

    private val gestureDetector: GestureDetector by lazy {
        return@lazy GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                val x = (0 until 9).firstOrNull { e.x > it * cellWidth && e.x < (it + 1) * cellWidth }
                    ?: -1
                val y = (0 until 9).firstOrNull { e.y > it * cellHeight && e.y < (it + 1) * cellHeight }
                    ?: -1
                updateSelectedPoint(x, y)
                return false
            }
        })
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
        gestureDetector.onTouchEvent(event)
        return true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //Draw boardBackground
        canvas?.drawRect(cellWidth * 3, 0f, cellWidth * 6, cellHeight * 3, boardPaint)
        canvas?.drawRect(0f, cellWidth * 3, cellWidth * 3, cellHeight * 6, boardPaint)
        canvas?.drawRect(cellWidth * 6, cellWidth * 3, cellWidth * 12, cellHeight * 6, boardPaint)
        canvas?.drawRect(cellWidth * 3, cellWidth * 6, cellWidth * 6, cellHeight * 12, boardPaint)

        //Draw Selected background
        run {
            val x = selectedPoint.x
            val y = selectedPoint.y
            if (x != -1 && y != -1) {
                val left = x * cellWidth
                val top = y * cellHeight
                val right = left + cellWidth
                val bottom = top + cellHeight
                canvas?.drawRect(left, top, right, bottom, selectPaint)
            }
        }

        //Draw lines
        linePaint.apply {
            strokeWidth = lineStrokeSecondary
            color = lineColorSecondary
        }
        (1 until 9).forEach {
            val x = it * cellWidth + paddingLeft
            val y = it * cellHeight + paddingTop
            canvas?.drawLine(x, 0f, x, height.toFloat(), linePaint)
            canvas?.drawLine(0f, y, width.toFloat(), y, linePaint)
        }

        //Draw border
        linePaint.apply {
            strokeWidth = lineStrokePrimary
            color = lineColorPrimary
            style = Paint.Style.STROKE
        }
        (0..9).filter { it % 3 == 0 }.forEach {
            val x = it * cellWidth + paddingLeft
            val y = it * cellHeight + paddingTop
            canvas?.drawLine(x, 0f, x, height.toFloat(), linePaint)
            canvas?.drawLine(0f, y, width.toFloat(), y, linePaint)
        }
    }

    private fun updateSelectedPoint(x: Int, y: Int) {
        if (selectedPoint.x == x && selectedPoint.y == y) {
            selectedPoint.set(-1, -1)
            invalidate()
            return
        }
        selectedPoint.set(x, y)
        invalidate()
    }

}