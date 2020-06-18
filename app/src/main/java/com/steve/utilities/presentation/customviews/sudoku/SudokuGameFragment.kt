package com.steve.utilities.presentation.customviews.sudoku

import android.view.View
import android.widget.TextView
import com.steve.utilities.R
import com.steve.utilities.common.base.BaseFragment
import com.steve.utilities.common.base.BasePresenter
import com.steve.utilities.common.di.component.AppComponent
import kotlinx.android.synthetic.main.fragment_sudoku_game.*
import javax.inject.Inject

class SudokuGameFragment : BaseFragment<SudokuGameView, SudokuGamePresenter>(), SudokuGameView, View.OnClickListener {

    private val buttonIds = mutableListOf(
        R.id.btn_one, R.id.btn_two, R.id.btn_three,
        R.id.btn_four, R.id.btn_five, R.id.btn_six,
        R.id.btn_seven, R.id.btn_eight, R.id.btn_nine)

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
        buttonIds.forEach { this@SudokuGameFragment.view?.findViewById<TextView>(it)?.setOnClickListener(this) }
    }

    override fun onClick(v: View) {
        buttonIds.forEach {
            val isSelected = it == v.id
            val button: TextView? = view?.findViewById(it)
            if (button?.isSelected == true && isSelected) {
                button.isSelected = false
                return@forEach
            }
            button?.isSelected = isSelected
        }
        toggleHighLightBoard()
    }

    private fun toggleHighLightBoard() {
        val button = buttonIds.map { view?.findViewById<TextView>(it) }.firstOrNull { it?.isSelected == true }
        boardView.highLightSelectedCell(button)
    }
}
