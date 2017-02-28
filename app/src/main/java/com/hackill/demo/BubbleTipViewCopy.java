package com.hackill.demo;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.hackill.util.DisplayUtil;

/**
 * Created by hackill on 16/9/14.
 */

public class BubbleTipViewCopy extends View {

    private static final String TAG = "BubbleTipView";

    private Paint mBigCirclePaint;
    private Paint mMinCirclePaint;
    private Paint mBackgroundPaint;
    private Paint mTextPaint;

    private AnimatorModel currentModel = AnimatorModel.STOP;

    float minRadius = 0;
    float maxRadius = 0;
    float viewHeight = 0;
    float viewWidth = 0;
    float showTextViewWidth = 0;
    RectF SecondRectF = new RectF();
    Path trianglePath = new Path();
    int alpha = 0;
    private String mTextValue = "";
    private String mRunnerTextValue;


    public BubbleTipViewCopy(Context context) {
        this(context, null);
    }

    public BubbleTipViewCopy(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubbleTipViewCopy(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = getDefaultSize(getSuggestedMinimumWidth(), heightMeasureSpec);

        viewHeight = height;
        viewWidth = width;
        maxRadius = height / 4;
        minRadius = height / 6;

        initPaint();

        setMeasuredDimension(width, height);
    }

    private void initPaint() {

        int color = getContext().getResources().getColor(R.color.background);
        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setColor(color);
        mBackgroundPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        color = getContext().getResources().getColor(R.color.arc_blue);
        mMinCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMinCirclePaint.setColor(color);
        mMinCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        color = getContext().getResources().getColor(R.color.point_three);
        mBigCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBigCirclePaint.setColor(color);
        mBigCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);


        color = getContext().getResources().getColor(R.color.text_color_yellow);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(color);
        mTextPaint.setTextSize(DisplayUtil.dp2Px(getContext(), 15));

        SecondRectF.left = maxRadius * 2 + viewHeight / 2;
        SecondRectF.top = 0;
        SecondRectF.right = viewWidth;
        SecondRectF.bottom = viewHeight;


        trianglePath.reset();
        trianglePath.moveTo(maxRadius * 2, viewHeight / 2);
        trianglePath.lineTo(maxRadius * 2 + viewHeight / 2, 0);
        trianglePath.lineTo(viewWidth, 0);
        trianglePath.lineTo(viewWidth, viewHeight);
        trianglePath.lineTo(maxRadius * 2 + viewHeight / 2, viewHeight);
        trianglePath.close();
        showTextViewWidth = viewWidth - maxRadius * 2 - viewHeight * 2 / 3;

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (currentModel == AnimatorModel.START) {
//            mMinCirclePaint.setAlpha(alpha);
            mBigCirclePaint.setAlpha(alpha);
            canvas.drawCircle(maxRadius, viewHeight / 2, maxRadius, mBigCirclePaint);
            canvas.drawCircle(maxRadius, viewHeight / 2, minRadius, mMinCirclePaint);
        } else if (currentModel == AnimatorModel.START2) {
            canvas.drawCircle(maxRadius, viewHeight / 2, maxRadius, mBigCirclePaint);
            canvas.drawCircle(maxRadius, viewHeight / 2, minRadius, mMinCirclePaint);
            canvas.drawPath(trianglePath, mBackgroundPaint);
        } else if (currentModel == AnimatorModel.START3) {
            canvas.drawCircle(maxRadius, viewHeight / 2, maxRadius, mBigCirclePaint);
            canvas.drawCircle(maxRadius, viewHeight / 2, minRadius, mMinCirclePaint);
            canvas.drawPath(trianglePath, mBackgroundPaint);
            drawRunningText(canvas);
        } else if (currentModel == AnimatorModel.OVER) {
            canvas.drawCircle(maxRadius, viewHeight / 2, maxRadius, mBigCirclePaint);
            canvas.drawCircle(maxRadius, viewHeight / 2, minRadius, mMinCirclePaint);
            canvas.drawPath(trianglePath, mBackgroundPaint);
        }
    }

    public void drawRunningText(Canvas canvas) {
        int runLength = mRunnerTextValue == null ? 0 : mRunnerTextValue.length();
        if (!TextUtils.isEmpty(mTextValue) && runLength < mTextValue.length()) {
            mRunnerTextValue = mTextValue.substring(0, runLength + 1);
            Rect rect = new Rect();
            mTextPaint.getTextBounds(mRunnerTextValue, 0, mRunnerTextValue.length(), rect);
            canvas.drawText(mRunnerTextValue, (maxRadius * 2 + viewHeight / 2 + showTextViewWidth / 2 - rect.width() / 2), (viewHeight / 2 + rect.height() / 3), mTextPaint);
        } else {
            if (runLength == mTextValue.length()) {
                mRunnerTextValue = null;
            }
            if (TextUtils.isEmpty(mTextValue)) {
                //播报完毕
                if (mAnimatorSet != null) {
                    mAnimatorSet.cancel();
                    mAnimatorSet = null;
                }
                currentModel = AnimatorModel.OVER;
                invalidate();
            }
        }
    }

    public void setTextValue(String textValue) {
        this.mTextValue = textValue;
        mRunnerTextValue = null;
        mTextPaint.setTextSize(DisplayUtil.dp2Px(getContext(), 15));
        if (!TextUtils.isEmpty(mTextValue)) {
            Rect rect = new Rect();
            mTextPaint.getTextBounds(mTextValue, 0, mTextValue.length(), rect);
            while (rect.width() > showTextViewWidth) {
                mTextPaint.setTextSize(mTextPaint.getTextSize() - 1);
                mTextPaint.getTextBounds(mTextValue, 0, mTextValue.length(), rect);
                if (mTextPaint.getTextSize() < 5) {
                    break;
                }
            }
            Log.i(TAG, "setTextValue: showTextViewWidth = " + showTextViewWidth + ", textWidth = " + rect.width() + ", paint Size = " + mTextPaint.getTextSize());
        }
    }

    public void startAutoAnimation() {
        currentModel = AnimatorModel.START;
        mRunnerTextValue = null;
        smoothRefreshTime();
    }

    public void stopAnimation() {
        currentModel = AnimatorModel.STOP;
        invalidate();
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
            mAnimatorSet = null;
        }
    }

    private AnimatorSet mAnimatorSet = new AnimatorSet();

    private void smoothRefreshTime() {

        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "alpha", 0, 255).setDuration(200);

        animator.setInterpolator(new TimeInterpolator() {
            @Override
            public float getInterpolation(float input) {
                return input;
            }
        });
        animator.setRepeatCount(4);
        animator.setRepeatMode(ValueAnimator.RESTART);

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                currentModel = AnimatorModel.START2;
                invalidate();
                smoothRefreshAutoText();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {


            }
        });

        if (mAnimatorSet != null && mAnimatorSet.isRunning()) {
            mAnimatorSet.cancel();
            mAnimatorSet = null;
        }
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.play(animator);
        mAnimatorSet.start();
    }

    private void smoothRefreshAutoText() {

        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "test", 0, 125).setDuration(400);

        animator.setInterpolator(new TimeInterpolator() {
            @Override
            public float getInterpolation(float input) {
                return input;
            }
        });

        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                currentModel = AnimatorModel.START3;
                invalidate();
            }
        });

        if (mAnimatorSet != null && mAnimatorSet.isRunning()) {
            mAnimatorSet.cancel();
            mAnimatorSet = null;
        }
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.play(animator);
        mAnimatorSet.start();
    }

    public void setAlpha(float alpha) {
        this.alpha = (int) alpha;
        invalidate();
    }

    enum AnimatorModel {
        STOP, START, START2, START3, OVER
    }
}
