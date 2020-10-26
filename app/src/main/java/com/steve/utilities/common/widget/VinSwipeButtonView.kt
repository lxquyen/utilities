package com.steve.utilities.common.widget

import android.animation.ValueAnimator
import android.animation.ValueAnimator.REVERSE
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import com.steve.utilities.R
import com.steve.utilities.common.extensions.dp2Px
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class VinSwipeButtonView(context: Context?, attrs: AttributeSet?) : ImageView(context, attrs) {
    companion object {
        private const val THREAD_HOLD = 10
    }

    private val circlePaint: Paint by lazy {
        return@lazy Paint().apply {
            isAntiAlias = true
            color = circleColor
        }
    }
    private val circleProgressPaint: Paint by lazy {
        return@lazy Paint().apply {
            isAntiAlias = true
            color = circleProgressColor
        }
    }

    private var centerX = 0f
    private var centerY = 0f
    var shouldHideCircleBackground = false

    private var circleColor = Color.RED
    private var circleRadius = 0f
    private var circleRadiusMax = 0f
    private var circleRadiusHint = 0f

    private var circleProgressColor = Color.GREEN
    private var circleProgressRadius = 0f

    private var targetCircleColor = Color.GREEN
    private var targetPulseRadius = 0
    private var targetCirclePulseRadius = 0f
    private var targetCirclePulseAlpha = 0
    private val targetCirclePulsePaint: Paint by lazy {
        return@lazy Paint().apply {
            isAntiAlias = true
            color = targetCircleColor
        }
    }

    init {
        context?.obtainStyledAttributes(attrs, R.styleable.VinSwipeButtonView)
            ?.apply {
                circleColor = this.getColor(R.styleable.VinSwipeButtonView_vin_circleColor, Color.RED)
                circleProgressColor = this.getColor(R.styleable.VinSwipeButtonView_vin_circleProgressColor, Color.GREEN)
                targetCircleColor = this.getColor(R.styleable.VinSwipeButtonView_vin_targetCircleColor, Color.GREEN)
            }
            ?.recycle()
        circleRadiusMax = context.dp2Px(150f)
        circleRadiusHint = context.dp2Px(55f)
        circleRadius = context.dp2Px(25f)
        targetPulseRadius = context.dp2Px(40f).toInt()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        startHintAnimation()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        centerX = width / 2f
        centerY = height / 2f
    }

    override fun onDraw(canvas: Canvas?) {
        if (!shouldHideCircleBackground) {
            drawBackgroundCircle(canvas)
            drawBackgroundCircleProgress(canvas)
        }

        super.onDraw(canvas)
    }

    private fun drawBackgroundCircle(canvas: Canvas?) {
        canvas?.drawCircle(centerX, centerY, circleRadius, circlePaint)
    }

    private fun drawBackgroundCircleProgress(canvas: Canvas?) {
        canvas?.drawCircle(centerX, centerY, circleProgressRadius, circleProgressPaint)
    }

    private fun delayedLerp(lerp: Float, threshold: Float): Float {
        return if (lerp < threshold) {
            0.0f
        } else (lerp - threshold) / (1.0f - threshold)
    }

    fun stopHintAnimation(shouldHideCircleBackground: Boolean = false) {
        hintAnimation.cancel()
        hintProgressAnimation.cancel()
        this.shouldHideCircleBackground = shouldHideCircleBackground
        invalidate()
    }

    fun startHintAnimation() {
        hintAnimation.start()
        hintProgressAnimation.start()
    }

    fun expandAnimation() {
        expandAnimation.start()
        stopHintAnimation()
    }

    fun collapseAnimation() {
        shouldHideCircleBackground = false
        expandAnimation.reverse()
    }

    fun setTranslation(xDist: Float, yDist: Float) {
        val radiusX = abs(xDist - centerX)
        val radiusY = abs(yDist - centerY)
        val radius = max(radiusX, radiusY)
        circleProgressRadius = min(radius, circleRadiusMax)
        invalidate()
    }

    fun handleActionUp() {

    }

    fun calculateSwiped(): Boolean {
        return circleProgressRadius >= circleRadiusMax - THREAD_HOLD
    }

    private val hintAnimation: ValueAnimator = ValueAnimator.ofFloat(0.5f, 1.0f, 0.85f, 1.0f, 0.6f)
        .apply {
            duration = 2000
            repeatCount = -1
            repeatMode = REVERSE
            interpolator = LinearInterpolator()
            addUpdateListener {
                val value = (it.animatedValue as? Float) ?: 0f
                circleRadius = value * circleRadiusHint
                invalidate()
            }
        }

    private val hintProgressAnimation: ValueAnimator = ValueAnimator.ofFloat(0.5f, 0.95f, 0.55f, 0.95f, 0.55f)
        .apply {
            duration = 2000
            repeatCount = -1
            repeatMode = REVERSE
            interpolator = LinearInterpolator()
            addUpdateListener {
                val value = (it.animatedValue as? Float) ?: 0f
                circleProgressRadius = value * circleRadiusHint
                invalidate()
            }
        }

    private val expandAnimation: ValueAnimator = ValueAnimator.ofFloat(0.5f, 1.0f)
        .apply {
            duration = 250
            interpolator = LinearInterpolator()
            addUpdateListener {
                val value = (it.animatedValue as? Float) ?: 0f
                circleRadius = value * circleRadiusMax
                invalidate()
            }
        }
}