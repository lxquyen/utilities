package com.steve.utilities.presentation;

import android.os.Bundle
import com.steve.utilities.common.base.BaseActivity
import com.steve.utilities.common.base.BaseFragment
import com.steve.utilities.common.extensions.setFlagsShowWhenLocked
import com.steve.utilities.common.extensions.wakeupIfNeeded


class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFlagsShowWhenLocked()
        wakeupIfNeeded()
    }

    override fun injectFragment(): BaseFragment<*, *> {
        return MainFragment()
    }

}
