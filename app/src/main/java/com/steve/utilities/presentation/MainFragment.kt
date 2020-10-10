package com.steve.utilities.presentation

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.steve.utilities.R
import com.steve.utilities.common.base.BaseFragment
import com.steve.utilities.common.base.BasePresenter
import com.steve.utilities.common.di.component.AppComponent
import com.steve.utilities.common.extensions.startActivity
import com.steve.utilities.core.extensions.inflate
import com.steve.utilities.presentation.audio.AudioFocusFragment
import com.steve.utilities.presentation.circularprogressbar.CircularProgressBarFragment
import com.steve.utilities.presentation.gridmanager.GridManagerFragment
import com.steve.utilities.presentation.ripple.RippleFragment
import com.steve.utilities.presentation.rxjava.RxJavaFragment
import com.steve.utilities.presentation.service.ServiceExampleFragment
import com.steve.utilities.presentation.sudoku.play.SudokuGameFragment
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.item_main.view.*
import javax.inject.Inject

class MainFragment : BaseFragment<MainView, MainPresenter>(), MainView {
    private val list = listOf(
        "Custom View",
        "RxJava",
        "Audio",
        "Service",
        "Circular Progress Bar",
        "Ripple Drawable",
        "Grid Manager"
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
        rvMain.apply {
            adapter = MainAdapter { index ->
                when (index) {
                    0 -> context?.startActivity(SudokuGameFragment::class.java)
                    1 -> context?.startActivity(RxJavaFragment::class.java)
                    2 -> context?.startActivity(AudioFocusFragment::class.java)
                    3 -> context?.startActivity(ServiceExampleFragment::class.java)
                    4 -> context?.startActivity(CircularProgressBarFragment::class.java)
                    5 -> context?.startActivity(RippleFragment::class.java)
                    6 -> context?.startActivity(GridManagerFragment::class.java)
                }
            }
        }
    }


    inner class MainAdapter(private val itemClick: (Int) -> Unit) : RecyclerView.Adapter<MainViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
            val view = parent.inflate(R.layout.item_main)
            return MainViewHolder(view, itemClick)
        }

        override fun getItemCount(): Int {
            return list.count()
        }

        override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
            holder.bindData()
        }

    }

    inner class MainViewHolder(view: View, itemClick: (Int) -> Unit) : RecyclerView.ViewHolder(view) {
        init {
            itemView.setOnClickListener {
                itemClick.invoke(absoluteAdapterPosition)
            }
        }

        fun bindData() {
            itemView.tvMain.text = list[absoluteAdapterPosition]
        }

    }

}
