package com.steve.utilities.common.widget

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import com.steve.utilities.R

class TwoCircularProgressView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    companion object {
        private const val DEFAULT_ANIM_STEPS = 3
        private const val DEFAULT_ANIM_DURATION = 4000
        private const val DEFAULT_START_ANGLE = -90F
        private const val INDETERMINANT_MIN_SWEEP = 15F
    }

    private val paint = Paint()
    private var size = 0
    private var bounds = RectF()
    private var bounds2 = RectF()

    private var indeterminateSweep = 0F
    private var indeterminateRotateOffset = 0F

    private var thickness = 0
    private var space = 0
    private var animDuration = DEFAULT_ANIM_DURATION
    private var animSteps = DEFAULT_ANIM_STEPS
    private var startAngle = DEFAULT_START_ANGLE
    private var indeterminateAnimator: AnimatorSet? = null

    private val gradientColors = intArrayOf(Color.MAGENTA, Color.BLUE)
    private val gradientPositions = floatArrayOf(0 / 360f, 180 / 360f)

    init {
        initAttributes(attrs)
        updatePaint()
    }

    private fun initAttributes(attrs: AttributeSet?) {
        context?.obtainStyledAttributes(attrs, R.styleable.TwoCircularProgressView)
            ?.apply {
                thickness = this.getDimensionPixelSize(R.styleable.TwoCircularProgressView_tcpv_thickness,
                    resources.getDimensionPixelSize(R.dimen.cpv_default_thickness))
                space = this.getDimensionPixelSize(R.styleable.TwoCircularProgressView_tcpv_space,
                    resources.getDimensionPixelSize(R.dimen.cpv_default_space))
                val startColor = this.getColor(R.styleable.TwoCircularProgressView_tcpv_color_start, Color.MAGENTA)
                val endColor = this.getColor(R.styleable.TwoCircularProgressView_tcpv_color_end, Color.BLUE)
                gradientColors[1] = startColor
                gradientColors[0] = endColor
            }
            ?.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val xPad = paddingLeft + paddingRight
        val yPad = paddingTop + paddingBottom
        val width = measuredWidth - xPad
        val height = measuredHeight - yPad
        size = if (width < height) width else height
        setMeasuredDimension(size + xPad, size + yPad)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        size = if (w < h) w else h
        updateBounds()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawArc(bounds, startAngle + indeterminateRotateOffset, indeterminateSweep, false, paint)
        canvas?.drawArc(bounds2, (startAngle + indeterminateRotateOffset) * -1, indeterminateSweep * -1, false, paint)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnimation()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAnimation()
    }

    override fun setVisibility(visibility: Int) {
        val currentVisibility = this.visibility
        super.setVisibility(visibility)
        if (visibility != currentVisibility) {
            if (visibility == VISIBLE) {
                resetAnimation()
            } else {
                stopAnimation()
            }
        }
    }

    private fun startAnimation() {
        resetAnimation()
    }

    private fun stopAnimation() {
        indeterminateAnimator?.cancel()
    }

    private fun resetAnimation() {
        if (indeterminateAnimator?.isRunning == true) {
            indeterminateAnimator?.cancel()
        }

        indeterminateSweep = INDETERMINANT_MIN_SWEEP
        indeterminateAnimator = AnimatorSet()

        var prevSet: AnimatorSet? = null
        var nextSet: AnimatorSet?

        (0 until animSteps).forEach {
            nextSet = createIndeterminateAnimator(it)
            val builder = indeterminateAnimator?.play(nextSet)

            prevSet?.let {
                builder?.after(prevSet)
            }
            prevSet = nextSet
        }

        indeterminateAnimator?.addListener(object : Animator.AnimatorListener {
            var wasCancelled = false

            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                if (!wasCancelled) {
                    resetAnimation()
                }
            }

            override fun onAnimationCancel(animation: Animator?) {
                wasCancelled = true
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })
        indeterminateAnimator?.start()
    }

    private fun createIndeterminateAnimator(step: Int): AnimatorSet {
        val maxSweep = 360 * (animSteps - 1) / animSteps + INDETERMINANT_MIN_SWEEP
        val start = -90F + step * (maxSweep - INDETERMINANT_MIN_SWEEP)

        val frontEndExtend = ValueAnimator.ofFloat(INDETERMINANT_MIN_SWEEP, maxSweep)
            .apply {
                duration = animDuration / animSteps / 2L
                interpolator = DecelerateInterpolator(1F)
                addUpdateListener {
                    indeterminateSweep = it.animatedValue as Float
                    invalidate()
                }
            }

        val rotateAnimator1 = ValueAnimator.ofFloat(step * 720F / animSteps, (step + .5F) * 720F / animSteps)
            .apply {
                duration = animDuration / animSteps / 2L
                interpolator = LinearInterpolator()
                addUpdateListener {
                    indeterminateRotateOffset = it.animatedValue as Float
                }
            }

        val backEndRetract = ValueAnimator.ofFloat(start, start + maxSweep - INDETERMINANT_MIN_SWEEP)
            .apply {
                duration = animDuration / animSteps / 2L
                interpolator = DecelerateInterpolator(1F)
                addUpdateListener {
                    startAngle = it.animatedValue as Float
                    indeterminateSweep = maxSweep - startAngle + start
                    invalidate()
                }
            }

        val rotateAnimator2 = ValueAnimator.ofFloat((step + .5f) * 720f / animSteps, (step + 1) * 720f / animSteps)
            .apply {
                duration = animDuration / animSteps / 2L
                interpolator = LinearInterpolator()
                addUpdateListener {
                    indeterminateRotateOffset = it.animatedValue as Float
                }
            }

        val set = AnimatorSet()
        set.play(frontEndExtend).with(rotateAnimator1)
        set.play(backEndRetract).with(rotateAnimator2).after(rotateAnimator1)
        return set
    }

    private fun updateBounds() {
        bounds.set(
            paddingLeft + thickness * 1F,
            paddingTop + thickness * 1F,
            size - paddingLeft - thickness * 1F,
            size - paddingTop - thickness * 1F
        )

        bounds2.set(
            paddingLeft + thickness + space * 1F,
            paddingTop + thickness + space * 1F,
            size - paddingLeft - thickness - space * 1F,
            size - paddingTop - thickness - space * 1F
        )
    }

    private fun updatePaint() {
        paint.apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = thickness.toFloat()
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            val shader: Shader = SweepGradient(size / 2F, size / 2F, gradientColors, gradientPositions)
            paint.shader = shader
        }
    }

}
