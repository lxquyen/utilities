package com.steve.utilities.common.extensions

import android.content.Context
import com.steve.utilities.common.base.SubActivity

fun Context?.startActivity(fragment: Class<*>) {
    SubActivity.start(this, fragment)
}