package com.steve.utilities.common.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.animation.AccelerateDecelerateInterpolator
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

    private var circleProgressColor = Color.GREEN
    private var circleProgressRadius = 0f
    private var shouldHideProgress = false

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
        targetPulseRadius = context.dp2Px(40f).toInt()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        pulseAnimation.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        pulseAnimation.cancel()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        centerX = width / 2f
        centerY = height / 2f
    }

    override fun onDraw(canvas: Canvas?) {
        if(!shouldHideCircleBackground){
            drawBackgroundCircle(canvas)
            drawBackgroundCircleProgress(canvas)
            drawTarget(canvas)
        }

        super.onDraw(canvas)
    }

    private fun drawTarget(canvas: Canvas?) {
        if (targetCirclePulseAlpha > 0) {
            targetCirclePulsePaint.alpha = targetCirclePulseAlpha
            canvas?.drawCircle(centerX, centerY, targetCirclePulseRadius, targetCirclePulsePaint)
        }
    }

    private fun drawBackgroundCircle(canvas: Canvas?) {
        canvas?.drawCircle(centerX, centerY, circleRadius, circlePaint)
    }

    private fun drawBackgroundCircleProgress(canvas: Canvas?) {
        if (shouldHideProgress)
            return
        canvas?.drawCircle(centerX, centerY, circleProgressRadius, circleProgressPaint)
    }

    private fun delayedLerp(lerp: Float, threshold: Float): Float {
        return if (lerp < threshold) {
            0.0f
        } else (lerp - threshold) / (1.0f - threshold)
    }

    fun cancelPulseAnimation() {
        targetCirclePulseRadius = 0f
        pulseAnimation.cancel()
        invalidate()
    }

    fun startPulseAnimation() {
        pulseAnimation.start()
    }

    fun expandAnimation() {
        expandAnimation.start()
    }

    fun collapseAnimation() {
        shouldHideProgress = true
        expandAnimation.cancel()
        collapseAnimation.start()
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

    fun goneCircle() {

    }

    private val expandAnimation: ValueAnimator = FloatValueAnimatorBuilder()
        .duration(250)
        .delayBy(10)
        .interpolator(AccelerateDecelerateInterpolator())
        .onUpdate { lerpTime ->
            val pulseLerp: Float = delayedLerp(lerpTime, 0.5f)
            circleRadius = pulseLerp * circleRadiusMax
            invalidate()
        }
        .onEnd {}
        .build()

    private val collapseAnimation: ValueAnimator = FloatValueAnimatorBuilder()
        .duration(250)
        .delayBy(10)
        .interpolator(AccelerateDecelerateInterpolator())
        .onUpdate { lerpTime ->
            val pulseLerp: Float = delayedLerp(lerpTime, 0.5f)
            val tmp = pulseLerp * circleRadiusMax
            circleRadius -= tmp
            invalidate()
        }
        .onEnd {
            circleProgressRadius = 0f
            shouldHideProgress = false
        }
        .build()

    private val pulseAnimation: ValueAnimator = FloatValueAnimatorBuilder()
        .duration(1000)
        .repeat(ValueAnimator.INFINITE)
        .interpolator(AccelerateDecelerateInterpolator())
        .onUpdate { lerpTime ->
            val pulseLerp: Float = delayedLerp(lerpTime, 0.5f)
            targetCirclePulseRadius = (1.0f + pulseLerp) * 56
            targetCirclePulseAlpha = ((1.0f - pulseLerp) * 255).toInt()
            invalidate()
        }
        .build()

}