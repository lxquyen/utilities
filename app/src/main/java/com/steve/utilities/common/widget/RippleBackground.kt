package com.steve.utilities.common.widget

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.RelativeLayout
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.StyleableRes
import androidx.core.content.ContextCompat
import com.steve.utilities.R

class RippleBackground : RelativeLayout {

    companion object {
        const val DEFAULT_RIPPLE_COUNT = 3
        const val DEFAULT_DURATION_TIME = 3000
        const val DEFAULT_SCALE = 6f
        const val DEFAULT_FILL_TYPE = 0
    }

    private var rippleColor = Color.BLACK
    var rippleStrokeWidth = 0f
    private var rippleRadius = 0f
    private var rippleDurationTime = DEFAULT_DURATION_TIME
    private var rippleAmount = 0
    private var rippleDelay = 0
    private var rippleScale = DEFAULT_SCALE
    private var rippleType = DEFAULT_FILL_TYPE

    private var animationRunning = false
    private var animatorList = mutableListOf<Animator>()
    private var rippleViewList = mutableListOf<RippleView>()

    private val paint: Paint by lazy {
        return@lazy Paint().apply {
            isAntiAlias = true
            style = if (rippleType == DEFAULT_FILL_TYPE) Paint.Style.FILL else Paint.Style.STROKE
            color = rippleColor
        }
    }

    private val animatorSet: AnimatorSet by lazy {
        return@lazy AnimatorSet().apply {
            interpolator = AccelerateInterpolator()
        }
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
        setup(attributeSet)
    }

    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(context, attributeSet, defStyle) {
        setup(attributeSet)
    }

    private fun setup(attrs: AttributeSet?) {
        if (isInEditMode) return

        if (attrs == null) {
            throw  IllegalArgumentException("Attributes should be provided to this view,")
        }

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RippleBackground)

        rippleColor = typedArray.getColorEx(context, R.styleable.RippleBackground_rb_color, R.color.colorAccent)
        rippleStrokeWidth = typedArray.getDimensionEx(R.styleable.RippleBackground_rb_strokeWidth, R.dimen.rippleStrokeWidth)
        rippleRadius = typedArray.getDimensionEx(R.styleable.RippleBackground_rb_radius, R.dimen.rippleRadius)
        rippleDurationTime = typedArray.getInt(R.styleable.RippleBackground_rb_duration, DEFAULT_DURATION_TIME)
        rippleAmount = typedArray.getInt(R.styleable.RippleBackground_rb_rippleAmount, DEFAULT_RIPPLE_COUNT)
        rippleScale = typedArray.getFloat(R.styleable.RippleBackground_rb_scale, DEFAULT_SCALE)
        rippleType = typedArray.getInt(R.styleable.RippleBackground_rb_type, DEFAULT_FILL_TYPE)

        typedArray.recycle()

        rippleDelay = rippleDurationTime / rippleAmount

        (0 until rippleAmount)
                .map { return@map Pair(it, RippleView(context)) }
                .forEach { (index, rippleView) ->
                    addView(rippleView)
                    val scaleXAnimator = ObjectAnimator.ofFloat(rippleView, "ScaleX", 1f, rippleScale)
                            .apply {
                                repeatCount = ObjectAnimator.INFINITE
                                repeatMode = ObjectAnimator.RESTART
                                startDelay = index * rippleDelay * 1L
                                duration = rippleDurationTime * 1L
                            }
                    animatorList.add(scaleXAnimator)

                    val scaleYAnimator = ObjectAnimator.ofFloat(rippleView, "ScaleY", 1f, rippleScale)
                            .apply {
                                repeatCount = ObjectAnimator.INFINITE
                                repeatMode = ObjectAnimator.RESTART
                                startDelay = index * rippleDelay * 1L
                                duration = rippleDurationTime * 1L
                            }
                    animatorList.add(scaleYAnimator)

                    val alphaAnimator = ObjectAnimator.ofFloat(rippleView, "Alpha", 1f, 0f)
                            .apply {
                                repeatCount = ObjectAnimator.INFINITE
                                repeatMode = ObjectAnimator.RESTART
                                startDelay = index * rippleDelay * 1L
                                duration = rippleDurationTime * 1L
                            }
                    animatorList.add(alphaAnimator)
                    rippleViewList.add(rippleView)
                }

        animatorSet.playTogether(animatorList)
    }

    inner class RippleView(context: Context?) : View(context) {
        init {
            this.visibility = INVISIBLE
        }

        override fun onDraw(canvas: Canvas?) {
            super.onDraw(canvas)
            val radius = (width.coerceAtMost(height)) / 2f
            canvas?.drawCircle(radius, radius, radius - rippleStrokeWidth, paint)
        }
    }

    fun on(view: View) {
        view.post {
            val paramsView = view.layoutParams
            rippleViewList
                    .forEach { it.layoutParams = paramsView }
        }
    }

    fun startRippleAnimation() {
        if (animationRunning)
            return
        animationRunning = true
        post {

            if(!animationRunning){
                return@post
            }

            rippleViewList.forEach { it.visibility = View.VISIBLE }
            animatorSet.start()
            visibility = View.VISIBLE
        }

    }

    fun stopRippleAnimation() {
        if (animationRunning) {
            visibility = View.GONE
            animationRunning = false
            rippleViewList.forEach { it.visibility = View.INVISIBLE }
            animatorSet.end()
        }
    }
}

fun TypedArray.getColorEx(context: Context, @StyleableRes id: Int, @ColorRes default: Int): Int {
    return this.getColor(id, ContextCompat.getColor(context, default))
}

fun TypedArray.getDimensionEx(@StyleableRes id: Int, @DimenRes default: Int): Float {
    return this.getDimension(id, resources.getDimension(default))
}

