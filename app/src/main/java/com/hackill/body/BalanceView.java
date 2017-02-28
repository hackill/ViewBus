package com.hackill.body;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.hackill.demo.R;
import com.hackill.util.DisplayUtil;

/**
 * 平衡图
 *
 * @author hackill
 */
public class BalanceView extends View {

    public static final String TAG = BalanceView.class.getSimpleName();

    // 最小的圆的半径
    float radius = 0;
    private Paint mRingPiePaint;
    private Paint mTextPaint;
    private Paint mAveragePaint;

    private Point centerPoint = new Point();
    private Point onePoint = new Point();
    private Point twoPoint = new Point();
    private Point threePoint = new Point();

    private String targetOne;
    private String targetTwo;
    private String targetThree;

    // 三条线的路径
    float[] linePaints = new float[3 * 4];
    //  三个指标
    private float physical = 0.7f;
    private float agility = 0.4f;
    private float balance = 0.3f;
    // 三个平衡指标
    private float averagePhysical = 0.8f;
    private float averageAgility = 0.6f;
    private float averageBalance = 0.7f;

    int paddingSize = 0;
    int textSize = 0;

    private Path averagePathOne = new Path();
    private Path averagePathTwo = new Path();
    private Path averagePathThree = new Path();
    private Path userPathOne = new Path();
    private Path userPathTwo = new Path();
    private Path userPathThree = new Path();

    public BalanceView(Context context) {
        this(context, null);
    }

    public BalanceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BalanceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        long startTIme = System.currentTimeMillis();
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = getDefaultSize(getSuggestedMinimumWidth(), heightMeasureSpec);
        int size = Math.min(width, height);

        centerPoint.set(size / 2, size / 2);
        //为字体预留的距离
        paddingSize = (int) DisplayUtil.dp2Px(getContext(), 40);
        textSize = (int) DisplayUtil.dp2Px(getContext(), 14);
        radius = (size / 2 - paddingSize) / 5;
        initPaint();
        initLines();
        setMeasuredDimension(size, size);
        Log.i(TAG, "onMeasure: time3 = " + (System.currentTimeMillis() - startTIme));
    }

    private void initPaint() {

        int color = getContext().getResources().getColor(R.color.average_one);
        mAveragePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAveragePaint.setColor(color);
        mAveragePaint.setStyle(Paint.Style.FILL);

        color = getContext().getResources().getColor(R.color.white);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(color);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        color = getContext().getResources().getColor(R.color.balance_ring);
        mRingPiePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRingPiePaint.setColor(color);
        mRingPiePaint.setStyle(Paint.Style.STROKE);
        mRingPiePaint.setStrokeWidth(1);

        targetOne = getContext().getString(R.string.body_physical);
        targetTwo = getContext().getString(R.string.body_agility);
        targetThree = getContext().getString(R.string.body_balance);
    }


    private void initLines() {
        float R = 5 * radius;
        linePaints = new float[]{
                0, 0, 0, -R,
                0, 0, R * (float) Math.cos(Math.PI / 6), R * (float) Math.sin(Math.PI / 6),
                0, 0, -R * (float) Math.cos(Math.PI / 6), R * (float) Math.sin(Math.PI / 6)
        };

        // 三个位置点
        onePoint.set(0, -(int) R);
        twoPoint.set((int) (R * (float) Math.cos(Math.PI / 6)), (int) (R * (float) Math.sin(Math.PI / 6)));
        threePoint.set(-(int) (R * (float) Math.cos(Math.PI / 6)), (int) (R * (float) Math.sin(Math.PI / 6)));
    }

    private void calculatePath(float R) {

        averagePathOne.reset();
        averagePathOne.moveTo(0, 0);
        averagePathOne.lineTo(0, -averagePhysical * R);
        averagePathOne.lineTo(averageAgility * R * (float) Math.cos(Math.PI / 6), averageAgility * R * (float) Math.sin(Math.PI / 6));
        averagePathOne.lineTo(0, 0);

        averagePathTwo.reset();
        averagePathTwo.moveTo(0, 0);
        averagePathTwo.lineTo(averageAgility * R * (float) Math.cos(Math.PI / 6), averageAgility * R * (float) Math.sin(Math.PI / 6));
        averagePathTwo.lineTo(-averageBalance * R * (float) Math.cos(Math.PI / 6), averageBalance * R * (float) Math.sin(Math.PI / 6));
        averagePathTwo.lineTo(0, 0);

        averagePathThree.reset();
        averagePathThree.moveTo(0, 0);
        averagePathThree.lineTo(-averageBalance * R * (float) Math.cos(Math.PI / 6), averageBalance * R * (float) Math.sin(Math.PI / 6));
        averagePathThree.lineTo(0, -averagePhysical * R);
        averagePathThree.lineTo(0, 0);
    }

    private void calculateUserPath(float R) {

        userPathOne.reset();
        userPathOne.moveTo(0, 0);
        userPathOne.lineTo(0, -physical * R);
        userPathOne.lineTo(agility * R * (float) Math.cos(Math.PI / 6), agility * R * (float) Math.sin(Math.PI / 6));
        userPathOne.lineTo(0, 0);

        userPathTwo.reset();
        userPathTwo.moveTo(0, 0);
        userPathTwo.lineTo(agility * R * (float) Math.cos(Math.PI / 6), agility * R * (float) Math.sin(Math.PI / 6));
        userPathTwo.lineTo(-balance * R * (float) Math.cos(Math.PI / 6), balance * R * (float) Math.sin(Math.PI / 6));
        userPathTwo.lineTo(0, 0);

        userPathThree.reset();
        userPathThree.moveTo(0, 0);
        userPathThree.lineTo(-balance * R * (float) Math.cos(Math.PI / 6), balance * R * (float) Math.sin(Math.PI / 6));
        userPathThree.lineTo(0, -physical * R);
        userPathThree.lineTo(0, 0);
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
        //绘制5个圈
        canvas.drawCircle(0, 0, radius, mRingPiePaint);
        canvas.drawCircle(0, 0, 2 * radius, mRingPiePaint);
        canvas.drawCircle(0, 0, 3 * radius, mRingPiePaint);
        canvas.drawCircle(0, 0, 4 * radius, mRingPiePaint);
        canvas.drawCircle(0, 0, 5 * radius, mRingPiePaint);
        // 绘制三条线
        canvas.drawLines(linePaints, mRingPiePaint);

        //physical
        canvas.drawText(targetOne, onePoint.x - getStringWith(targetOne) / 2, onePoint.y - textSize / 2, mTextPaint);
        //Agility
        canvas.drawText(targetTwo, twoPoint.x + textSize / 3, twoPoint.y + textSize, mTextPaint);
        //Balance
        canvas.drawText(targetThree, threePoint.x - getStringWith(targetThree) - textSize / 3, threePoint.y + textSize, mTextPaint);

        Log.i(TAG, "onDraw: animationValue = " + animationValue);

        if (model == AnimatorModel.AVERAGE) {

            //绘制三个 平衡三角形
            calculatePath(5 * radius * animationValue);

            mAveragePaint.setColor(getContext().getResources().getColor(R.color.average_one));
            canvas.drawPath(averagePathOne, mAveragePaint);
            mAveragePaint.setColor(getContext().getResources().getColor(R.color.average_two));
            canvas.drawPath(averagePathTwo, mAveragePaint);
            mAveragePaint.setColor(getContext().getResources().getColor(R.color.average_three));
            canvas.drawPath(averagePathThree, mAveragePaint);
        } else if (model == AnimatorModel.USER) {

            calculatePath(5 * radius);
            mAveragePaint.setColor(getContext().getResources().getColor(R.color.average_one));
            canvas.drawPath(averagePathOne, mAveragePaint);
            mAveragePaint.setColor(getContext().getResources().getColor(R.color.average_two));
            canvas.drawPath(averagePathTwo, mAveragePaint);
            mAveragePaint.setColor(getContext().getResources().getColor(R.color.average_three));
            canvas.drawPath(averagePathThree, mAveragePaint);

            calculateUserPath(5 * radius * animationValue);
            mAveragePaint.setColor(getContext().getResources().getColor(R.color.user_one));
            canvas.drawPath(userPathOne, mAveragePaint);
            mAveragePaint.setColor(getContext().getResources().getColor(R.color.user_two));
            canvas.drawPath(userPathTwo, mAveragePaint);
            mAveragePaint.setColor(getContext().getResources().getColor(R.color.user_three));
            canvas.drawPath(userPathThree, mAveragePaint);
        }
    }


    private float getStringWith(String value) {
        return mTextPaint.measureText(value);
    }


    AnimatorModel model = AnimatorModel.STOP;

    public void startAverageAnimation() {
        model = AnimatorModel.AVERAGE;
        smoothRefresh();
    }

    public void startUserAnimation() {
        model = AnimatorModel.USER;
        smoothRefresh();
    }

    public void stopAnimation() {
        if (mAnimatorSetTotal != null) {
            mAnimatorSetTotal.cancel();
            mAnimatorSetTotal = null;
        }
    }

    /**
     * 设置平均值 雷达图
     *
     * @param averagePhysical
     * @param averageAgility
     * @param averageBalance
     */
    public void setAverageData(float averagePhysical, float averageAgility, float averageBalance) {
        this.averagePhysical = averagePhysical;
        this.averageAgility = averageAgility;
        this.averageBalance = averageBalance;
    }

    /**
     * 设置用户值
     *
     * @param userPhysical
     * @param userAgility
     * @param userBalance
     */
    public void setUserData(float userPhysical, float userAgility, float userBalance) {
        this.physical = userPhysical;
        this.agility = userAgility;
        this.balance = userBalance;
    }

    private AnimatorSet mAnimatorSetTotal = new AnimatorSet();


    private float animationValue = 0;


    private void smoothRefresh() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "animationValue", 0, 1).setDuration(1200);

        animator.setInterpolator(new OvershootInterpolator(1));

        if (mAnimatorSetTotal != null && mAnimatorSetTotal.isRunning()) {
            mAnimatorSetTotal.cancel();
            mAnimatorSetTotal = null;
        }

        mAnimatorSetTotal = new AnimatorSet();
        mAnimatorSetTotal.play(animator);
        mAnimatorSetTotal.start();
    }


    public void setAnimationValue(float animationValue) {
        this.animationValue = animationValue;
        invalidate();
    }

    enum AnimatorModel {
        STOP, AVERAGE, USER
    }
}
