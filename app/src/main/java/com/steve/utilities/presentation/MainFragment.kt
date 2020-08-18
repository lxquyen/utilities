package com.steve.utilities.presentation

import android.view.View
import com.steve.utilities.R
import com.steve.utilities.common.base.BaseFragment
import com.steve.utilities.common.base.BasePresenter
import com.steve.utilities.common.di.component.AppComponent
import com.steve.utilities.common.extensions.startActivity
import com.steve.utilities.presentation.audio.AudioFocusFragment
import com.steve.utilities.presentation.rxjava.RxJavaFragment
import com.steve.utilities.presentation.service.ServiceExampleFragment
import com.steve.utilities.presentation.sudoku.play.SudokuGameFragment
import com.steve.utilities.presentation.sudoku.rank.RankFragment
import kotlinx.android.synthetic.main.fragment_main.*
import javax.inject.Inject

class MainFragment : BaseFragment<MainView, MainPresenter>(), MainView, View.OnClickListener {

    private val buttons = listOf(
        R.id.btn_custom_view,
        R.id.btn_rank,
        R.id.btn_rxjava,
        R.id.btn_audio,
        R.id.btn_service
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
            R.id.btn_rxjava -> context?.startActivity(RxJavaFragment::class.java)
            R.id.btn_audio -> context?.startActivity(AudioFocusFragment::class.java)
            R.id.btn_service -> context?.startActivity(ServiceExampleFragment::class.java)
        }
    }
}
