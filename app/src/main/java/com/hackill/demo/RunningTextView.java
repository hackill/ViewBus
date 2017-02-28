package com.hackill.demo;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import java.math.BigDecimal;

/**
 * 奔跑吧textview
 * Created by hackill
 */

public class RunningTextView extends TextView {

    public RunningTextView(Context context) {
        this(context, null);
    }

    public RunningTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RunningTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private float mNumber = 0;
    private float mOldNumber = 0;

    public void setTextNumber(float number) {
        this.mNumber = number;
    }

    public void setAndRunTextNumber(float number) {
        this.mNumber = number;
        startAutoAnimation();
    }

    public String getInfo(){
        return mOldNumber +"-"+ mNumber;
    }

    public void startAutoAnimation() {
        smoothRefreshNumber();
    }

    public void stopAnimation() {
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
            mAnimatorSet = null;
        }
    }

    private AnimatorSet mAnimatorSet = new AnimatorSet();

    private float tempNumber = 0;

    private void smoothRefreshNumber() {

        long refreshTime;

        float delTime = Math.abs(mNumber - mOldNumber);
        if (delTime < 2) {
            refreshTime = 200;
        } else if (delTime < 5) {
            refreshTime = 300;
        } else if (delTime < 10) {
            refreshTime = 400;
        } else if (delTime < 20) {
            refreshTime = 500;
        } else {
            refreshTime = 800;
        }

        if (mAnimatorSet != null && mAnimatorSet.isRunning()) {
            mAnimatorSet.cancel();
            mAnimatorSet = null;
        }

        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "tempNumber", mOldNumber, mNumber).setDuration(refreshTime);

        animator.setInterpolator(new TimeInterpolator() {
            @Override
            public float getInterpolation(float input) {
                return input;
            }
        });

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mOldNumber = tempNumber;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.play(animator);
        mAnimatorSet.start();
    }

    private void setTempNumber(float tempNumber) {
        float curFloat = getFloatFormatNumber(tempNumber);
        if (Math.abs(curFloat - this.tempNumber) > 0) {
            this.tempNumber = curFloat;
            this.setText(String.valueOf(curFloat));
        }
    }

    private float getFloatFormatNumber(float value) {
        BigDecimal b = new BigDecimal(value);
        return b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
    }
}
