package com.steve.utilities.common.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.steve.utilities.R
import timber.log.Timber

class VinHolderLayout(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    companion object {
        private const val TAG = "VinHolderLayout"
    }

    var onVinHolderLayoutCallbackLister: OnVinHolderLayoutCallbackLister? = null

    private var leftTargetView: VinSwipeButtonView? = null
    private var rightTargetView: VinSwipeButtonView? = null

    private var initialTouchX = 0f
    private var initialTouchY = 0f

    override fun onFinishInflate() {
        super.onFinishInflate()
        rightTargetView = findViewById(R.id.imgAnswer)
        leftTargetView = findViewById(R.id.imgReject)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        val touchInsideLeft = leftTargetView?.touchInside(x, y) ?: false
        val touchInsideRight = rightTargetView?.touchInside(x, y) ?: false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                initialTouchX = x
                initialTouchY = y
                if (!touchInsideLeft && !touchInsideRight) {
                    Timber.i("on touch outside")
                    return false
                }

                if (touchInsideLeft) {
                    leftTargetView?.expandAnimation()
                }

                if (touchInsideRight) {
                    rightTargetView?.expandAnimation()
                }

                leftTargetView?.cancelPulseAnimation()
                rightTargetView?.cancelPulseAnimation()
            }

            MotionEvent.ACTION_UP -> {
                val isLeftSwiped = leftTargetView?.calculateSwiped()
                val isRightSwiped = rightTargetView?.calculateSwiped()
                if (isLeftSwiped == true) {
                    leftTargetView?.shouldHideCircleBackground = true
                    onVinHolderLayoutCallbackLister?.onLeftSwiped()
                    return true
                }

                if (isRightSwiped == true) {
                    rightTargetView?.shouldHideCircleBackground = true
                    onVinHolderLayoutCallbackLister?.onRightSwiped()
                    return true
                }

                rightTargetView?.startPulseAnimation()
                leftTargetView?.startPulseAnimation()
                leftTargetView?.collapseAnimation()
                rightTargetView?.collapseAnimation()

                if (touchInsideLeft) {
                    onVinHolderLayoutCallbackLister?.onLeftSwiped()
                    return true
                }

                if (touchInsideRight) {
                    onVinHolderLayoutCallbackLister?.onRightSwiped()
                    return true
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (leftTargetView?.touchInside(initialTouchX, initialTouchY) == true) {
                    val xDist = x - initialTouchX
                    val yDist = y - initialTouchY
                    leftTargetView?.setTranslation(xDist, yDist)
                }

                if (rightTargetView?.touchInside(initialTouchX, initialTouchY) == true) {
                    val xDist = x - initialTouchX
                    val yDist = y - initialTouchY
                    rightTargetView?.setTranslation(xDist, yDist)
                }
            }
        }
        return true
    }

    private fun View?.touchInside(x: Float, y: Float): Boolean {
        return this?.let {
            val outside = x < it.x || x > it.x + width
                    || y < it.y || y > it.y + height
            return !outside
        } ?: false
    }

    interface OnVinHolderLayoutCallbackLister {
        fun onLeftSwiped()

        fun onRightSwiped()

        fun onLeftClicked()

        fun onRightClicked()
    }
}