package com.hackill.body;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.hackill.demo.R;
import com.hackill.util.BitmapUtil;
import com.hackill.util.DisplayUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author hackill
 */
public class LevelCircleView extends View {
    private static final String TAG = "LevelCircleView";

    private Paint mArcPaint;
    private Paint mArcTextPaint;
    private Paint mInnerPaint;
    private Paint mValuePaint;
    private Paint mUnitPaint;

    private Point centerPoint = new Point();

    // 中心圆半径
    float radius = 0;
    float mInnerRadius = 0;
    float mTriangleRadius = 0;

    private RectF mRectF = new RectF();
    private RectF mHeartRectF = new RectF();
    private float heartRadius = 1;
    private Point mHeartCenterPoint = new Point();

    private final static int LEVEL_NUM = 5;
    private final static int INTERVAL_ANGLE = 18;
    private float sectorAngle = 50;

    private String mHeartValue = "123";
    private String mUnitValue = "心率 (bpm)";

    private int mColor1, mColor2, mColor3, mColor4, mColor0;
    private int mUColor1, mUColor2, mUColor3, mUColor4, mUColor0;

    private List<String> mLevelArray = new ArrayList<>(Arrays.asList("最佳燃脂", "心肺脂肪", "耐力锻炼", "极限锻炼", "热身心率"));

    public LevelCircleView(Context context) {
        this(context, null);
    }

    public LevelCircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LevelCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Log.i(TAG, "LevelCircleView: ");
        initPaint();
    }

    private void initPaint() {

        int color = getContext().getResources().getColor(R.color.white);
        mInnerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInnerPaint.setColor(color);
        mInnerPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        color = getContext().getResources().getColor(R.color.point_two);
        mValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mValuePaint.setColor(color);
        mValuePaint.setStyle(Paint.Style.FILL_AND_STROKE);


        color = getContext().getResources().getColor(R.color.background);
        mUnitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mUnitPaint.setColor(color);
        mUnitPaint.setTextSize(DisplayUtil.dp2Px(getContext(), 14));
        mUnitPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        color = getContext().getResources().getColor(R.color.white);
        mArcTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcTextPaint.setColor(color);
        mArcTextPaint.setTextSize(DisplayUtil.dp2Px(getContext(), 14));
        mArcTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        color = getContext().getResources().getColor(R.color.arc_blue);
        mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcPaint.setColor(color);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeCap(Paint.Cap.ROUND);
        mArcPaint.setStrokeWidth(DisplayUtil.dp2Px(getContext(), 12));

        mColor0 = getContext().getResources().getColor(R.color.level_one);
        mColor1 = getContext().getResources().getColor(R.color.level_two);
        mColor2 = getContext().getResources().getColor(R.color.level_three);
        mColor3 = getContext().getResources().getColor(R.color.level_four);
        mColor4 = getContext().getResources().getColor(R.color.level_five);

//        mUColor0 = getContext().getResources().getColor(R.color.level_one);
//        mUColor1 = getContext().getResources().getColor(R.color.level_two);
//        mUColor2 = getContext().getResources().getColor(R.color.level_three);
//        mUColor3 = getContext().getResources().getColor(R.color.level_four);
//        mUColor4 = getContext().getResources().getColor(R.color.level_five);

        sectorAngle = (360 - INTERVAL_ANGLE * LEVEL_NUM) / LEVEL_NUM;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = getDefaultSize(getSuggestedMinimumWidth(), heightMeasureSpec);

        int size = Math.min(width, height);

        centerPoint.set(size / 2, size / 2);

        radius = size / 2;


        mInnerRadius = radius * 0.62f;
        mTriangleRadius = radius * 0.69f;

        float rectRadius = radius * 0.76f;

        mRectF.left = -rectRadius;
        mRectF.top = -rectRadius;
        mRectF.bottom = rectRadius;
        mRectF.right = rectRadius;

        heartRadius = mInnerRadius * 0.16f;
        mHeartCenterPoint.set(0, -(int) (mInnerRadius * 0.65f));


        mHeartRectF.left = -heartRadius + mHeartCenterPoint.x;
        mHeartRectF.right = heartRadius + mHeartCenterPoint.x;
        mHeartRectF.top = -heartRadius + mHeartCenterPoint.y;
        mHeartRectF.bottom = heartRadius + mHeartCenterPoint.y;

        triangleAngle = (INTERVAL_ANGLE / 2 + sectorAngle / 2) / 180 * (float) Math.PI;

        mValuePaint.setTextSize(mInnerRadius * 0.85f);
        mUnitPaint.setTextSize(mInnerRadius * 0.18f);
        mArcPaint.setStrokeWidth(radius * 0.1f);
        mArcTextPaint.setTextSize(radius * 0.09f);

        Log.i(TAG, "onMeasure: width = " + width);

        setMeasuredDimension(size, size);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 将画布圆心拖至中点
        canvas.translate(centerPoint.x, centerPoint.y);
        drawSector(canvas);
        drawCenter(canvas);

    }

    private int mSelected = 0;

    private Path mTrianglePath = new Path();

    private void drawSector(Canvas canvas) {
        float startAngle = -90 + INTERVAL_ANGLE / 2;
        float sectorPerimeter = (float) (mRectF.width() * Math.PI * sectorAngle / 360);

        Path path = new Path();

        for (int i = 0; i < LEVEL_NUM; i++) {
            //绘制弧度
            mArcPaint.setColor(getColor(i));
            if (mSelected == i) {
                mArcTextPaint.setAlpha(255);
                mArcPaint.setAlpha(255);
            } else {
                mArcTextPaint.setAlpha(51);
                mArcPaint.setAlpha(51);
            }
            canvas.drawArc(mRectF, startAngle, sectorAngle, false, mArcPaint);

            path.reset();
            if (i == 1 || i == 2 || i == 3) {
                path.addArc(mRectF, startAngle + sectorAngle, -sectorAngle);
                //绘制text
                canvas.drawTextOnPath(mLevelArray.get(i), path, getXOffset(mLevelArray.get(i), sectorPerimeter), radius * 0.16f, mArcTextPaint);
            } else {
                path.addArc(mRectF, startAngle, sectorAngle);
                //绘制text
                canvas.drawTextOnPath(mLevelArray.get(i), path, getXOffset(mLevelArray.get(i), sectorPerimeter), -radius * 0.1f, mArcTextPaint);
            }

            startAngle += sectorAngle + INTERVAL_ANGLE;
        }

        calcTrianglePath();

        canvas.drawPath(mTrianglePath, mInnerPaint);
    }

    private void calcTrianglePath() {
        mTrianglePath.reset();
        mTrianglePath.moveTo(mTriangleRadius * (float) Math.sin(triangleAngle), -mTriangleRadius * (float) Math.cos(triangleAngle));
        mTrianglePath.lineTo((mInnerRadius - 2) * (float) Math.sin(triangleAngle - Math.PI * 0.03f), -(mInnerRadius - 2) * (float) Math.cos(triangleAngle - Math.PI * 0.03f));
        mTrianglePath.lineTo((mInnerRadius - 2) * (float) Math.sin(triangleAngle + Math.PI * 0.03f), -(mInnerRadius - 2) * (float) Math.cos(triangleAngle + Math.PI * 0.03f));
        mTrianglePath.close();
    }

    private int getColor(int index) {
        switch (index) {
            case 0:
                return mColor0;
            case 1:
                return mColor1;
            case 2:
                return mColor2;
            case 3:
                return mColor3;
            case 4:
                return mColor4;
        }
        return mColor0;
    }

    private void drawCenter(Canvas canvas) {
        //calc
        mHeartRectF.left = -heartRadius + mHeartCenterPoint.x;
        mHeartRectF.right = heartRadius + mHeartCenterPoint.x;
        mHeartRectF.top = -heartRadius + mHeartCenterPoint.y;
        mHeartRectF.bottom = heartRadius + mHeartCenterPoint.y;

        //绘制白圆
        canvas.drawCircle(0, 0, mInnerRadius, mInnerPaint);

        Bitmap bitmap = BitmapUtil.drawableToBitmap(getResources().getDrawable(R.drawable.face_1));
        //绘制心率
        canvas.drawBitmap(bitmap, null, mHeartRectF, new Paint());

        canvas.drawText(mHeartValue, -getStringWith(mValuePaint, mHeartValue) * 0.5f, mInnerRadius * 0.32f, mValuePaint);

        canvas.drawText(mUnitValue, -getStringWith(mUnitPaint, mUnitValue) * 0.5f, mInnerRadius * 0.7f, mUnitPaint);

    }


    private float getXOffset(String value, float sector) {

        float textLength = getStringWith(mArcTextPaint, value);

        return sector / 2 - textLength / 2;
    }

    private float getStringWith(Paint paint, String value) {
        return paint.measureText(value);
    }

    private float triangleAngle = 0;


    public void startAnimation() {
        smoothRefreshHeart();
    }

    public void setLevel(int level) {
        mSelected = level % LEVEL_NUM;
        smoothRefreshTime(level);
    }

    public void stopAnimation() {
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
            mAnimatorSet = null;
        }
        if (mAnimatorHeartSet != null) {
            mAnimatorHeartSet.cancel();
            mAnimatorHeartSet = null;
        }
    }

    private AnimatorSet mAnimatorSet = new AnimatorSet();
    private AnimatorSet mAnimatorHeartSet = new AnimatorSet();

    private void smoothRefreshTime(int level) {

        // level angle
        float targetAngle = (INTERVAL_ANGLE / 2 + sectorAngle / 2 + (INTERVAL_ANGLE + sectorAngle) * level) / 180 * (float) Math.PI;

        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "triangleAngle", triangleAngle, targetAngle).setDuration(500);

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

    private void smoothRefreshHeart() {

        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "heartRadius", heartRadius, heartRadius * 1.5f, heartRadius * 0.7f, heartRadius * 1.2f, heartRadius * 0.8f, heartRadius * 0.95f, heartRadius).setDuration(2000);

        animator.setInterpolator(new TimeInterpolator() {
            @Override
            public float getInterpolation(float input) {
                return input;
            }
        });
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        if (mAnimatorHeartSet != null && mAnimatorHeartSet.isRunning()) {
            mAnimatorHeartSet.cancel();
            mAnimatorHeartSet = null;
        }
        mAnimatorHeartSet = new AnimatorSet();
        mAnimatorHeartSet.play(animator);
        mAnimatorHeartSet.start();

    }

    public void setTriangleAngle(float triangleAngle) {
        this.triangleAngle = triangleAngle;
        invalidate();
    }

    public void setHeartRadius(float heartRadius) {
        this.heartRadius = heartRadius;
        invalidate();
    }
}
