package com.hackill.demo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * @author hackill
 */
public class SearchView extends View {
    public static final String TAG = SearchView.class.getSimpleName();

    private Paint mPiePaint;
    private Paint mWarePiePaint;
    private Paint mRingPiePaint;

    private int viewWith = 0;
    private int viewHeight = 0;
    private int viewSize = 0;
    private Point centerPoint = new Point();
    private AnimatorModel currentModel = AnimatorModel.STOP;
    private Handler handler = new Handler(Looper.getMainLooper());

    // 最大波浪半径
    float maxWareRadius = 0;
    // 当前波浪半径
    float currentWareRadius = 0;
    // 中心圆半径
    float radius = 0;
    // 搜索动画半径
    float searchRadius = 0;
    // 画笔透明度
    private float paintAlpha = 0;
    // 搜索动画圆圈宽度
    private float mStrokeWidth = 5;

    public SearchView(Context context) {
        this(context, null);
    }

    public SearchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = getDefaultSize(getSuggestedMinimumWidth(), heightMeasureSpec);

        int size = Math.min(width, height);

        viewWith = width;
        viewHeight = height;
        viewSize = size;

        centerPoint.set(viewWith / 2, viewHeight / 2);

        radius = viewSize / 3;
        maxWareRadius = viewSize / 2;

        mStrokeWidth = size * 0.05f;

        initPaint();

        setMeasuredDimension(size, size);
    }

    private void initPaint() {

        int color = getContext().getResources().getColor(R.color.wave_yellow);
        mWarePiePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWarePiePaint.setColor(color);
        mWarePiePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        color = getContext().getResources().getColor(R.color.text_color_yellow);
        mPiePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPiePaint.setColor(color);
        mPiePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        color = getContext().getResources().getColor(R.color.white);
        mRingPiePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRingPiePaint.setColor(color);
        mRingPiePaint.setStyle(Paint.Style.STROKE);
        mRingPiePaint.setStrokeWidth(mStrokeWidth);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (currentModel == AnimatorModel.STOP) {
            canvas.drawCircle(centerPoint.x, centerPoint.y, radius, mPiePaint);
        } else if (currentModel == AnimatorModel.WAVE) {
            canvas.drawCircle(centerPoint.x, centerPoint.y, radius, mPiePaint);
            mWarePiePaint.setAlpha((int) paintAlpha);
            if (currentWareRadius > 0) {
                canvas.drawCircle(centerPoint.x, centerPoint.y, currentWareRadius, mWarePiePaint);
            }
        } else if (currentModel == AnimatorModel.SEARCH) {
            canvas.drawCircle(centerPoint.x, centerPoint.y, radius, mPiePaint);


            if (valueFloat > 0.65) {
                int alpha = (int) ((1 - valueFloat) / 0.35 * 255);
                Log.i(TAG, "onDraw: valueFloat = " + valueFloat + " alpha = " + alpha);
                mRingPiePaint.setAlpha(alpha);
            } else {
                mRingPiePaint.setAlpha(255);
            }
            float currentWidth = mStrokeWidth * valueFloat;
            mRingPiePaint.setStrokeWidth(currentWidth);
            Log.i(TAG, "onDraw: currentWidth = " + currentWidth);

            canvas.drawCircle(centerPoint.x, centerPoint.y, searchRadius, mRingPiePaint);
        }
    }

    boolean isStopWave = false;
    boolean isStopSearch = false;

    public void startSearchAnimation() {
        if (currentModel != AnimatorModel.STOP) {
            stopWareAnimation();
        }
        isStopSearch = false;
        currentModel = AnimatorModel.SEARCH;
        smoothRefreshSearch();
    }

    public void stopSearchAnimation() {
        currentModel = AnimatorModel.STOP;
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
            mAnimatorSet = null;
        }
        isStopSearch = true;
        invalidate();
    }

    public void startWareAnimation() {
        isStopWave = false;
        currentModel = AnimatorModel.WAVE;
        smoothRefreshWare();
    }

    public void stopWareAnimation() {
        isStopWave = true;
        currentModel = AnimatorModel.STOP;
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
            mAnimatorSet = null;
        }
        handler.removeCallbacksAndMessages(null);
        currentWareRadius = radius;
        invalidate();
    }

    private AnimatorSet mAnimatorSet = new AnimatorSet();

    private void smoothRefreshWare() {
        Log.i(TAG, "smoothYRefresh() called with: " + "from = [" + radius + "], to = [" + maxWareRadius + "]");
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "currentWareRadius", radius, maxWareRadius).setDuration(1500);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(this, "paintAlpha", 255f, 0f).setDuration(1200);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (isStopWave) {
                    return;
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isStopWave) {
                            return;
                        }
                        smoothRefreshWare();
                    }
                }, 1000);
            }
        });
        if (mAnimatorSet != null && mAnimatorSet.isRunning()) {
            mAnimatorSet.cancel();
            mAnimatorSet = null;
        }
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(animator, alphaAnimator);
        mAnimatorSet.start();
    }

    float valueFloat = 0;

    private void smoothRefreshSearch() {
        float maxSearchRadius = radius + 3 * mStrokeWidth / 2;
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "searchRadius", 0f, maxSearchRadius).setDuration(1500);

        animator.setInterpolator(new LinearInterpolator() {
            @Override
            public float getInterpolation(float input) {
                Log.i(TAG, "getInterpolation: input = " + input);
                valueFloat = input;
                return super.getInterpolation(input);
            }
        });
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        if (mAnimatorSet != null && mAnimatorSet.isRunning()) {
            mAnimatorSet.cancel();
            mAnimatorSet = null;
        }
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.play(animator);
        mAnimatorSet.start();
    }


    public void setCurrentWareRadius(float currentWareRadius) {
        this.currentWareRadius = currentWareRadius;
        invalidate();
    }

    public void setPaintAlpha(float paintAlpha) {
        this.paintAlpha = paintAlpha;
    }

    public void setSearchRadius(float searchRadius) {
        this.searchRadius = searchRadius;
        invalidate();
    }

    enum AnimatorModel {
        STOP, WAVE, SEARCH
    }
}
