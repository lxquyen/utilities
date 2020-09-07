package com.steve.utilities.presentation.circularprogressbar

import android.app.AlertDialog
import android.app.ProgressDialog
import com.steve.utilities.R
import com.steve.utilities.common.base.BaseFragment
import com.steve.utilities.common.base.BasePresenter
import com.steve.utilities.common.di.component.AppComponent
import kotlinx.android.synthetic.main.fragment_circular_progress_bar.*
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
        btnShow.setOnClickListener {
            AlertDialog.Builder(context)
                .setView(R.layout.dialog_content_view)
                .show()
        }
    }
}
