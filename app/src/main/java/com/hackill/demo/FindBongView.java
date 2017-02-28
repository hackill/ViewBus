package com.hackill.demo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * @author hackill
 */
public class FindBongView extends View {
    public static final String TAG = FindBongView.class.getSimpleName();

    private static final int DEGREE_TIMES = 80;//把圆分成多少份
    private static final int LINE_WIDTH = 3;//齿轮的宽度
    private static final int LINE_WIDTH2 = 1;//十字线的宽度

    private Paint mPiePaint;
    private Paint mRingPiePaint;
    private Paint mPathPaint;
    private Paint mPathGrayPaint;
    private Paint mPathLinePaint;

    private int viewSize = 0;
    private Point centerPoint = new Point();
    private Status currentStatus = Status.STOP;
    private Line currentLine = Line.LINE;

    // 搜索动画圆圈宽度
    private float mStrokeWidth = 5;

    float minRadius = 0;
    float degree = 0;
    float runRadius = 0;

    float[] lineOnePaints = new float[DEGREE_TIMES * 4];
    float[] lineTwoPaints = new float[DEGREE_TIMES * 4];
    float[] lineThreePaints = new float[DEGREE_TIMES * 4];


    public FindBongView(Context context) {
        this(context, null);
    }

    public FindBongView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FindBongView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = getDefaultSize(getSuggestedMinimumWidth(), heightMeasureSpec);

        int size = Math.min(width, height);

        viewSize = size;

        centerPoint.set(size / 2, size / 2);

        mStrokeWidth = size * 0.06f;

        initPaint();

        runRadius = 0;

        setMeasuredDimension(size, size);
    }

    private void initPaint() {

        // 圆环画笔
        int color = getContext().getResources().getColor(R.color.text_color_yellow);
        mRingPiePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRingPiePaint.setColor(color);
        mRingPiePaint.setStyle(Paint.Style.STROKE);
        mRingPiePaint.setStrokeWidth(mStrokeWidth);

        // 全圆画笔
        mPiePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPiePaint.setColor(color);
        mPiePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPiePaint.setStrokeWidth(mStrokeWidth);

        // 白色圈画笔
        color = getContext().getResources().getColor(R.color.white);
        mPathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPathPaint.setColor(color);
        mPathPaint.setStyle(Paint.Style.STROKE);
        mPathPaint.setStrokeWidth(LINE_WIDTH);

        // 灰色圈画笔
        color = getContext().getResources().getColor(R.color.disable);
        mPathGrayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPathGrayPaint.setColor(color);
        mPathGrayPaint.setStyle(Paint.Style.STROKE);
        mPathGrayPaint.setStrokeWidth(LINE_WIDTH);


        //十字线画笔
        color = getContext().getResources().getColor(R.color.disable_line);
        mPathLinePaint = new Paint((Paint.ANTI_ALIAS_FLAG));
        mPathLinePaint.setColor(color);
        mPathLinePaint.setStyle(Paint.Style.STROKE);
        mPathLinePaint.setStrokeWidth(LINE_WIDTH2);

        initPoints();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i(TAG, "onDraw: .......");
        if (currentStatus == Status.STOP) {
            //默认状态
            canvas.drawCircle(centerPoint.x, centerPoint.y, (viewSize - mStrokeWidth) / 4, mPiePaint);
        } else if (currentStatus == Status.ENLARGE) {
            canvas.drawCircle(centerPoint.x, centerPoint.y, minRadius, mRingPiePaint);
            canvas.drawCircle(centerPoint.x, centerPoint.y, minRadius * 2, mRingPiePaint);
            canvas.drawCircle(centerPoint.x, centerPoint.y, minRadius * 3, mRingPiePaint);
        } else if (currentStatus == Status.TRAIL) {

            // 将画布圆心拖至中点
            canvas.translate(centerPoint.x, centerPoint.y);
            // 画十字线
            canvas.drawLine(0, -viewSize / 2, 0, viewSize / 2, mPathLinePaint);
            canvas.drawLine(-viewSize / 2, 0, viewSize / 2, 0, mPathLinePaint);
            // 三个圈
            if (currentLine == Line.LINE_ONE) {
                canvas.drawLines(lineOnePaints, mPathPaint);
                canvas.drawLines(lineTwoPaints, mPathGrayPaint);
                canvas.drawLines(lineThreePaints, mPathGrayPaint);
            } else if (currentLine == Line.LINE_TWO) {
                canvas.drawLines(lineOnePaints, mPathGrayPaint);
                canvas.drawLines(lineTwoPaints, mPathPaint);
                canvas.drawLines(lineThreePaints, mPathGrayPaint);
            } else if (currentLine == Line.LINE_THREE) {
                canvas.drawLines(lineOnePaints, mPathGrayPaint);
                canvas.drawLines(lineTwoPaints, mPathGrayPaint);
                canvas.drawLines(lineThreePaints, mPathPaint);
            } else {
                canvas.drawLines(lineOnePaints, mPathGrayPaint);
                canvas.drawLines(lineTwoPaints, mPathGrayPaint);
                canvas.drawLines(lineThreePaints, mPathGrayPaint);
            }
            // 旋转的小球
            if (runRadius > 0) {
                canvas.rotate(degree, 0, 0);
                canvas.drawCircle(0, runRadius, 10, mPiePaint);
            }
        }
    }



    //生成绘制圆形齿轮的值
    private void initPoints() {
        double degree = Math.PI * 2.0 / DEGREE_TIMES;

        float radius = (viewSize - mStrokeWidth * 2) / 6;

        float startY = radius - mStrokeWidth / 2;
        float endY = radius + mStrokeWidth / 2;
        float startY2 = 2 * radius - mStrokeWidth / 2;
        float endY2 = 2 * radius + mStrokeWidth / 2;
        float startY3 = 3 * radius - mStrokeWidth / 2;
        float endY3 = 3 * radius + mStrokeWidth / 2;

        for (int i = 0; i < DEGREE_TIMES; i++) {
            float tempDegree = (float) degree * i;
            lineOnePaints[4 * i] = (float) (startY * Math.sin(tempDegree));
            lineOnePaints[4 * i + 1] = (float) (startY * Math.cos(tempDegree));
            lineOnePaints[4 * i + 2] = (float) (endY * Math.sin(tempDegree));
            lineOnePaints[4 * i + 3] = (float) (endY * Math.cos(tempDegree));

            lineTwoPaints[4 * i] = (float) (startY2 * Math.sin(tempDegree));
            lineTwoPaints[4 * i + 1] = (float) (startY2 * Math.cos(tempDegree));
            lineTwoPaints[4 * i + 2] = (float) (endY2 * Math.sin(tempDegree));
            lineTwoPaints[4 * i + 3] = (float) (endY2 * Math.cos(tempDegree));

            lineThreePaints[4 * i] = (float) (startY3 * Math.sin(tempDegree));
            lineThreePaints[4 * i + 1] = (float) (startY3 * Math.cos(tempDegree));
            lineThreePaints[4 * i + 2] = (float) (endY3 * Math.sin(tempDegree));
            lineThreePaints[4 * i + 3] = (float) (endY3 * Math.cos(tempDegree));
        }
    }

    private AnimatorSet mAnimatorSet = new AnimatorSet();
    private AnimatorSet mAnimatorRunSet = new AnimatorSet();


    public void startAnimator() {
        currentStatus = Status.ENLARGE;
        currentLine = Line.LINE;
        runRadius = 0;
        smoothRefreshSearch();
    }

    public void setLine(Line line) {
        currentLine = line;
        if (mAnimatorSet != null && mAnimatorSet.isRunning()) {
            invalidate();
        } else {
            smoothRunBall();
        }
        if (line == Line.LINE_ONE) {
            smoothRunRadius(runRadius, minRadius);
        } else if (line == Line.LINE_TWO) {
            smoothRunRadius(runRadius, minRadius * 2);
        } else {
            smoothRunRadius(runRadius, minRadius * 3);
        }
    }

    public void stopAnimator() {
        currentStatus = Status.STOP;
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
            mAnimatorSet = null;
        }
        if (mAnimatorRunSet != null) {
            mAnimatorRunSet.cancel();
            mAnimatorRunSet = null;
        }
        invalidate();
    }

    private void smoothRefreshSearch() {
        float start = (viewSize - mStrokeWidth) / 12;
        float end = (viewSize - mStrokeWidth * 2) / 6;
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "minRadius", start, end).setDuration(300);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                currentStatus = Status.TRAIL;
                invalidate();
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

    private void smoothRunBall() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "degree", 0, 360).setDuration(2500);
        animator.setInterpolator(new TimeInterpolator() {
            @Override
            public float getInterpolation(float input) {
                return input;
            }
        });
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        if (mAnimatorSet != null && mAnimatorSet.isRunning()) {
            mAnimatorSet.cancel();
            mAnimatorSet = null;
        }
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.play(animator);
        mAnimatorSet.start();
    }

    private void smoothRunRadius(float from, float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "runRadius", from, to).setDuration(500);
        animator.setInterpolator(new TimeInterpolator() {
            @Override
            public float getInterpolation(float input) {
                return input;
            }
        });
        if (mAnimatorRunSet != null && mAnimatorRunSet.isRunning()) {
            mAnimatorRunSet.cancel();
            mAnimatorRunSet = null;
        }
        mAnimatorRunSet = new AnimatorSet();
        mAnimatorRunSet.play(animator);
        mAnimatorRunSet.start();
    }


    public void setMinRadius(float minRadius) {
        this.minRadius = minRadius;
        invalidate();
    }


    public void setDegree(float degree) {
        this.degree = degree;
        invalidate();
    }

    public void setRunRadius(float runRadius) {
        this.runRadius = runRadius;
    }
}

enum Status {
    STOP, ENLARGE, TRAIL
}

enum Line {
    LINE, LINE_ONE, LINE_TWO, LINE_THREE
}

