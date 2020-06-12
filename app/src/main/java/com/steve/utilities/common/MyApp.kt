package com.steve.utilities.common;

import android.app.Application
import com.steve.utilities.BuildConfig
import com.steve.utilities.common.di.component.AppComponent
import com.steve.utilities.common.di.component.DaggerAppComponent
import timber.log.Timber

class MyApp : Application() {
    companion object {
        lateinit var self: MyApp
            private set
    }

    val appComponent: AppComponent by lazy {
        return@lazy DaggerAppComponent.builder()
            .application(this)
            .baseUrl("http://5cef377a1c2baf00142cc649.mockapi.io/")
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        self = this
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}