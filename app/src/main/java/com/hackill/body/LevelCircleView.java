package com.hackill.body;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
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

    private AnimatorModel currentModel = AnimatorModel.STOP;

    // 中心圆半径
    float radius = 0;
    float mInnerRadius = 0;

    private RectF mRectF = new RectF();
    private RectF mHeartRectF = new RectF();
    private float mHeartRectFRadius = 1;
    private Point mHeartCenterPoint = new Point();

    private final static int LEVEL_NUM = 5;
    private final static int INTERVAL_ANGLE = 18;
    private float sectorAngle = 50;

    private String mHeartValue = "123";
    private String mUnitValue = "心率 (bpm)";

    private int mColor1, mColor2, mColor3, mColor4, mColor0;

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

        float rectRadius = radius * 0.76f;

        mRectF.left = -rectRadius;
        mRectF.top = -rectRadius;
        mRectF.bottom = rectRadius;
        mRectF.right = rectRadius;

        mHeartRectFRadius = mInnerRadius * 0.16f;
        mHeartCenterPoint.set(0, -(int) (mInnerRadius * 0.65f));


        mHeartRectF.left = -mHeartRectFRadius + mHeartCenterPoint.x;
        mHeartRectF.right = mHeartRectFRadius + mHeartCenterPoint.x;
        mHeartRectF.top = -mHeartRectFRadius + mHeartCenterPoint.y;
        mHeartRectF.bottom = mHeartRectFRadius + mHeartCenterPoint.y;

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
        drawCenter(canvas);
        drawSector(canvas);

    }

    private int mSelected = 0;

    private void drawSector(Canvas canvas) {
        float startAngle = -90 + INTERVAL_ANGLE / 2;
        float sectorPerimeter = (float) (mRectF.width() * Math.PI * sectorAngle / 360);


        Path path = new Path();

        for (int i = 0; i < LEVEL_NUM; i++) {
            //绘制弧度
            mArcPaint.setColor(getColor(i));
            canvas.drawArc(mRectF, startAngle, sectorAngle, false, mArcPaint);
            path.reset();
            path.addArc(mRectF, startAngle, sectorAngle);
            //绘制text
            canvas.drawTextOnPath(mLevelArray.get(i), path, getXOffset(mLevelArray.get(i), sectorPerimeter), -radius * 0.1f, mArcTextPaint);
            startAngle += sectorAngle + INTERVAL_ANGLE;
        }
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
        mHeartRectF.left = -mHeartRectFRadius + mHeartCenterPoint.x;
        mHeartRectF.right = mHeartRectFRadius + mHeartCenterPoint.x;
        mHeartRectF.top = -mHeartRectFRadius + mHeartCenterPoint.y;
        mHeartRectF.bottom = mHeartRectFRadius + mHeartCenterPoint.y;

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
