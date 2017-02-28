package com.hackill.demo;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.hackill.util.DisplayUtil;

/**
 * 半圆进度条
 * Created by hackill
 */

public class SemiCircleProgressView extends View {

    private static final String TAG = "SemiCircleProgressView";

    private Paint mProgressPaint;
    private Paint mProgressBgPaint;
    private RectF mBodyRectF = new RectF();

    float viewHeight = 0;
    float viewWidth = 0;
    private float progressX = 0;
    private float mProgress = 0;
    private float radiusWidth = 0;

    public SemiCircleProgressView(Context context) {
        this(context, null);
    }

    public SemiCircleProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SemiCircleProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = getDefaultSize(getSuggestedMinimumWidth(), heightMeasureSpec);

        viewHeight = height;
        viewWidth = width;
        initMeasure();
        setMeasuredDimension(width, height);
    }

    private void initMeasure() {
        float padding = radiusWidth / 2 + 1;

        mBodyRectF.left = padding;
        mBodyRectF.top = padding;
        mBodyRectF.right = viewWidth - padding;
        mBodyRectF.bottom = 2 * viewHeight - padding * 2;
        float minSize = mBodyRectF.width() > mBodyRectF.height() ? mBodyRectF.height() : mBodyRectF.width();
        mBodyRectF.top = padding;
        mBodyRectF.bottom = minSize;
        mBodyRectF.left = viewWidth / 2 - minSize / 2;
        mBodyRectF.right = viewWidth / 2 + minSize / 2;
    }

    private void initPaint() {

        int color = getContext().getResources().getColor(R.color.balance_ring);
        radiusWidth = DisplayUtil.dp2Px(getContext(), 13);

        mProgressBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressBgPaint.setColor(color);
        mProgressBgPaint.setStrokeWidth(radiusWidth);
        mProgressBgPaint.setStyle(Paint.Style.STROKE);
        mProgressBgPaint.setStrokeCap(Paint.Cap.ROUND);

        color = getContext().getResources().getColor(R.color.white);

        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setColor(color);
        mProgressPaint.setStrokeWidth(radiusWidth);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(mBodyRectF, 170, 200, false, mProgressBgPaint);
        canvas.drawArc(mBodyRectF, 170, progressX * 200, false, mProgressPaint);
    }

    public void startAutoAnimation() {
        smoothRefreshProgress();
    }

    public void stopAnimation() {
        invalidate();
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
            mAnimatorSet = null;
        }
    }

    public void setProgress(float percent) {
        if (percent > 1) percent = 1;
        if (percent < 0) percent = 0;
        this.mProgress = percent;
        startAutoAnimation();
    }

    private AnimatorSet mAnimatorSet = new AnimatorSet();

    private void smoothRefreshProgress() {

        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "progressX", progressX, mProgress).setDuration(1000);

        animator.setInterpolator(new DecelerateInterpolator());

        if (mAnimatorSet != null && mAnimatorSet.isRunning()) {
            mAnimatorSet.cancel();
            mAnimatorSet = null;
        }
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.play(animator);
        mAnimatorSet.start();
    }

    private void setProgressX(float progressX) {
        this.progressX = progressX;
        invalidate();
    }
}
