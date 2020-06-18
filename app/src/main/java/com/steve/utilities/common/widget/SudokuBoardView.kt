package com.steve.utilities.common.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import com.steve.utilities.R
import com.steve.utilities.common.extensions.readGameBoards
import com.steve.utilities.core.extensions.Array2D
import com.steve.utilities.domain.model.Cell
import timber.log.Timber

class SudokuBoardView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var board = context?.readGameBoards()

    private var cellWidth = 0f
    private var cellHeight = 0f

    private var boardColor = 0
    private var backgroundSelectedColor = 0

    private var lineColorPrimary = 0
    private var lineColorSecondary = 0

    private var lineStrokePrimary = 0f
    private var lineStrokeSecondary = 0f

    private var textColorPrimary = 0
    private var textColorSecondary = 0

    //region #Paint
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
    private val selectUnEditablePaint: Paint by lazy {
        return@lazy Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 5f
        }
    }
    private val numberPaint: TextPaint by lazy {
        return@lazy TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            textAlign = Paint.Align.CENTER
        }
    }
    //endregion

    private val selectedPoint = Point(-1, -1)
    private val sameCells = mutableListOf<Cell>()
    private var textSize = 0f
        set(value) {
            field = value
            numberPaint.textSize = value
        }
    private val textBound = Rect()

    private val gestureDetector: GestureDetector by lazy {
        return@lazy GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                val x = (0 until 9).firstOrNull { e.x > it * cellWidth && e.x < (it + 1) * cellWidth }
                    ?: -1
                val y = (0 until 9).firstOrNull { e.y > it * cellHeight && e.y < (it + 1) * cellHeight }
                    ?: -1
                val cell = board?.get(x, y)
                if (cell?.isEditable == true)
                    updateSelectedPoint(x, y)
                else
                    findAllTheSameCell(cell)
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

                textColorPrimary = it.getColor(R.styleable.SudokuBoardView_textCellColorPrimary, Color.BLACK)
                textColorSecondary = it.getColor(R.styleable.SudokuBoardView_textCellColorSecondary, Color.GREEN)
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
        textSize = cellWidth * 0.6f

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
        //Draw Background of Same Cell
        run {
            sameCells.forEach { cell ->
                val left = cell.x * cellWidth + lineStrokeSecondary
                val top = cell.y * cellHeight + lineStrokeSecondary
                val right = left + cellWidth - lineStrokeSecondary * 2
                val bottom = top + cellHeight - lineStrokeSecondary * 2
                canvas?.drawRect(left, top, right, bottom, selectUnEditablePaint)
            }
        }

        //Draw Number
        Array2D(9, 9) { row, col ->
            val cell = board?.get(row, col)
            val text = if (cell?.value != 0) cell?.value.toString() else ""
            numberPaint.getTextBounds(text, 0, text.length, textBound)
            val x = row * cellWidth + cellWidth / 2
            val y = col * cellHeight + (cellHeight + textBound.height()) / 2
            numberPaint.color = if(cell?.isEditable == true) textColorSecondary else textColorPrimary
            canvas?.drawText(text, x, y, numberPaint)
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
        sameCells.clear()
        if (selectedPoint.x == x && selectedPoint.y == y) {
            selectedPoint.set(-1, -1)
            invalidate()
            return
        }
        selectedPoint.set(x, y)
        invalidate()
    }

    private fun findAllTheSameCell(cellInput: Cell?) {
        sameCells.clear()
        selectedPoint.set(-1, -1)
        board?.forEachIndexed { _, _, cell ->
            if (cell != null && cell.value == cellInput?.value) {
                sameCells.add(cell)
            }
        }
        invalidate()
    }

    fun highLightSelectedCell(button: TextView?) {
        if (button == null) {
            sameCells.clear()
            invalidate()
            return
        }
        val cell = Cell().apply {
            value = button.text.toString().toInt()
        }
        findAllTheSameCell(cell)
    }

    fun drawNumber(number: String?) {
        if (selectedPoint.isNull()) return
        board?.get(selectedPoint.x, selectedPoint.y)?.value = number?.toInt() ?: 0
        invalidate()
    }

    fun delete() {
        if (selectedPoint.isNull()) return
        board?.get(selectedPoint.x, selectedPoint.y)?.value = 0
        invalidate()
    }

    private fun Point.isNull(): Boolean {
        return this.x == -1 || this.y == -1
    }

}