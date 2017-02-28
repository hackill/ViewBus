package com.hackill.body;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import com.hackill.demo.R;

/**
 * @author hackill
 */
public class ProgressView extends View {

    public static final String TAG = ProgressView.class.getSimpleName();

    private Paint mRectPaint;

    public ProgressView(Context context) {
        this(context, null);
    }

    public ProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int viewWidth = 0;
    private int viewHeight = 0;


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        viewWidth = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        viewHeight = getDefaultSize(getSuggestedMinimumWidth(), heightMeasureSpec);

        mRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRectPaint.setColor(getContext().getResources().getColor(R.color.text_color_yellow));
        mRectPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        setMeasuredDimension(viewWidth, viewHeight);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        updateRect();
        canvas.drawRect(mProgressRect, mRectPaint);
    }

    private Rect mProgressRect = new Rect();

    private void updateRect() {
        mProgressRect.left = 0;
        mProgressRect.top = 0;
        mProgressRect.bottom = viewHeight;
        mProgressRect.right = (int) (viewWidth * progress);
    }


    private float progress = 0;
    private AnimatorModel currentModel = AnimatorModel.PROGRESS;

    public void setProgress(int index, int total) {
        currentModel = AnimatorModel.PROGRESS;
        this.progress = index * 1.0f / total;
        invalidate();
    }

    public void startResetProgressAnimation() {
        currentModel = AnimatorModel.RESET;
        smoothRefreshReset();
    }

    public void stopAnimation() {
        currentModel = AnimatorModel.PROGRESS;
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
            mAnimatorSet = null;
        }
    }

    private AnimatorSet mAnimatorSet = new AnimatorSet();

    private void smoothRefreshReset() {

        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "progress", 0, 1).setDuration(500);

        animator.setInterpolator(new AccelerateInterpolator());

        if (mAnimatorSet != null && mAnimatorSet.isRunning()) {
            mAnimatorSet.cancel();
            mAnimatorSet = null;
        }
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.play(animator);
        mAnimatorSet.start();
    }

    private void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }

    enum AnimatorModel {
        RESET, PROGRESS
    }
}
