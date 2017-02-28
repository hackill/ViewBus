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
import android.view.animation.DecelerateInterpolator;

import com.hackill.util.DisplayUtil;

/**
 * 水平loading view
 * Created by hackill
 */

public class HorizontalLoadingView extends View {

    private static final String TAG = "BubbleTipView";

    private Paint mBigCirclePaint;
    private Paint mMinCirclePaint;
    private Paint mRadiusPaint;
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


    float mRadius = 0;
    float mRectFWidth = 0;


    public HorizontalLoadingView(Context context) {
        this(context, null);
    }

    public HorizontalLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = getDefaultSize(getSuggestedMinimumWidth(), heightMeasureSpec);

        viewHeight = height;
        viewWidth = width;
        mRadius = height / 2;
        mRectFWidth = viewWidth / 2;

        initMeasure();

        setMeasuredDimension(width, height);
    }

    private void initMeasure() {
        SecondRectF.left = 0;
        SecondRectF.top = 0;
        SecondRectF.right = viewWidth / 4;
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

    private void initPaint() {

        int color = getContext().getResources().getColor(R.color.white);
        mRadiusPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRadiusPaint.setColor(color);
        mRadiusPaint.setStyle(Paint.Style.FILL_AND_STROKE);

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
        drawLinePoint(canvas);
        drawProcessRectF(canvas);
    }

    private void drawLinePoint(Canvas canvas) {

        int count = (int) ((viewWidth - viewHeight) / viewHeight * 2 / 5);
        float spaceWidth = (viewWidth - viewHeight) / count;
        for (int i = 0; i <= count; i++) {
            canvas.drawCircle(spaceWidth * i + viewHeight / 2, viewHeight / 2, mRadius, mRadiusPaint);
        }
    }

    private void drawProcessRectF(Canvas canvas) {

        if (progressX < mRectFWidth) {
            SecondRectF.left = 0;
            SecondRectF.top = 0;
            SecondRectF.right = progressX;
            SecondRectF.bottom = viewHeight;
        } else {
            SecondRectF.left = progressX - mRectFWidth;
            SecondRectF.top = 0;
            SecondRectF.right = progressX;
            SecondRectF.bottom = viewHeight;
        }
        canvas.drawRoundRect(SecondRectF, viewHeight / 2, viewHeight / 2, mRadiusPaint);
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
        smoothRefreshProgressX();
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

    private void smoothRefreshProgressX() {

        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "progressX", 0, viewWidth + mRectFWidth).setDuration(5000);

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
        STOP, LOADING, SUCCESS
    }
}
