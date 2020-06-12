package com.steve.utilities.presentation;

import com.steve.utilities.common.base.BaseActivity
import com.steve.utilities.common.base.BaseFragment

class MainActivity : BaseActivity() {

    override fun injectFragment(): BaseFragment<*, *> {
        return MainFragment()
    }

}
