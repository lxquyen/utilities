package com.steve.utilities.common.base

interface BaseView {
    fun showProgressDialog(isShow: Boolean)
    fun showError(throwable: Throwable)
}