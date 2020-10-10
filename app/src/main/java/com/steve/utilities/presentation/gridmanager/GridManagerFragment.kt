package com.steve.utilities.presentation.gridmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.steve.utilities.R
import com.steve.utilities.common.base.BaseFragment
import com.steve.utilities.common.base.BasePresenter
import com.steve.utilities.common.di.component.AppComponent
import kotlinx.android.synthetic.main.fragment_grid_manager.*
import javax.inject.Inject


class GridManagerFragment : BaseFragment<GridManagerView, GridManagerPresenter>(), GridManagerView {

    @Inject
    lateinit var presenter: GridManagerPresenter

    override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    override fun presenter(): BasePresenter<GridManagerView>? {
        return presenter
    }

    override fun viewIF(): GridManagerView? {
        return this
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_grid_manager
    }

    override fun initView() {
        recyclerView.apply {
            adapter = GridAdapter()
            val spacingInPixels = resources.getDimensionPixelSize(R.dimen.grid_layout_margin)
            addItemDecoration(GridSpacingItemDecoration(2, spacingInPixels, true))
        }

    }

    inner class GridAdapter : RecyclerView.Adapter<GridViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_grid_manager, parent, false)
            view.layoutParams.width = 100
            return GridViewHolder(view)
        }

        override fun getItemCount(): Int {
            return 12
        }

        override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
            holder.bindData()
        }

    }

    inner class GridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                Toast.makeText(context, "$absoluteAdapterPosition", Toast.LENGTH_SHORT).show()
            }
        }

        fun bindData() {
            (itemView as TextView).text = "$absoluteAdapterPosition"
        }

    }
}
