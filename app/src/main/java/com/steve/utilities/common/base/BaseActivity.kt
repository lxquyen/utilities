package com.steve.utilities.common.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    private var currentFragment: BaseFragment<*, *>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            initFragment()
        }
    }

    private fun initFragment() {
        currentFragment = injectFragment()

        currentFragment?.let {
            supportFragmentManager
                .beginTransaction()
                .replace(android.R.id.content, it)
                .commit()
        }
    }

    abstract fun injectFragment(): BaseFragment<*, *>

    override fun onBackPressed() {
        if (currentFragment?.onBackPressed() == true)
            return
        super.onBackPressed()
    }
}
