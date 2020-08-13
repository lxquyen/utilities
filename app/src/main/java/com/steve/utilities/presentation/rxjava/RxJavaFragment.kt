package com.steve.utilities.presentation.rxjava

import android.widget.AbsListView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.steve.utilities.R
import com.steve.utilities.common.base.BaseFragment
import com.steve.utilities.common.base.BasePresenter
import com.steve.utilities.common.di.component.AppComponent
import kotlinx.android.synthetic.main.fragment_rx_java.*
import timber.log.Timber
import javax.inject.Inject

class RxJavaFragment : BaseFragment<RxJavaView, RxJavaPresenter>(), RxJavaView {

    @Inject
    lateinit var presenter: RxJavaPresenter
    private lateinit var rxJavaAdapter: RxJavaAdapter

    override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    override fun presenter(): BasePresenter<RxJavaView>? {
        return presenter
    }

    override fun viewIF(): RxJavaView? {
        return this
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_rx_java
    }

    override fun initView() {
        presenter.handle()
        rxJavaAdapter = RxJavaAdapter(presenter.data)
        rvRxJava.apply {
            adapter = rxJavaAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    val layoutManager = layoutManager as LinearLayoutManager
                    if (newState == 0) {
                        val first = layoutManager.findFirstVisibleItemPosition()

                        var end = layoutManager.findLastVisibleItemPosition()
                        if (end < presenter.data.size - 1) {
                            end += 1
                        }
                        val items = presenter.data.subList(first, end)
                        RxJavaHelper.request(items)
                        Timber.i("onScrollStateChanged: $first - $end")
                    }
                }
            })
        }
    }
}
