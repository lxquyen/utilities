package com.steve.utilities.common.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.steve.utilities.R
import com.steve.utilities.common.extensions.readGameBoards
import com.steve.utilities.core.extensions.Array2D
import com.steve.utilities.domain.model.Board
import com.steve.utilities.domain.model.Cell
import timber.log.Timber

class SudokuBoardView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    var listener: SudokuBoardViewListener? = null
    private var board = Board(context?.readGameBoards())

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
    private val warningPaint: Paint by lazy {
        return@lazy Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.RED
        }
    }
    //endregion

    private val steps = mutableListOf<Cell>()
    private val selectedCells = mutableListOf<Cell>()
    private var inputCell: Cell? = null
        set(value) {
            field = value
            field?.let {
                isDelete = false
            }
        }

    private var textSize = 0f
        set(value) {
            field = value
            numberPaint.textSize = value
        }

    //region# Blink : Warning
    private var warningCells = mutableListOf<Cell>()
    private var blink = false
        set(value) {
            field = value
            invalidate()
        }
    private var count = 0

    private val runnableBlink = object : Runnable {
        override fun run() {
            blink = !blink
            count++
            if (count == 4) {
                count = 0
                removeCallbacks(this)
                return
            }
            postDelayed(this, 350)
        }
    }
    //endregion

    private var isDelete = false
        set(value) {
            field = value
            listener?.onDeleteActionChanged(field)
        }

    private val textBound = Rect()

    private val gestureDetector: GestureDetector by lazy {
        return@lazy GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                val x = (0 until 9).firstOrNull { e.x > it * cellWidth && e.x < (it + 1) * cellWidth }
                    ?: -1
                val y = (0 until 9).firstOrNull { e.y > it * cellHeight && e.y < (it + 1) * cellHeight }
                    ?: -1
                val cell = board.matrix?.get(x, y) ?: return false

                if (cell.isEditable) {
                    if (isDelete) {
                        drawNumber(x, y, 0)
                        return false
                    }

                    inputCell?.let {
                        if (isValidate(x, y, it.value)) {
                            drawNumber(x, y, it.value)
                        } else {
                            showWaring(x, y, it.value)
                        }
                    }
                    return false
                }

                if (!cell.isEditable) {
                    listener?.onNumberUnEditableClicked(cell.value)
                    drawBackgroundStroke(cell)
                }
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

        //Draw Background waring
        if (blink) {
            warningCells.forEach { cell ->
                val left = cell.x * cellWidth + lineStrokeSecondary
                val top = cell.y * cellHeight + lineStrokeSecondary
                val right = left + cellWidth - lineStrokeSecondary * 2
                val bottom = top + cellHeight - lineStrokeSecondary * 2
                canvas?.drawRect(left, top, right, bottom, warningPaint)
            }
        }

        //Draw Background stroke
        run {
            selectedCells.forEach { cell ->
                val left = cell.x * cellWidth + lineStrokeSecondary
                val top = cell.y * cellHeight + lineStrokeSecondary
                val right = left + cellWidth - lineStrokeSecondary * 2
                val bottom = top + cellHeight - lineStrokeSecondary * 2
                canvas?.drawRect(left, top, right, bottom, selectUnEditablePaint)
            }
        }

        //Draw Number
        Array2D(9, 9) { row, col ->
            val cell = board.matrix?.get(row, col)
            val text = if (cell?.value != 0) cell?.value.toString() else ""
            numberPaint.getTextBounds(text, 0, text.length, textBound)
            val x = row * cellWidth + cellWidth / 2
            val y = col * cellHeight + (cellHeight + textBound.height()) / 2
            numberPaint.color = if (cell?.isEditable == true) textColorSecondary else textColorPrimary
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

    fun drawBackgroundStroke(cell: Cell?) {
        this.inputCell = cell
        selectedCells.clear()
        board.matrix?.forEachIndexed { _, _, c ->
            if (c != null && c.value == cell?.value) {
                selectedCells.add(c)
            }
        }
        invalidate()
    }

    fun drawNumber(x: Int, y: Int, number: Int, isUndo: Boolean = false) {
        Timber.d("drawNumber cell: $x, $y, $number, $isUndo")
        board.matrix?.get(x, y)
            ?.apply {
                value = number
            }?.let {
                if (!isUndo) addStep(x, y, number)
                if (!isDelete) selectedCells.add(it)
            }
        listener?.onBoardChanged(board.matrix)
        invalidate()
    }

    fun restart() {
        steps.clear()
        isDelete = false
        board.matrix?.forEach {
            if (it?.isEditable == true) it.value = 0
        }
        listener?.onStepsChanged(true)
        invalidate()
    }

    fun undo() {
        if (steps.isEmpty()) return
        isDelete = false

        val removedCell = steps.removeAt(steps.lastIndex)
        listener?.onStepsChanged(steps.isEmpty())

        val previousCell = steps.findLast { it.x == removedCell.x && it.y == removedCell.y }
            ?: Cell(removedCell).apply { value = 0 }

        drawNumber(previousCell.x, previousCell.y, previousCell.value, true)
    }

    fun clear() {
        isDelete = !isDelete
    }

    private fun showWaring(x: Int, y: Int, value: Int) {
        post(runnableBlink)
    }

    private fun isValidate(x: Int, y: Int, value: Int): Boolean {
        warningCells = board.findWarningItem(x, y, value)
        return warningCells.isEmpty()
    }

    private fun addStep(x: Int, y: Int, value: Int) {
        val cell = Cell().apply {
            this.x = x
            this.y = y
            this.value = value
        }
        steps.add(cell)
        listener?.onStepsChanged(steps.isEmpty())
    }

    interface SudokuBoardViewListener {
        fun onNumberUnEditableClicked(number: Int)
        fun onNumberEditableClicked(number: Int)
        fun onBoardChanged(matrix: Array2D<Cell?>?)
        fun onDeleteActionChanged(isDelete: Boolean)
        fun onStepsChanged(isEmpty: Boolean)
    }

}
