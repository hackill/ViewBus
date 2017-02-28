package com.hackill.body;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import com.hackill.demo.R;
import com.hackill.util.DisplayUtil;


public class CircleWaveLoadView extends View {

    public final static String TAG = CircleWaveLoadView.class.getSimpleName();
    private static final int WAVE_HEIGHT_LARGE = 16;
    private final static int WAVE_HEIGHT_MIDDLE = 8;
    private final static int WAVE_HEIGHT_LITTLE = 5;

    private final static float WAVE_LENGTH_MULTIPLE_LARGE = 1.5f;
    private final static float WAVE_LENGTH_MULTIPLE_MIDDLE = 1f;
    private final static float WAVE_LENGTH_MULTIPLE_LITTLE = 0.5f;

    private final static float WAVE_HZ_FAST = 0.13f;
    private final static float WAVE_HZ_NORMAL = 0.09f;
    private final static float WAVE_HZ_SLOW = 0.05f;

    public final int DEFAULT_ABOVE_WAVE_ALPHA = 160;
    public final int DEFAULT_BLOW_WAVE_ALPHA = 120;

    private final float X_SPACE = 20;
    private final static double PI2 = 2 * Math.PI;

    private Path mAboveWavePath = new Path();
    private Path mBlowWavePath = new Path();

    private Paint mAboveWavePaint = new Paint();
    private Paint mBlowWavePaint = new Paint();

    private int mAboveWaveColor;
    private int mBlowWaveColor;

    private float mWaveMultiple;
    private float mWaveLength;
    private int mWaveHeight;
    private float mMaxRight;
    private float mWaveHz;

    private Path mCirclePath;
    private RectF mBottomRect;

    // wave animation
    private float mAboveOffset = 0.0f;
    private float mBlowOffset;


    private final int DEFAULT_ABOVE_WAVE_COLOR = Color.RED;
    private final int DEFAULT_BLOW_WAVE_COLOR = Color.BLUE;
    private final int DEFAULT_PROGRESS = 1;


    protected static final int LARGE = 1;
    protected static final int MIDDLE = 2;
    protected static final int LITTLE = 3;

    private int mProgress;

    private float mRotation = 0f;

    private Status currentStatus = Status.NORMAL;

    private int mWaveToTop;

    private RefreshProgressRunnable mRefreshProgressRunnable;

    private int left, right, bottom;
    // ω
    private double omega;

    private RectF mBounds = new RectF();
    private int mStrokeWidth = 5;
    private Paint mProgressPaint;
    private Paint mCirclePaint;
    private Paint mRingPiePaint;
    private Paint mRingPointPaint;

    private Path mRightPath = new Path();
    private float right_X = 0;

    private float offSet_X = 0;
    private float offSet_Y = 0;

    private float startX = 0;
    private float endX = 0;

    public CircleWaveLoadView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.waveViewStyle);
    }

    public CircleWaveLoadView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);


        //load styled attributes.
        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WaveView, R.attr.waveViewStyle, 0);
        mAboveWaveColor = attributes.getColor(R.styleable.WaveView_above_wave_color, DEFAULT_ABOVE_WAVE_COLOR);
        mBlowWaveColor = attributes.getColor(R.styleable.WaveView_blow_wave_color, DEFAULT_BLOW_WAVE_COLOR);
        mProgress = attributes.getInt(R.styleable.WaveView_wave_progress, DEFAULT_PROGRESS);
        int waveHeight = attributes.getInt(R.styleable.WaveView_wave_height, LARGE);
        int waveMultiple = attributes.getInt(R.styleable.WaveView_wave_length, LARGE);
        int waveHz = attributes.getInt(R.styleable.WaveView_wave_hz, MIDDLE);
        attributes.recycle();

        mAboveWaveColor = getContext().getResources().getColor(R.color.btn_bg_yellow);
        mBlowWaveColor = getContext().getResources().getColor(R.color.progress_bg);


        initializeWaveSize(waveMultiple, waveHeight, waveHz);
        initializePainters();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = getDefaultSize(getSuggestedMinimumWidth(), heightMeasureSpec);

        int size = Math.min(width, height);

        mStrokeWidth = (int) Math.min(size * 0.05, DisplayUtil.dp2Px(getContext(), 4));
        mBounds.set(mStrokeWidth / 2, mStrokeWidth / 2, size - mStrokeWidth / 2, size - mStrokeWidth / 2);
        initPaint();

        offSet_X = -size / 10;
        offSet_Y = size / 6;
        startX = -size / 9;
        endX = size / 3f;

        setMeasuredDimension(size, size);
    }


    private void initPaint() {

        int color = getContext().getResources().getColor(R.color.btn_bg_yellow);
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(color);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(mStrokeWidth);
        mCirclePaint.setStrokeCap(Paint.Cap.ROUND);

        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setColor(color);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeWidth(mStrokeWidth);
        mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        SweepGradient gradient = new SweepGradient(mBounds.right / 2f, mBounds.bottom / 2f, getColors(color), new float[]{0f, 0.98f});
        mProgressPaint.setShader(gradient);

        mRingPiePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRingPiePaint.setColor(color);
        mRingPiePaint.setStyle(Paint.Style.STROKE);
        mRingPiePaint.setStrokeWidth(DisplayUtil.dp2Px(getContext(), 4));

        mRingPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRingPointPaint.setColor(color);
        mRingPointPaint.setStyle(Paint.Style.FILL);

    }

    private int[] getColors(int c) {
        int[] result = new int[2];
        int alpha = Color.alpha(c);
        int red = Color.red(c);
        int green = Color.green(c);
        int blue = Color.blue(c);
        result[0] = c;
        result[1] = Color.argb((int) (alpha * 0.0f), red, green, blue);

        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (currentStatus == Status.NORMAL) {

        } else if (currentStatus == Status.PREPARE) {
            //loading页面
            canvas.save();
            canvas.rotate(-mRotation, getWidth() / 2f, getHeight() / 2f);
            canvas.drawArc(mBounds, 0, 360, false, mProgressPaint);
            canvas.restore();
        } else if (currentStatus == Status.PROGRESS) {
            // 绘制一个圆
            //裁剪成圆形
            canvas.drawArc(mBounds, 0, 360, false, mCirclePaint);

            canvas.clipPath(getCirclePath());
            canvas.drawRect(mBottomRect, mBlowWavePaint);
            canvas.drawRect(mBottomRect, mAboveWavePaint);
            canvas.save();
            canvas.translate(0, mWaveToTop - bottom);
            canvas.drawPath(mBlowWavePath, mBlowWavePaint);
            canvas.drawPath(mAboveWavePath, mAboveWavePaint);

            canvas.restore();
            canvas.drawArc(mBounds, 0, 360, false, mCirclePaint);

        } else if (currentStatus == Status.SUCCESS) {
            // 绘制一个圆
            canvas.drawArc(mBounds, 0, 360, false, mCirclePaint);
            canvas.save();
            canvas.translate(getWidth() / 2f, getHeight() / 2f);
            getCurrentRightPath(canvas);
            canvas.drawPath(mRightPath, mRingPiePaint);
            canvas.restore();
        }
    }

    private void getCurrentRightPath(Canvas canvas) {
        mRightPath.reset();

        float xTan = (float) Math.tan(Math.PI / 3.5);
        float radius = DisplayUtil.dp2Px(getContext(), 2);
        //起始点
        mRightPath.moveTo(startX + offSet_X, xTan * startX + offSet_Y);
        canvas.drawCircle(startX + offSet_X, xTan * startX + offSet_Y, radius, mRingPointPaint);
        //落点
        if (right_X <= 0) {
            mRightPath.lineTo(right_X + offSet_X, xTan * right_X + offSet_Y);
            canvas.drawCircle(right_X + offSet_X, xTan * right_X + offSet_Y, radius, mRingPointPaint);
        } else {
            mRightPath.lineTo(0 + offSet_X, 0 + offSet_Y);
            mRightPath.lineTo(right_X + offSet_X, -right_X + offSet_Y);
            canvas.drawCircle(right_X + offSet_X, -right_X + offSet_Y, radius, mRingPointPaint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setProgress(mProgress);

        if (mWaveLength == 0) {
            startWave();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }


    public void setAboveWaveColor(int aboveWaveColor) {
        this.mAboveWaveColor = aboveWaveColor;
    }

    public void setBlowWaveColor(int blowWaveColor) {
        this.mBlowWaveColor = blowWaveColor;
    }

    public Paint getAboveWavePaint() {
        return mAboveWavePaint;
    }

    public Paint getBlowWavePaint() {
        return mBlowWavePaint;
    }

    public void initializeWaveSize(int waveMultiple, int waveHeight, int waveHz) {
        mWaveMultiple = getWaveMultiple(waveMultiple);
        mWaveHeight = getWaveHeight(waveHeight);
        mWaveHz = getWaveHz(waveHz);
        mBlowOffset = mWaveHeight * 0.3f;
    }

    public void initializePainters() {
        mAboveWavePaint.setColor(mAboveWaveColor);
        mAboveWavePaint.setAlpha(DEFAULT_ABOVE_WAVE_ALPHA);
        mAboveWavePaint.setStyle(Paint.Style.FILL);
        mAboveWavePaint.setAntiAlias(true);

        mBlowWavePaint.setColor(mBlowWaveColor);
        mBlowWavePaint.setAlpha(DEFAULT_BLOW_WAVE_ALPHA);
        mBlowWavePaint.setStyle(Paint.Style.FILL);
        mBlowWavePaint.setAntiAlias(true);
    }

    private float getWaveMultiple(int size) {
        switch (size) {
            case LARGE:
                return WAVE_LENGTH_MULTIPLE_LARGE;
            case MIDDLE:
                return WAVE_LENGTH_MULTIPLE_MIDDLE;
            case LITTLE:
                return WAVE_LENGTH_MULTIPLE_LITTLE;
        }
        return 0;
    }

    private int getWaveHeight(int size) {
        switch (size) {
            case LARGE:
                return WAVE_HEIGHT_LARGE;
            case MIDDLE:
                return WAVE_HEIGHT_MIDDLE;
            case LITTLE:
                return WAVE_HEIGHT_LITTLE;
        }
        return 0;
    }

    private float getWaveHz(int size) {
        switch (size) {
            case LARGE:
                return WAVE_HZ_FAST;
            case MIDDLE:
                return WAVE_HZ_NORMAL;
            case LITTLE:
                return WAVE_HZ_SLOW;
        }
        return 0;
    }

    /**
     * calculate wave track
     */
    private void calculatePath() {
        mAboveWavePath.reset();
        mBlowWavePath.reset();

        getWaveOffset();

        float y;
        mAboveWavePath.moveTo(left, bottom);
        for (float x = 0; x <= mMaxRight; x += X_SPACE) {
            y = (float) (mWaveHeight * Math.sin(omega * x + mAboveOffset) + mWaveHeight);
            mAboveWavePath.lineTo(x, y);
        }
        mAboveWavePath.lineTo(right, bottom);

        mBlowWavePath.moveTo(left, bottom);
        for (float x = 0; x <= mMaxRight; x += X_SPACE) {
            y = (float) (mWaveHeight * Math.sin(omega * x + mBlowOffset) + mWaveHeight);
            mBlowWavePath.lineTo(x, y);
        }
        mBlowWavePath.lineTo(right, bottom);
    }

    private void beginLoading() {
        removeCallbacks(mRefreshProgressRunnable);
        mRefreshProgressRunnable = new RefreshProgressRunnable();
        post(mRefreshProgressRunnable);
    }

    private void loadFinish() {
        removeCallbacks(mRefreshProgressRunnable);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }


    private void startWave() {
        if (getWidth() != 0) {
            int width = getWidth();
            mWaveLength = width * mWaveMultiple;
            left = 0;
            right = width;
            bottom = mWaveHeight * 2 + 2;
            mMaxRight = right + X_SPACE;
            omega = PI2 / mWaveLength;
        }
    }

    private void getWaveOffset() {
        if (mBlowOffset > Float.MAX_VALUE - 100) {
            mBlowOffset = 0;
        } else {
            mBlowOffset += mWaveHz;
        }

        if (mAboveOffset > Float.MAX_VALUE - 100) {
            mAboveOffset = 0;
        } else {
            mAboveOffset += mWaveHz;
        }
    }

    private Path getCirclePath() {
        if (mCirclePath == null) {
            mCirclePath = new Path();
            float radius = Math.min(getWidth() / 2f, getHeight() / 2f);
            mCirclePath.addCircle(getWidth() / 2f, getHeight() / 2f, radius, Path.Direction.CCW);
        }

        return mCirclePath;

    }

    public void setProgress(int progress) {
        this.mProgress = progress > 100 ? 100 : progress;
        computeWaveToTop();
    }

    public int getProgress() {
        return mProgress;
    }

    private void computeWaveToTop() {
        mWaveToTop = (int) (getMeasuredHeight() * (1f - mProgress / 100f)) - 1;

        if (mBottomRect == null) {
            mBottomRect = new RectF();
        }

        mBottomRect.set(0, mWaveToTop, getMeasuredWidth(), getMeasuredHeight());

    }

    private class RefreshProgressRunnable implements Runnable {
        public void run() {
            synchronized (CircleWaveLoadView.this) {
                long start = System.currentTimeMillis();

                calculatePath();

                invalidate();

                long gap = 16 - (System.currentTimeMillis() - start);
                postDelayed(this, gap < 0 ? 0 : gap);
            }
        }
    }


    public void startLoading() {
        currentStatus = Status.PREPARE;
        loadFinish();
        smoothRefreshTime();
    }

    public void startProgressAnimator() {
        currentStatus = Status.PROGRESS;
        beginLoading();
    }

    public void startSuccessAnimator() {
        currentStatus = Status.SUCCESS;
        loadFinish();
        smoothRefreshRight();
    }

    private void smoothRefreshRight() {

        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "right_X", startX, endX).setDuration(800);

        if (mAnimatorSet != null && mAnimatorSet.isRunning()) {
            mAnimatorSet.cancel();
            mAnimatorSet = null;
        }
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.play(animator);
        mAnimatorSet.start();
    }

    private AnimatorSet mAnimatorSet = new AnimatorSet();

    private void smoothRefreshTime() {

        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "mRotation", 0, 360).setDuration(1000);

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
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.play(animator);
        mAnimatorSet.start();
    }


    public void setRight_X(float right_X) {
        this.right_X = right_X;
        invalidate();
    }

    public void setMRotation(float mRotation) {
        this.mRotation = mRotation;
        invalidate();
    }

    enum Status {
        NORMAL, PREPARE, PROGRESS, SUCCESS
    }


}
