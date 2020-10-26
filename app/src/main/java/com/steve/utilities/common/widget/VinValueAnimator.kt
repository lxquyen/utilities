package com.steve.utilities.common.widget

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.view.animation.LinearInterpolator
import androidx.core.animation.addListener
import com.steve.utilities.common.extensions.dp2Px

class VinValueAnimator(private val context: Context?) {
    companion object {
        fun test(context: Context) {
            val vinValueAnimator = VinValueAnimator(context)
                .apply {
                    addAnimator(80f, 128f, 350 - 0)
                    addAnimator(128f, 128f, 700 - 350)
                    addAnimator(128f, 118f, 1050 - 700)
                    addAnimator(118f, 128f, 1400 - 1050)
                    addAnimator(128f, 128f, 1750 - 1400)
                    addAnimator(128f, 118f, 2100 - 1750)
                    addAnimator(118f, 80f, 2483 - 2100)
                }
            vinValueAnimator.start()
        }
    }

    private val animatorSet = AnimatorSet()
    private val animators = mutableListOf<Animator>()
    var updateValueListener: ((Float) -> Unit)? = null

    fun addAnimator(from: Float, to: Float, duration: Long) {
        val animator = ValueAnimator.ofFloat(context.dp2Px(from), context.dp2Px(to))
            .apply {
                this.duration = duration
                this.interpolator = LinearInterpolator()
                this.addUpdateListener {
                    updateValueListener?.invoke(it.animatedValue as Float)
                }
            }

        animators.add(animator)
    }

    fun cancel(){
        animatorSet.cancel()
    }

    fun start() {
        animatorSet.playSequentially(animators)
        animatorSet.start()
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                animatorSet.start()
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })
    }

}