package com.steve.utilities.presentation.sudoku.play

import android.os.SystemClock
import android.view.View
import android.widget.TextView
import com.steve.utilities.R
import com.steve.utilities.common.base.BaseFragment
import com.steve.utilities.common.base.BasePresenter
import com.steve.utilities.common.di.component.AppComponent
import com.steve.utilities.common.widget.SudokuBoardView
import com.steve.utilities.core.extensions.Array2D
import com.steve.utilities.domain.model.Cell
import kotlinx.android.synthetic.main.fragment_sudoku_game.*
import javax.inject.Inject

class SudokuGameFragment : BaseFragment<SudokuGameView, SudokuGamePresenter>(), SudokuGameView, View.OnClickListener, SudokuBoardView.SudokuBoardViewListener {

    private val buttonIds = mutableListOf(
        R.id.btn_one, R.id.btn_two, R.id.btn_three,
        R.id.btn_four, R.id.btn_five, R.id.btn_six,
        R.id.btn_seven, R.id.btn_eight, R.id.btn_nine)

    private val levelIds = mutableListOf(
        R.id.btn_easy, R.id.btn_medium, R.id.btn_hard, R.id.btn_very_hard
    )

    private val actionIds = mutableListOf(
        R.id.action_undo, R.id.action_restart, R.id.action_clear
    )

    @Inject
    lateinit var presenter: SudokuGamePresenter

    override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    override fun presenter(): BasePresenter<SudokuGameView>? {
        return presenter
    }

    override fun viewIF(): SudokuGameView? {
        return this
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_sudoku_game
    }

    override fun initView() {
        boardView.apply {
            listener = this@SudokuGameFragment
        }

        buttonIds.forEach { this@SudokuGameFragment.view?.findViewById<TextView>(it)?.setOnClickListener(this) }
        levelIds.forEach { this@SudokuGameFragment.view?.findViewById<TextView>(it)?.setOnClickListener(this) }
        actionIds.forEach { this@SudokuGameFragment.view?.findViewById<View>(it)?.setOnClickListener(this) }
    }

    override fun onClick(v: View) {
        buttonIds.forEach {
            val isSelected = it == v.id
            val button: TextView? = view?.findViewById(it)
            button?.isSelected = isSelected
        }

        when (v.id) {
            R.id.action_undo -> boardView.undo()
            R.id.action_restart -> boardView.restart()
            R.id.action_clear -> boardView.clear()
        }

        toggleHighLightBoard()
    }

    private fun toggleHighLightBoard() {
        val button = buttonIds.map { view?.findViewById<TextView>(it) }.firstOrNull { it?.isSelected == true }
        var cell: Cell? = null

        if (button != null) {
            cell = Cell().apply {
                value = button.text.toString().toIntOrNull() ?: 0
            }
        }
        boardView.drawBackgroundStroke(cell)
    }

    override fun onNumberUnEditableClicked(number: Int) {
        val btnId = buttonIds[number - 1]
        buttonIds.forEach {
            val isSelected = it == btnId
            val button: TextView? = view?.findViewById(it)
            button?.isSelected = isSelected
        }
    }

    override fun onNumberEditableClicked(number: Int) {
        buttonIds.forEach {
            val button: TextView? = view?.findViewById(it)
            button?.isSelected = false
        }
    }

    override fun onBoardChanged(matrix: Array2D<Cell?>?) {
        (1..9)
            .map {
                val sum = matrix?.sum { cell -> cell?.value == it }
                return@map sum == 9
            }
            .forEachIndexed { index, b ->
                view?.findViewById<TextView>(buttonIds[index])
                    ?.apply {
                        if (b) {
                            isEnabled = false
                            alpha = 0.1f
                        } else {
                            isEnabled = true
                            alpha = 1f
                        }
                    }
            }
    }

    override fun onDeleteActionChanged(isDelete: Boolean) {
        action_clear.isSelected = isDelete
    }

    override fun onStepsChanged(isEmpty: Boolean) {
        action_undo.apply {
            isClickable = !isEmpty
            alpha = if (isEmpty) 0.1f else 1f
        }
    }

    override fun onStartGameChanged(status: SudokuBoardView.StatusGame) {
        when (status) {
            SudokuBoardView.StatusGame.IDE -> {
                tv_time.base = SystemClock.elapsedRealtime()
                tv_time.stop()
            }
            SudokuBoardView.StatusGame.START -> {
                tv_time.base = SystemClock.elapsedRealtime()
                tv_time.start()
            }
            else -> {
                tv_time.stop()
            }
        }

    }
}
