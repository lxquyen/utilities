package com.steve.utilities.presentation;

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.steve.utilities.common.base.BaseActivity
import com.steve.utilities.common.base.BaseFragment
import com.steve.utilities.common.extensions.setFlagsShowWhenLocked
import com.steve.utilities.common.extensions.wakeupIfNeeded


class MainActivity : BaseActivity() {
    companion object {
        fun start(context: Context?) {
            val intent = Intent(context, MainActivity::class.java)
            context?.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFlagsShowWhenLocked()
        wakeupIfNeeded()
    }

    override fun injectFragment(): BaseFragment<*, *> {
        return MainFragment()
    }

}
