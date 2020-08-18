package com.steve.utilities.presentation.service

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.steve.utilities.R
import com.steve.utilities.common.base.BaseFragment
import com.steve.utilities.common.base.BasePresenter
import com.steve.utilities.common.di.component.AppComponent
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_service_example.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ServiceExampleFragment : BaseFragment<ServiceExampleView, ServiceExamplePresenter>(), ServiceExampleView {

    @Inject
    lateinit var presenter: ServiceExamplePresenter
    private val serviceIntent: Intent by lazy {
        return@lazy Intent(context, MyService::class.java)
    }

    override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    override fun presenter(): BasePresenter<ServiceExampleView>? {
        return presenter
    }

    override fun viewIF(): ServiceExampleView? {
        return this
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_service_example
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initView() {
        btnStart.setOnClickListener {
            context?.startService(serviceIntent)
        }

        btnStop.setOnClickListener {
            context?.stopService(serviceIntent)
        }

        btnStartForeground.setOnClickListener {
            Observable.just(true)
                .delay(3, TimeUnit.SECONDS)
                .subscribe{
                    serviceIntent.apply {
                        action = MyService.ACTION_START_FOREGROUND_SERVICE
                    }
                    context?.startForegroundService(serviceIntent)
                }
        }

        btnStopForeground.setOnClickListener {
            serviceIntent.apply {
                action = MyService.ACTION_STOP_FOREGROUND_SERVICE
            }
            context?.startService(serviceIntent)
        }
    }
}
