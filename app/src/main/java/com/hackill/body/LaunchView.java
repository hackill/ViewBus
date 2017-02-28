package com.hackill.body;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.hackill.demo.R;
import com.hackill.util.DisplayUtil;

/**
 * @author hackill
 */
public class LaunchView extends View {
//    public static final String TAG = CounterView.class.getSimpleName();

    private Paint mPiePaint;
    private Paint mWarePiePaint;
    private Paint mArcPiePaint;
    private Paint mRingPiePaint;

    private Point centerPoint = new Point();
    private AnimatorModel currentModel = AnimatorModel.STOP;

    // 中心圆半径
    float radius = 0;

    float midRadius = 0;

    float minRadius = 0;

    public LaunchView(Context context) {
        this(context, null);
    }

    public LaunchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LaunchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = getDefaultSize(getSuggestedMinimumWidth(), heightMeasureSpec);

        int size = Math.min(width, height);

        centerPoint.set(size / 2, size / 2);


        radius = size / 2;

        midRadius = radius * 12 / 25;


        minRadius = midRadius / 5;

        initPaint();

        setMeasuredDimension(size, size);
    }

    RectF rect = new RectF();
    RectF minRect = new RectF();
    Path path = new Path();

    private void initPaint() {

        int color = getContext().getResources().getColor(R.color.text_color_yellow);
        mWarePiePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWarePiePaint.setColor(color);
        mWarePiePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        color = getContext().getResources().getColor(R.color.arc_color);
        mArcPiePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcPiePaint.setColor(color);
        mArcPiePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        color = getContext().getResources().getColor(R.color.white);
        mPiePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPiePaint.setColor(color);
        mPiePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        color = getContext().getResources().getColor(R.color.text_color_yellow);
        mRingPiePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRingPiePaint.setColor(color);
        mRingPiePaint.setStyle(Paint.Style.STROKE);
        mRingPiePaint.setStrokeWidth(DisplayUtil.dp2Px(getContext(), 10));

        rect.left = -(int) radius;
        rect.top = -(int) radius;
        rect.right = (int) radius;
        rect.bottom = (int) radius;

        minRect.left = -(int) minRadius;
        minRect.top = -(int) minRadius;
        minRect.right = (int) minRadius;
        minRect.bottom = (int) minRadius;

        path.reset();
        path.moveTo(0, 0);
        path.lineTo((float) Math.sin(Math.PI / 9) * minRadius, -(float) Math.cos(Math.PI / 9) * minRadius);
        path.lineTo(3.5f * minRadius, 0);
        path.lineTo((float) Math.sin(Math.PI / 9) * minRadius, (float) Math.cos(Math.PI / 9) * minRadius);
        path.lineTo(0, 0);
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
            // 绘制淡黄色圆
            canvas.drawCircle(0, 0, radius, mArcPiePaint);
            // 黄色圆环
            canvas.drawCircle(0, 0, midRadius, mPiePaint);
            // 白色小圆
            canvas.drawCircle(0, 0, midRadius, mRingPiePaint);
            // 绘制中心指针
            canvas.rotate(-90, 0, 0);
            canvas.drawCircle(0, 0, minRadius, mWarePiePaint);
            canvas.drawPath(path, mWarePiePaint);
        } else if (currentModel == AnimatorModel.COUNTER) {

            canvas.drawCircle(0, 0, radius, mArcPiePaint);

            canvas.drawArc(rect, -90, 360 * progress, true, mPiePaint);
            // 黄色圆环
            canvas.drawCircle(0, 0, midRadius, mPiePaint);
            //白色小圆
            canvas.drawCircle(0, 0, midRadius, mRingPiePaint);
            //指针
            canvas.rotate(360 * progress - 90, 0, 0);
            canvas.drawCircle(0, 0, minRadius, mWarePiePaint);
            canvas.drawPath(path, mWarePiePaint);
        }
    }


    public void startAutoAnimation() {
        currentModel = AnimatorModel.COUNTER;
        smoothRefreshTime();
    }

    float progress = 0;

    public void setProgress(int index, int total) {
        currentModel = AnimatorModel.COUNTER;
        progress = index * 1.0f / total;
        invalidate();
    }


    public void stopAnimation() {
        currentModel = AnimatorModel.STOP;
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
            mAnimatorSet = null;
        }
    }

    private AnimatorSet mAnimatorSet = new AnimatorSet();

    private void smoothRefreshTime() {

        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "progress", 0, 1).setDuration(2000);

        animator.setInterpolator(new TimeInterpolator() {
            @Override
            public float getInterpolation(float input) {
                return input;
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

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }

    enum AnimatorModel {
        STOP, COUNTER
    }
}
