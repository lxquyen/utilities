package com.steve.utilities.presentation.sudoku.rank

import com.steve.utilities.R
import com.steve.utilities.common.base.BaseFragment
import com.steve.utilities.common.base.BasePresenter
import com.steve.utilities.common.di.component.AppComponent
import kotlinx.android.synthetic.main.fragment_rank.*
import javax.inject.Inject

class RankFragment : BaseFragment<RankView, RankPresenter>(), RankView {

    @Inject
    lateinit var presenter: RankPresenter

    override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    override fun presenter(): BasePresenter<RankView>? {
        return presenter
    }

    override fun viewIF(): RankView? {
        return this
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_rank
    }

    override fun initView() {
        rankRV.apply {
            adapter = RankAdapter()
        }
    }
}
