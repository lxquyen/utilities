/*
package com.steve.utilities.common.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.steve.utilities.R;

public class TwoCircularProgressView extends View {
    private static final int DEFAULT_ANIM_STEPS = 3;
    private static final int DEFAULT_ANIM_DURATION = 4000;
    private static final float DEFAULT_START_ANGLE = -90F;
    private static final float INDETERMINANT_MIN_SWEEP = 15F;

    private Paint paint = new Paint();
    private int size = 0;
    private RectF bounds = new RectF();
    private RectF bounds2 = new RectF();

    private float indeterminateSweep = 0F;
    private float indeterminateRotateOffset = 0F;

    private int thickness = 0;
    private int space = 0;
    private int animDuration = DEFAULT_ANIM_DURATION;
    private int animSteps = DEFAULT_ANIM_STEPS;
    private float startAngle = DEFAULT_START_ANGLE;
    private AnimatorSet indeterminateAnimator;

    private int[] gradientColors = new int[2];
    private float[] gradientPositions = new float[]{0 / 360F, 180 / 360F};


    public TwoCircularProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TwoCircularProgressView);
        thickness = a.getDimensionPixelSize(R.styleable.TwoCircularProgressView_tcpv_thickness,
                getResources().getDimensionPixelSize(R.dimen.cpv_default_thickness));
        space = a.getDimensionPixelSize(R.styleable.TwoCircularProgressView_tcpv_space,
                getResources().getDimensionPixelSize(R.dimen.cpv_default_space));
        int startColor = a.getColor(R.styleable.TwoCircularProgressView_tcpv_color_start, Color.MAGENTA);
        int endColor = a.getColor(R.styleable.TwoCircularProgressView_tcpv_color_end, Color.BLUE);
        gradientColors[0] = endColor;
        gradientColors[1] = startColor;
        a.recycle();

        Shader shader = new SweepGradient(size / 2f, size / 2f, gradientColors, gradientPositions);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(thickness);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setShader(shader);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int xPad = getPaddingLeft() + getPaddingRight();
        int yPad = getPaddingTop() + getPaddingBottom();

        int width = getMeasuredWidth() - xPad;
        int height = getMeasuredWidth() - yPad;

        size = Math.min(width, height);
        setMeasuredDimension(size + xPad, size + yPad);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        size = Math.min(w, h);
        updateBounds();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float startAngle = this.startAngle + indeterminateRotateOffset;
        canvas.drawArc(bounds, startAngle, indeterminateSweep, false, paint);
        canvas.drawArc(bounds2, startAngle * -1, indeterminateSweep * -1, false, paint);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAnimation();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimation();
    }

    @Override
    public void setVisibility(int visibility) {
        int currentVisibility = getVisibility();
        super.setVisibility(visibility);

        if (visibility != currentVisibility) {
            if (visibility == VISIBLE) {
                resetAnimation();
            } else {
                stopAnimation();
            }
        }
    }

    private void startAnimation() {
        resetAnimation();
    }

    private void stopAnimation() {
        if (indeterminateAnimator != null) {
            indeterminateAnimator.cancel();
        }
    }

    private void resetAnimation() {
        if (indeterminateAnimator != null && indeterminateAnimator.isRunning()) {
            indeterminateAnimator.cancel();
        }

        indeterminateSweep = INDETERMINANT_MIN_SWEEP;
        indeterminateAnimator = new AnimatorSet();

        AnimatorSet prevSet = null;
        AnimatorSet nextSet;

        for (int i = 0; i < animSteps; i++) {
            nextSet = createIndeterminateAnimator(i);
            AnimatorSet.Builder builder = indeterminateAnimator.play(nextSet);

            if (prevSet != null) {
                builder.after(prevSet);
            }
            prevSet = nextSet;
        }

        indeterminateAnimator.addListener(new Animator.AnimatorListener() {
            boolean wasCancelled = false;

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!wasCancelled) {
                    resetAnimation();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                wasCancelled = true;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        indeterminateAnimator.start();
    }

    private AnimatorSet createIndeterminateAnimator(int step) {
        final float maxSweep = 360f * (animSteps - 1) / animSteps + INDETERMINANT_MIN_SWEEP;
        final float start = -90f + step * (maxSweep - INDETERMINANT_MIN_SWEEP);

        ValueAnimator frontEndExtend = ValueAnimator.ofFloat(INDETERMINANT_MIN_SWEEP, maxSweep)
                .setDuration(animDuration / animSteps / 2L);
        frontEndExtend.setInterpolator(new DecelerateInterpolator(1F));
        frontEndExtend.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                indeterminateSweep = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        ValueAnimator rotateAnimator1 = ValueAnimator.ofFloat(step * 720f / animSteps, (step + .5F) * 720F / animSteps)
                .setDuration(animDuration / animSteps / 2L);
        rotateAnimator1.setInterpolator(new LinearInterpolator());
        rotateAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                indeterminateRotateOffset = (float) animation.getAnimatedValue();
            }
        });

        ValueAnimator backEndRetract = ValueAnimator.ofFloat(start, start + maxSweep - INDETERMINANT_MIN_SWEEP)
                .setDuration(animDuration / animSteps / 2L);
        backEndRetract.setInterpolator(new DecelerateInterpolator(1f));
        backEndRetract.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                startAngle = (float) animation.getAnimatedValue();
                indeterminateSweep = maxSweep - startAngle + start;
                invalidate();
            }
        });

        ValueAnimator rotateAnimator2 = ValueAnimator.ofFloat((step + .5f) * 720f / animSteps, (step + 1) * 720f / animSteps)
                .setDuration(animDuration / animSteps / 2L);
        rotateAnimator1.setInterpolator(new LinearInterpolator());
        rotateAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                indeterminateRotateOffset = (float) animation.getAnimatedValue();
            }
        });

        AnimatorSet set = new AnimatorSet();
        set.play(frontEndExtend).with(rotateAnimator1);
        set.play(backEndRetract).with(rotateAnimator2).after(rotateAnimator1);
        return set;
    }

    private void updateBounds() {
        bounds.set(
                getPaddingLeft() + thickness * 1F,
                getPaddingTop() + thickness * 1F,
                size - getPaddingLeft() - thickness * 1F,
                size - getPaddingBottom() - thickness * 1F
        );

        bounds2.set(
                getPaddingLeft() + thickness + space * 1F,
                getPaddingTop() + thickness + space * 1F,
                size - getPaddingLeft() - thickness - space * 1F,
                size - getPaddingBottom() - thickness - space * 1F
        );
    }

}
*/
