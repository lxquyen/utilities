package com.steve.utilities.presentation.rxjava

import io.reactivex.subjects.PublishSubject

object RxJavaHelper {
    val memoryCache = mutableMapOf<String, Boolean>()
    private val publishSubject = PublishSubject.create<List<Item>>()

    fun get() = publishSubject
    fun request(items: List<Item>) {
        publishSubject.onNext(items)
    }

}