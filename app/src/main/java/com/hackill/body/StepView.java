package com.hackill.body;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.hackill.demo.R;
import com.hackill.util.DisplayUtil;

/**
 * @author hackill
 */
public class StepView extends View {
    public static final String TAG = StepView.class.getSimpleName();

    private Paint mPiePaint;
    private Paint mWarePiePaint;
    private Paint mRingPiePaint;

    private Point centerPoint = new Point();
    private AnimatorModel currentModel = AnimatorModel.STOP;
    private Handler handler = new Handler(Looper.getMainLooper());

    // 最大波浪半径
    float maxWareRadius = 0;
    // 当前波浪半径
    float currentWareRadius = 0;
    // 中心圆半径
    float radius = 0;
    // 画笔透明度
    private float paintAlpha = 0;
    private Path mRightPath = new Path();

    private float right_X = 0;

    private float offSet_X = 0;
    private float offSet_Y = 0;

    private float startX = 0;
    private float endX = 0;


    public StepView(Context context) {
        this(context, null);
    }

    public StepView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StepView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = getDefaultSize(getSuggestedMinimumWidth(), heightMeasureSpec);

        int size = Math.min(width, height);

        centerPoint.set(size / 2, size / 2);

        radius = size / 3.5f;
        maxWareRadius = size / 2;


        offSet_X = -radius / 6;//-DisplayUtil.dp2Px(getContext(), 15);
        offSet_Y = radius / 3;//DisplayUtil.dp2Px(getContext(), 25);
        startX = -radius * 2 / 5;
        endX = radius * 4 / 5;

        initPaint();

        setMeasuredDimension(size, size);
    }

    private void initPaint() {

        int color = getContext().getResources().getColor(R.color.white);
        mWarePiePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWarePiePaint.setColor(color);
        mWarePiePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        color = getContext().getResources().getColor(R.color.white);
        mPiePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPiePaint.setColor(color);
        mPiePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        color = getContext().getResources().getColor(R.color.white);
        mRingPiePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRingPiePaint.setColor(color);
        mRingPiePaint.setStyle(Paint.Style.STROKE);
        mRingPiePaint.setStrokeWidth(DisplayUtil.dp2Px(getContext(), 3));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 将画布圆心拖至中点
        canvas.translate(centerPoint.x, centerPoint.y);
        if (currentModel == AnimatorModel.STOP) {
            mPiePaint.setColor(getContext().getResources().getColor(R.color.white));
            canvas.drawCircle(0, 0, radius, mPiePaint);
        } else if (currentModel == AnimatorModel.WAVE) {
            mPiePaint.setColor(getContext().getResources().getColor(R.color.white));
            canvas.drawCircle(0, 0, radius, mPiePaint);
            mWarePiePaint.setAlpha((int) paintAlpha);
            if (currentWareRadius > 0) {
                canvas.drawCircle(0, 0, currentWareRadius, mWarePiePaint);
            }
        } else if (currentModel == AnimatorModel.SUCCESS) {
            mPiePaint.setColor(getContext().getResources().getColor(R.color.text_color_yellow));
            canvas.drawCircle(0, 0, radius, mPiePaint);
            Log.i(TAG, "onDraw: right_X = " + right_X);
            getCurrentRightPath();
            canvas.drawPath(mRightPath, mRingPiePaint);
        }
    }

    private void getCurrentRightPath() {
        mRightPath.reset();
        //起始点
        mRightPath.moveTo(startX + offSet_X, startX + offSet_Y);
        //落点
        if (right_X <= 0) {
            mRightPath.lineTo(right_X + offSet_X, right_X + offSet_Y);
        } else {
            mRightPath.lineTo(0 + offSet_X, 0 + offSet_Y);
            mRightPath.lineTo(right_X + offSet_X, -right_X + offSet_Y);
        }
    }

    boolean isStopWave = false;

    public void startWareAnimation() {
        isStopWave = false;
        currentModel = AnimatorModel.WAVE;
        smoothRefreshWare();
    }


    public void startSuccessAnimation() {
        isStopWave = true;
        currentModel = AnimatorModel.SUCCESS;
        smoothRefreshRight();
    }

    public void stopAnimation() {
        isStopWave = true;
        currentModel = AnimatorModel.STOP;
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
            mAnimatorSet = null;
        }
        handler.removeCallbacksAndMessages(null);
        currentWareRadius = radius;
    }

    private AnimatorSet mAnimatorSet = new AnimatorSet();

    private void smoothRefreshRight() {

        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "right_X", startX, endX).setDuration(800);

        if (mAnimatorSet != null && mAnimatorSet.isRunning()) {
            mAnimatorSet.cancel();
            mAnimatorSet = null;
        }
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.play(animator);
        mAnimatorSet.start();
    }

    private void smoothRefreshWare() {
        Log.i(TAG, "smoothYRefresh() called with: " + "from = [" + radius + "], to = [" + maxWareRadius + "]");
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "currentWareRadius", radius, maxWareRadius).setDuration(1200);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(this, "paintAlpha", 255f, 0f).setDuration(1000);
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
                }, 300);
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


    public void setCurrentWareRadius(float currentWareRadius) {
        this.currentWareRadius = currentWareRadius;
        invalidate();
    }

    public void setPaintAlpha(float paintAlpha) {
        this.paintAlpha = paintAlpha;
    }


    public void setRight_X(float right_X) {
        this.right_X = right_X;
        invalidate();
    }

    enum AnimatorModel {
        STOP, WAVE, OVER, SUCCESS
    }
}
