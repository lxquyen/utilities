package com.steve.utilities.presentation.ripple

import android.view.View
import com.steve.utilities.R
import com.steve.utilities.common.base.BaseFragment
import com.steve.utilities.common.base.BasePresenter
import com.steve.utilities.common.di.component.AppComponent
import kotlinx.android.synthetic.main.fragment_ripple.*
import javax.inject.Inject

class RippleFragment : BaseFragment<RippleView, RipplePresenter>(), RippleView, View.OnClickListener {

    @Inject
    lateinit var presenter: RipplePresenter

    override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    override fun presenter(): BasePresenter<RippleView>? {
        return presenter
    }

    override fun viewIF(): RippleView? {
        return this
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_ripple
    }

    override fun initView() {
        tvTap.setOnClickListener(this)
        tvTap2.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

    }
}
