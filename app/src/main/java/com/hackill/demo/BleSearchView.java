package com.hackill.demo;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * @author hackill
 */
public class BleSearchView extends View {
    public static final String TAG = BleSearchView.class.getSimpleName();
    private static final float mMaxStrokeWidth = 8;
    private Paint mImgPaint;
    private Paint mRingPiePaint;

    private int viewWith = 0;
    private int viewHeight = 0;
    private AnimatorModel currentModel = AnimatorModel.STOP;

    private float radius = 0;
    private float searchRadius = 0;

    private Rect mSrcRect;
    private Rect mDestRect;
    private Bitmap bleBitmap;

    private Point wareCenter = new Point();
    private float mDeltaRadius = 0;
    private float mStrokeWidth = 5;


    private float mFirstRadius = 0;
    private float mSecondRadius = -1;
    private float mThirdRadius = -1;
    private RectF mFirstRect = new RectF();
    private RectF mSecondRect = new RectF();
    private RectF mThirdRect = new RectF();

    public BleSearchView(Context context) {
        this(context, null);
    }

    public BleSearchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BleSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = getDefaultSize(getSuggestedMinimumWidth(), heightMeasureSpec);

        viewWith = width;
        viewHeight = height;

        radius = viewHeight / 2;

        initPaint();

        setMeasuredDimension(width, height);
    }


    private void initPaint() {

        int color = getContext().getResources().getColor(R.color.text_color_yellow);

        mRingPiePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRingPiePaint.setColor(color);
        mRingPiePaint.setStyle(Paint.Style.STROKE);
        mRingPiePaint.setStrokeWidth(mStrokeWidth);

        mImgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mImgPaint.setStyle(Paint.Style.STROKE);
        mImgPaint.setFilterBitmap(true);

        bleBitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.phone)).getBitmap();

        int mBitWidth = bleBitmap.getWidth();
        int mBitHeight = bleBitmap.getHeight();
        mSrcRect = new Rect(0, 0, mBitWidth, mBitHeight);
        mDestRect = new Rect(viewWith / 4, viewHeight - viewHeight / 2, viewWith * 3 / 4, viewHeight);
        wareCenter.set(viewWith / 2, viewHeight - viewWith * 3 / 8);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(bleBitmap, mSrcRect, mDestRect, mImgPaint);
        if (currentModel == AnimatorModel.SEARCH) {
            calcRect();
            calcRingPiePaint(mFirstRect.width());
            canvas.drawArc(mFirstRect, -135, 90, false, mRingPiePaint);
            calcRingPiePaint(mSecondRect.width());
            canvas.drawArc(mSecondRect, -135, 90, false, mRingPiePaint);
            calcRingPiePaint(mThirdRect.width());
            canvas.drawArc(mThirdRect, -135, 90, false, mRingPiePaint);
        }
    }

    private void calcRingPiePaint(float rectWidth) {
        float curRadius = rectWidth / 2;

        if (curRadius > radius / 3) {
            // 开始透明渐变
            float ratio = (curRadius - radius / 3) / (radius - radius / 3);

            mRingPiePaint.setAlpha((int) ((1 - ratio) * 255));
            mRingPiePaint.setStrokeWidth(mStrokeWidth + mMaxStrokeWidth * ratio);
        } else {
            mRingPiePaint.setAlpha(255);
            mRingPiePaint.setStrokeWidth(mStrokeWidth);
        }

    }

    private void calcRect() {

        mFirstRadius += mDeltaRadius;
        mFirstRadius %= radius;

        if (mSecondRadius < 0) {
            if (mFirstRadius > radius / 3) {
                mSecondRadius = mDeltaRadius;
            }
        } else {
            mSecondRadius += mDeltaRadius;
            mSecondRadius %= radius;
        }

        if (mThirdRadius < 0) {
            if (mSecondRadius > radius / 3) {
                mThirdRadius = mDeltaRadius;
            }
        } else {
            mThirdRadius += mDeltaRadius;
            mThirdRadius %= radius;
        }

        mFirstRect.left = -mFirstRadius + wareCenter.x;
        mFirstRect.top = -mFirstRadius + wareCenter.y;
        mFirstRect.right = mFirstRadius + wareCenter.x;
        mFirstRect.bottom = mFirstRadius + wareCenter.y;

        if (mSecondRadius > 0) {
            mSecondRect.left = -mSecondRadius + wareCenter.x;
            mSecondRect.top = -mSecondRadius + wareCenter.y;
            mSecondRect.right = mSecondRadius + wareCenter.x;
            mSecondRect.bottom = mSecondRadius + wareCenter.y;
        } else {
            mSecondRect.setEmpty();
        }

        if (mThirdRadius > 0) {
            mThirdRect.left = -mThirdRadius + wareCenter.x;
            mThirdRect.top = -mThirdRadius + wareCenter.y;
            mThirdRect.right = mThirdRadius + wareCenter.x;
            mThirdRect.bottom = mThirdRadius + wareCenter.y;
        } else {
            mThirdRect.setEmpty();
        }
    }

    public void startSearchAnimation() {
        currentModel = AnimatorModel.SEARCH;
        mFirstRadius = 0;
        mSecondRadius = -1;
        mThirdRadius = -1;
        smoothRefreshSearch();

    }

    public void stopSearchAnimation() {
        currentModel = AnimatorModel.STOP;
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
            mAnimatorSet = null;
        }
        invalidate();
    }

    private AnimatorSet mAnimatorSet = new AnimatorSet();


    private void smoothRefreshSearch() {

        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "searchRadius", 0f, radius).setDuration(2500);

        animator.setInterpolator(new LinearInterpolator() {
            @Override
            public float getInterpolation(float input) {
                return super.getInterpolation(input);
            }
        });
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        if (mAnimatorSet != null && mAnimatorSet.isRunning()) {
            mAnimatorSet.cancel();
            mAnimatorSet = null;
        }
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.play(animator);
        mAnimatorSet.start();
    }


    public void setSearchRadius(float searchRadius) {
        mDeltaRadius = Math.abs(searchRadius - this.searchRadius);
        this.searchRadius = searchRadius;
        invalidate();
    }

    enum AnimatorModel {
        STOP, SEARCH
    }
}
