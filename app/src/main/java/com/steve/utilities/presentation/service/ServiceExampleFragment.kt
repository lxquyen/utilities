package com.steve.utilities.presentation.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.steve.utilities.R
import com.steve.utilities.common.base.BaseFragment
import com.steve.utilities.common.base.BasePresenter
import com.steve.utilities.common.di.component.AppComponent
import com.steve.utilities.common.extensions.wakeupIfNeeded
import com.steve.utilities.presentation.BlurActivity
import com.steve.utilities.presentation.MainActivity
import com.steve.utilities.presentation.service.MyService.Companion.CHANNEL_DEFAULT
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
                .subscribe {
//                    activity?.wakeupIfNeeded()
//                    val intent = Intent(context, MainActivity::class.java)
//                    val pendingIntent = PendingIntent.getActivity(context!!, 1, intent, 0)
//                    val builder = NotificationCompat.Builder(context!!, CHANNEL_DEFAULT)
//                        .setContentTitle(getString(R.string.app_name))
//                        .setSmallIcon(R.drawable.ic_vcall)
//                        .setContentIntent(pendingIntent)
//                        .setOngoing(true)
//                        .setOnlyAlertOnce(true)
//                        .setCategory(Notification.CATEGORY_CALL)
//                        .setFullScreenIntent(pendingIntent, true)
//                        .setPriority(NotificationManager.IMPORTANCE_MAX)
//                    NotificationManagerCompat.from(context!!).notify(1, builder.build())
//                    MainActivity.start(context)
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
