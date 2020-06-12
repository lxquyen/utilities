package com.steve.utilities.common.base;

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.steve.utilities.common.MyApp
import com.steve.utilities.common.di.component.AppComponent

abstract class BaseFragment<V : BaseView?, P : BasePresenter<V>?> : Fragment(),
    BaseView {
    private val idLayoutRes: Int
        get() {
            return getLayoutRes()
        }

    private val basePresenter: BasePresenter<V>?
        get() {
            return presenter()
        }

    abstract fun inject(appComponent: AppComponent)
    abstract fun getLayoutRes(): Int
    abstract fun initView()
    abstract fun presenter(): BasePresenter<V>?
    abstract fun viewIF(): V?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject(MyApp.self.appComponent)
        initPresenter()
    }

    private fun initPresenter() {
        basePresenter?.let {
            it.view = viewIF()
            lifecycle.addObserver(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(idLayoutRes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun showProgressDialog(isShow: Boolean) {
    }

    override fun showError(throwable: Throwable) {

    }

    fun onBackPressed(): Boolean {
        return false
    }
}