package com.hackill.demo;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.hackill.util.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据报表 日 周 月切换 strip
 */
public class TimePickerScrollView extends View {

    protected float mItemWidth, mWidth;
    protected Paint mTextPaint, mBackgroundPaint, mCheckedTextPaint;

    protected int mSelectedIndex = 0;
    protected float mPadding;
    protected float mOffXPadding = 0;
    protected float mIndicatorHeight = 10;
    protected AnimatorSet mAnimatorSet = null;
    protected RectF mTmp = new RectF();

    protected float mAnimationPos, mAnimationWidth;

    protected List<String> mItems = new ArrayList<>();

    protected OnItemCheckListener mCheckListener;


    public TimePickerScrollView(Context context) {
        super(context);

    }

    public TimePickerScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimePickerScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initPaint();
    }

    protected void initPaint() {

        float textSize = DisplayUtil.dp2Px(getContext(), 18);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(Color.parseColor("#FFFFFF"));
        mTextPaint.setTextSize(textSize);

        mCheckedTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCheckedTextPaint.setTextAlign(Paint.Align.CENTER);
        mCheckedTextPaint.setColor(Color.parseColor("#ffffff"));
        mCheckedTextPaint.setTextSize(textSize);

        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setColor(Color.parseColor("#FFFFFF"));

        mIndicatorHeight = DisplayUtil.dp2Px(getContext(), 4);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

        mWidth = width;
        initItemLayout();
    }


    protected void initItemLayout() {
        if (mWidth != 0 && mItems != null && mItems.size() > 0) {
            mItemWidth = mWidth / mItems.size();
            mPadding = Math.min(mItemWidth, getHeight()) * 0.1f;
            mOffXPadding = mItemWidth * 0.15f;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mTmp.set(0, 0, getWidth(), getHeight());

        if (mAnimatorSet != null && mAnimatorSet.isRunning()) {
            mTmp.set(mAnimationPos + mOffXPadding, getHeight() - mPadding - mIndicatorHeight, mAnimationPos + mItemWidth - mOffXPadding, getHeight() - mPadding);
        } else {
            float start = mSelectedIndex * mItemWidth;
            mTmp.set(start + mOffXPadding, getHeight() - mPadding - mIndicatorHeight, start + mItemWidth - mOffXPadding, getHeight() - mPadding);
        }
        canvas.drawRect(mTmp, mBackgroundPaint);

        drawItem(canvas);
    }

    protected void drawItem(Canvas canvas) {
        float x = mItemWidth / 2f;
        float y = getHeight() / 2f - (mTextPaint.getFontMetrics().top + mTextPaint.getFontMetrics().bottom) / 2f - mPadding / 2;
        for (int i = 0; i < mItems.size(); i++) {
            if (i == mSelectedIndex) {
                canvas.drawText(mItems.get(i), x, y, mCheckedTextPaint);
            } else {
                canvas.drawText(mItems.get(i), x, y, mTextPaint);
            }
            x += mItemWidth;
        }
    }


    public void check(int index) {
        animatingTo(index);
        mSelectedIndex = index;

        onItemClickListen(index);
    }

    public void check(int index, boolean animation) {
        if (animation) {
            check(index);
        } else {
            mSelectedIndex = index;
            invalidate();
            onItemClickListen(index);
        }
    }


    private void onItemClickListen(int index) {
        if (mCheckListener != null && mItems != null && mItems.size() > index) {
            mCheckListener.onCheck(index);
        }
    }

    protected void animatingTo(int index) {
        if (index < 0 || mItems == null || index >= mItems.size()) {
            return;
        }
        if (mAnimatorSet != null && mAnimatorSet.isRunning()) {
            mAnimatorSet.end();
        }

        ObjectAnimator oa = ObjectAnimator.ofFloat(this, "animationPos", mSelectedIndex * mItemWidth, index * mItemWidth);
        mSelectedIndex = index;


        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.play(oa);


        mAnimatorSet.setDuration(200);
        mAnimatorSet.start();
    }


    public void setAnimationPos(float value) {
        mAnimationPos = value;
        invalidate();
    }

    public void setAnimationWidth(float value) {
        mAnimationWidth = value;
    }

    public List<String> getItems() {
        return mItems;
    }

    public void setItems(List<String> mItems) {
        this.mItems = mItems;
        initItemLayout();
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getActionMasked();

        if (action == MotionEvent.ACTION_UP) {
            check((int) (event.getX() / mItemWidth));
        }
        return true;
    }

    public interface OnItemCheckListener {
        void onCheck(int index);
    }


    public void setCheckListener(OnItemCheckListener mCheckListener) {
        this.mCheckListener = mCheckListener;
    }
}
