package com.steve.utilities.common.widget

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.Region
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi

class VinTargetView(context: Context) : View(context) {
    companion object {
        fun with(activity: Activity): VinTargetView {
            val decor = activity.window.decorView as ViewGroup
            val layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
            val vinTargetView = VinTargetView(context = activity)
            decor.addView(vinTargetView)
            return vinTargetView
        }
    }

    var targetCirclePulseRadius = 0f
    var targetCirclePulseAlpha = 0
    var targetCircleRadius = 0f
    var targetCircleAlpha = 0

    var outRectTargetView: Rect? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        setBackgroundColor(Color.GREEN)
        outRectTargetView?.let {
            canvas?.clipRect(it, Region.Op.REPLACE)
        }
    }

    fun on(target: View) {
        target.getClipBounds(outRectTargetView)
        invalidate()
    }
}