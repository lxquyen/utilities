package com.steve.utilities.presentation.rxjava

import com.steve.utilities.common.base.BasePresenter
import com.steve.utilities.core.extensions.addToCompositeDisposable
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RxJavaPresenter @Inject constructor() : BasePresenter<RxJavaView>() {
    val data = mutableListOf<Item>()

    init {
        (1..200).mapTo(data) {
            return@mapTo Item("$it", false)
        }

    }

    fun handle() {
        RxJavaHelper
            .get()
            .switchMap { items ->
                return@switchMap Observable.just(items).delay(500, TimeUnit.MILLISECONDS)
                    .doOnNext { it.forEach { item -> item.isActive = true } }

            }
            .subscribe {
                Timber.d("----> $it")
            }
            .addToCompositeDisposable(disposable)
    }
}