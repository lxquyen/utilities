package com.steve.utilities.presentation

import android.view.View
import com.steve.utilities.R
import com.steve.utilities.common.base.BaseFragment
import com.steve.utilities.common.base.BasePresenter
import com.steve.utilities.common.di.component.AppComponent
import com.steve.utilities.common.extensions.startActivity
import com.steve.utilities.presentation.sudoku.play.SudokuGameFragment
import com.steve.utilities.presentation.sudoku.rank.RankFragment
import kotlinx.android.synthetic.main.fragment_main.*
import javax.inject.Inject

class MainFragment : BaseFragment<MainView, MainPresenter>(), MainView, View.OnClickListener {

    private val buttons = listOf(
        R.id.btn_custom_view,
        R.id.btn_rank
    )

    @Inject
    lateinit var presenter: MainPresenter

    override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    override fun presenter(): BasePresenter<MainView>? {
        return presenter
    }

    override fun viewIF(): MainView? {
        return this
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_main
    }

    override fun initView() {
        buttons.forEach { view?.findViewById<View>(it)?.setOnClickListener(this) }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_custom_view -> context?.startActivity(SudokuGameFragment::class.java)
            R.id.btn_rank -> context?.startActivity(RankFragment::class.java)
        }
    }
}
