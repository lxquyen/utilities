package com.steve.utilities.common.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView

class SquareTextView(context: Context?, attrs: AttributeSet?) : TextView(context, attrs) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val min = widthSize.coerceAtMost(heightSize)

        super.onMeasure(MeasureSpec.makeMeasureSpec(min, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(min, MeasureSpec.EXACTLY))
    }
}