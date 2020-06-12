package com.steve.utilities.presentation

import android.app.Application
import android.content.Context
import com.steve.utilities.R
import com.steve.utilities.common.base.BaseFragment
import com.steve.utilities.common.base.BasePresenter
import com.steve.utilities.common.di.component.AppComponent
import kotlinx.android.synthetic.main.fragment_main.*
import javax.inject.Inject

class MainFragment : BaseFragment<MainView, MainPresenter>(), MainView {

    @Inject
    lateinit var presenter: MainPresenter

    @Inject
    lateinit var applicationContext : Application

    @Inject
    lateinit var baseUrl: String

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
        tv.text = baseUrl
    }
}
