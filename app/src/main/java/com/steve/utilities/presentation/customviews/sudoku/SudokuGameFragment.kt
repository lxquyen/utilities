package com.steve.utilities.presentation.customviews.sudoku

import com.steve.utilities.R
import com.steve.utilities.common.base.BaseFragment
import com.steve.utilities.common.base.BasePresenter
import com.steve.utilities.common.di.component.AppComponent
import javax.inject.Inject

class SudokuGameFragment : BaseFragment<SudokuGameView, SudokuGamePresenter>(), SudokuGameView {

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

    }
}
