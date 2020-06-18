package com.steve.utilities.presentation.customviews.sudoku

import android.view.View
import android.widget.TextView
import com.steve.utilities.R
import com.steve.utilities.common.base.BaseFragment
import com.steve.utilities.common.base.BasePresenter
import com.steve.utilities.common.di.component.AppComponent
import com.steve.utilities.common.widget.SudokuBoardView
import kotlinx.android.synthetic.main.fragment_sudoku_game.*
import javax.inject.Inject

class SudokuGameFragment : BaseFragment<SudokuGameView, SudokuGamePresenter>(), SudokuGameView, View.OnClickListener, SudokuBoardView.SudokuBoardViewListener {

    private val buttonIds = mutableListOf(
        R.id.btn_one, R.id.btn_two, R.id.btn_three,
        R.id.btn_four, R.id.btn_five, R.id.btn_six,
        R.id.btn_seven, R.id.btn_eight, R.id.btn_nine)

    private val keyIds = mutableListOf(
        R.id.key_one, R.id.key_two, R.id.key_three,
        R.id.key_four, R.id.key_five, R.id.key_six,
        R.id.key_seven, R.id.key_eight, R.id.key_nine
    )

    private val levelIds = mutableListOf(
        R.id.btn_easy, R.id.btn_medium, R.id.btn_hard, R.id.btn_very_hard
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
        keyIds.forEach { this@SudokuGameFragment.view?.findViewById<TextView>(it)?.setOnClickListener(this) }
        levelIds.forEach { this@SudokuGameFragment.view?.findViewById<TextView>(it)?.setOnClickListener(this) }
        key_restart.setOnClickListener(this)
        key_delete.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        buttonIds.forEach {
            val isSelected = it == v.id
            val button: TextView? = view?.findViewById(it)
            button?.isSelected = isSelected
        }

        when (v.id) {
            R.id.key_restart -> restart()
            R.id.key_delete -> boardView.delete()
        }

        keyIds.forEach {
            if (it == v.id) {
                val numberTV = view?.findViewById<TextView>(it)
                boardView.drawNumber(numberTV?.text?.toString())
            }
        }
        toggleHighLightBoard()
    }

    private fun restart() {
        boardView.restart()
    }

    private fun toggleHighLightBoard() {
        val button = buttonIds.map { view?.findViewById<TextView>(it) }.firstOrNull { it?.isSelected == true }
        boardView.highLightSelectedCell(button)
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
}
