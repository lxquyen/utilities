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


class CircularProgressView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    companion object {
        private const val DEFAULT_ANIM_STEPS = 3
        private const val DEFAULT_ANIM_DURATION = 3
        private const val DEFAULT_ANIM_SWOOP_DURATION = 5000
        private const val DEFAULT_ANIM_SYNC_DURATION = 500
        private const val DEFAULT_START_ANGLE = -90
        private const val DEFAULT_MAX_PROGRESS = 100F
        private const val DEFAULT_PROGRESS = 0F
        private const val INDETERMINANT_MIN_SWEEP = 15F
    }

    private val paint = Paint()
    private var size = 0
    private var bounds = RectF()
    private var bounds2 = RectF()

    private var isIndeterminate = false
    private var autoStartAnimation = true

    private var currentProgress = DEFAULT_PROGRESS
    private var maxProgress = DEFAULT_MAX_PROGRESS
    private var indeterminateSweep = 0F
    private var indeterminateRotateOffset = 0F

    private var thickness = 0
    private var color = 0
    private var animDuration = DEFAULT_ANIM_DURATION
    private var animSwoopDuration = DEFAULT_ANIM_SWOOP_DURATION
    private var animSyncDuration = DEFAULT_ANIM_SYNC_DURATION
    private var animSteps = DEFAULT_ANIM_STEPS

    private var startAngle = 0F
    private var actualProgress = 0F

    private var startAngleRotate: ValueAnimator? = null
    private var progressAnimator: ValueAnimator? = null
    private var indeterminateAnimator: AnimatorSet? = null
    private var initialStartAngle = 0F


    private val gradientColors = intArrayOf(Color.MAGENTA, Color.BLUE)
    private val gradientPositions = floatArrayOf(0 / 360f, 310 / 360f)

    init {
        initAttributes(attrs)
        updatePaint()
    }

    private fun initAttributes(attrs: AttributeSet?) {
        context?.obtainStyledAttributes(attrs, R.styleable.CircularProgressView)
            ?.apply {
                // Initialize attributes from styleable attributes
                currentProgress = this.getFloat(R.styleable.CircularProgressView_cpv_progress,
                    resources.getInteger(R.integer.cpv_default_progress).toFloat())
                maxProgress = this.getFloat(R.styleable.CircularProgressView_cpv_maxProgress,
                    resources.getInteger(R.integer.cpv_default_max_progress).toFloat())
                thickness = this.getDimensionPixelSize(R.styleable.CircularProgressView_cpv_thickness,
                    resources.getDimensionPixelSize(R.dimen.cpv_default_thickness))
                isIndeterminate = this.getBoolean(R.styleable.CircularProgressView_cpv_indeterminate,
                    resources.getBoolean(R.bool.cpv_default_is_indeterminate))
                autoStartAnimation = this.getBoolean(R.styleable.CircularProgressView_cpv_animAutoStart,
                    resources.getBoolean(R.bool.cpv_default_anim_autostart))
                initialStartAngle = this.getFloat(R.styleable.CircularProgressView_cpv_startAngle,
                    resources.getInteger(R.integer.cpv_default_start_angle).toFloat())
                startAngle = initialStartAngle

                animDuration = this.getInteger(R.styleable.CircularProgressView_cpv_animDuration,
                    resources.getInteger(R.integer.cpv_default_anim_duration))
                animSwoopDuration = this.getInteger(R.styleable.CircularProgressView_cpv_animSwoopDuration,
                    resources.getInteger(R.integer.cpv_default_anim_swoop_duration))
                animSyncDuration = this.getInteger(R.styleable.CircularProgressView_cpv_animSyncDuration,
                    resources.getInteger(R.integer.cpv_default_anim_sync_duration))
                animSteps = this.getInteger(R.styleable.CircularProgressView_cpv_animSteps,
                    resources.getInteger(R.integer.cpv_default_anim_steps))
                color = this.getColor(R.styleable.CircularProgressView_cpv_color, Color.RED)
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

        val sweepAngle = if (isInEditMode)
            currentProgress / maxProgress * 360
        else
            actualProgress / maxProgress * 360

        if (isIndeterminate) {
            canvas?.drawArc(bounds, startAngle + indeterminateRotateOffset, indeterminateSweep, false, paint)
            canvas?.drawArc(bounds2, (startAngle + indeterminateRotateOffset) * -1, indeterminateSweep * -1, false, paint)
        } else {
            canvas?.drawArc(bounds, startAngle, sweepAngle, false, paint)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (autoStartAnimation) {
            startAnimation()
        }
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
        startAngleRotate?.cancel()
        progressAnimator?.cancel()
        indeterminateAnimator?.cancel()
    }

    private fun resetAnimation() {
        if (startAngleRotate?.isRunning == true) {
            startAngleRotate?.cancel()
        }

        if (progressAnimator?.isRunning == true) {
            progressAnimator?.cancel()
        }

        if (indeterminateAnimator?.isRunning == true) {
            indeterminateAnimator?.cancel()
        }

        if (isIndeterminate) {
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

        } else {
            startAngle = initialStartAngle
            startAngleRotate = ValueAnimator.ofFloat(startAngle, startAngle + 360)
                .apply {
                    duration = animSwoopDuration.toLong()
                    interpolator = DecelerateInterpolator()
                    addUpdateListener {
                        startAngle = it.animatedValue as Float
                        invalidate()
                    }
                }
            startAngleRotate?.start()

            actualProgress = 0F
            progressAnimator = ValueAnimator.ofFloat(actualProgress, currentProgress)
                .apply {
                    duration = animSyncDuration.toLong()
                    interpolator = LinearInterpolator()
                    addUpdateListener {
                        actualProgress = it.animatedValue as Float
                        invalidate()
                    }
                }
            progressAnimator?.start()
        }
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
            paddingLeft + thickness * 2.5F,
            paddingTop + thickness * 2.5F,
            size - paddingLeft - thickness * 2.5F,
            size - paddingTop - thickness * 2.5F
        )
    }

    private fun updatePaint() {
        paint.apply {
            isAntiAlias = true
            color = this@CircularProgressView.color
            style = Paint.Style.STROKE
            strokeWidth = thickness.toFloat()
            strokeCap = Paint.Cap.BUTT
            val shader: Shader = SweepGradient(size / 2F, size / 2F, gradientColors, gradientPositions)
            paint.shader = shader
        }
    }

}