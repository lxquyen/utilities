package com.steve.utilities.presentation.circularprogressbar

import com.steve.utilities.R
import com.steve.utilities.common.base.BaseFragment
import com.steve.utilities.common.base.BasePresenter
import com.steve.utilities.common.di.component.AppComponent
import javax.inject.Inject

class CircularProgressBarFragment : BaseFragment<CircularProgressBarView, CircularProgressBarPresenter>(), CircularProgressBarView {

    @Inject
    lateinit var presenter: CircularProgressBarPresenter

    override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    override fun presenter(): BasePresenter<CircularProgressBarView>? {
        return presenter
    }

    override fun viewIF(): CircularProgressBarView? {
        return this
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_circular_progress_bar
    }

    override fun initView() {

    }
}
