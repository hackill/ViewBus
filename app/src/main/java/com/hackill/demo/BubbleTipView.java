package com.hackill.demo;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
import android.view.animation.DecelerateInterpolator;

import com.hackill.util.DisplayUtil;

/**
 * Created by hackill on 16/9/14.
 */

public class BubbleTipView extends View {

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
    private RectF SecondRectF = new RectF();
    private Path trianglePath = new Path();
    private int alpha = 0;
    private float progressX = 0;
    private String mTextValue = "";


    public BubbleTipView(Context context) {
        this(context, null);
    }

    public BubbleTipView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubbleTipView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = getDefaultSize(getSuggestedMinimumWidth(), heightMeasureSpec);

        viewHeight = height;
        viewWidth = width;
        maxRadius = height / 4;
        minRadius = height / 6;

        initMeasure();

        setMeasuredDimension(width, height);
    }

    private void initMeasure() {
        SecondRectF.left = maxRadius * 2 + viewHeight / 2;
        SecondRectF.top = 0;
        SecondRectF.right = viewWidth;
        SecondRectF.bottom = viewHeight;

        progressX = maxRadius * 2;

        trianglePath.reset();
        trianglePath.moveTo(maxRadius * 2, viewHeight / 2);
        trianglePath.lineTo(maxRadius * 2 + viewHeight / 2, 0);
        trianglePath.lineTo(viewWidth, 0);
        trianglePath.lineTo(viewWidth, viewHeight);
        trianglePath.lineTo(maxRadius * 2 + viewHeight / 2, viewHeight);
        trianglePath.close();
        showTextViewWidth = viewWidth - maxRadius * 2 - viewHeight * 2 / 3;
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


        color = getContext().getResources().getColor(R.color.white);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(color);
        mTextPaint.setTextSize(DisplayUtil.dp2Px(getContext(), 15));

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (currentModel == AnimatorModel.START) {
            mBigCirclePaint.setAlpha(alpha);
            canvas.drawCircle(maxRadius, viewHeight / 2, maxRadius, mBigCirclePaint);
            canvas.drawCircle(maxRadius, viewHeight / 2, minRadius, mMinCirclePaint);
        } else if (currentModel == AnimatorModel.START2) {
            canvas.drawCircle(maxRadius, viewHeight / 2, maxRadius, mBigCirclePaint);
            canvas.drawCircle(maxRadius, viewHeight / 2, minRadius, mMinCirclePaint);
            calcBackPath();
            canvas.drawPath(trianglePath, mBackgroundPaint);
        } else if (currentModel == AnimatorModel.OVER) {
            canvas.drawCircle(maxRadius, viewHeight / 2, maxRadius, mBigCirclePaint);
            canvas.drawCircle(maxRadius, viewHeight / 2, minRadius, mMinCirclePaint);
            canvas.drawPath(trianglePath, mBackgroundPaint);
            Rect rect = new Rect();
            mTextPaint.getTextBounds(mTextValue, 0, mTextValue.length(), rect);
            canvas.drawText(mTextValue, (maxRadius * 2 + viewHeight / 2 + showTextViewWidth / 2 - rect.width() / 2), (viewHeight / 2 + rect.height() / 3), mTextPaint);
        }
    }

    private void calcBackPath() {
        if (progressX < maxRadius * 2) progressX = maxRadius * 2;
        if (progressX > viewWidth) progressX = viewWidth;

        float bodyWidth = viewWidth - maxRadius * 2 - viewHeight / 2;

        if (progressX <= (viewWidth - viewHeight / 2)) {
            trianglePath.reset();
            trianglePath.moveTo(maxRadius * 2, 0);
            trianglePath.lineTo(progressX, 0);
            trianglePath.lineTo(progressX, viewHeight);
            trianglePath.lineTo(maxRadius * 2, viewHeight);
            trianglePath.close();
        } else {
            trianglePath.reset();
            trianglePath.moveTo(progressX, 0);
            trianglePath.lineTo(progressX, viewHeight);
            trianglePath.lineTo(progressX - bodyWidth, viewHeight);
            trianglePath.lineTo(maxRadius * 2, (viewHeight - (progressX - bodyWidth - maxRadius * 2)));
            trianglePath.lineTo(maxRadius * 2, (progressX - bodyWidth - maxRadius * 2));
            trianglePath.lineTo(progressX - bodyWidth, 0);
            trianglePath.close();
        }
    }

    public void setTextValue(String textValue) {
        this.mTextValue = textValue;
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
        }
    }

    public void startAutoAnimation() {
        currentModel = AnimatorModel.START;
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

        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "alpha", 0, 255).setDuration(500);

        animator.setInterpolator(new DecelerateInterpolator());
        animator.setRepeatCount(1);
        animator.setRepeatMode(ValueAnimator.RESTART);

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                currentModel = AnimatorModel.START2;
                smoothRefreshBackground();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                Log.i(TAG, "onAnimationRepeat: ...");

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

    private void smoothRefreshBackground() {

        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "progressX", maxRadius * 2, viewWidth).setDuration(500);

        animator.setInterpolator(new DecelerateInterpolator());
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.i(TAG, "smoothRefreshBackground onAnimationEnd: ");
                currentModel = AnimatorModel.OVER;
                invalidate();
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

    public void setAlpha(float alpha) {
        this.alpha = (int) alpha;
        invalidate();
    }

    public void setProgressX(float progressX) {
        this.progressX = progressX;
        invalidate();
    }

    enum AnimatorModel {
        STOP, START, START2, OVER
    }
}
