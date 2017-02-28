package com.hackill.demo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author hackill
 */
public class LineLoadingView extends View {
    public static final String TAG = LineLoadingView.class.getSimpleName();

    private Paint mCirclePaint;
    private Paint mCirclePaint2;
    private Paint mCirclePaint3;

    private int viewWith = 0;
    private int viewHeight = 0;


    public LineLoadingView(Context context) {
        this(context, null);
    }

    public LineLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = getDefaultSize(getSuggestedMinimumWidth(), heightMeasureSpec);

        viewWith = width;
        viewHeight = height;

        initPaint();

        setMeasuredDimension(width, height);
    }


    private void initPaint() {

        int color = getContext().getResources().getColor(R.color.text_color_yellow);

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(color);
        mCirclePaint.setStyle(Paint.Style.FILL);

        mCirclePaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint2.setColor(color);
        mCirclePaint2.setStyle(Paint.Style.FILL);

        mCirclePaint3 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint3.setColor(color);
        mCirclePaint3.setStyle(Paint.Style.FILL);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        calcCirclePaint(count % 6);
        canvas.drawCircle(viewWith / 2, viewHeight / 6, viewWith / 3, mCirclePaint);
        canvas.drawCircle(viewWith / 2, viewHeight / 2, viewWith / 3, mCirclePaint2);
        canvas.drawCircle(viewWith / 2, viewHeight * 5 / 6, viewWith / 3, mCirclePaint3);
    }

    private void calcCirclePaint(int count) {
        switch (count) {
            case 0:
                mCirclePaint.setColor(getContext().getResources().getColor(R.color.point_one));
                mCirclePaint2.setColor(getContext().getResources().getColor(R.color.point_one));
                mCirclePaint3.setColor(getContext().getResources().getColor(R.color.point_one));
                break;
            case 1:
                mCirclePaint.setColor(getContext().getResources().getColor(R.color.point_two));
                mCirclePaint2.setColor(getContext().getResources().getColor(R.color.point_one));
                mCirclePaint3.setColor(getContext().getResources().getColor(R.color.point_one));
                break;
            case 2:
                mCirclePaint.setColor(getContext().getResources().getColor(R.color.point_three));
                mCirclePaint2.setColor(getContext().getResources().getColor(R.color.point_two));
                mCirclePaint3.setColor(getContext().getResources().getColor(R.color.point_one));
                break;
            case 3:
                mCirclePaint.setColor(getContext().getResources().getColor(R.color.point_one));
                mCirclePaint2.setColor(getContext().getResources().getColor(R.color.point_three));
                mCirclePaint3.setColor(getContext().getResources().getColor(R.color.point_two));
                break;
            case 4:
                mCirclePaint.setColor(getContext().getResources().getColor(R.color.point_one));
                mCirclePaint2.setColor(getContext().getResources().getColor(R.color.point_one));
                mCirclePaint3.setColor(getContext().getResources().getColor(R.color.point_three));
                break;
            default:
                mCirclePaint.setColor(getContext().getResources().getColor(R.color.point_one));
                mCirclePaint2.setColor(getContext().getResources().getColor(R.color.point_one));
                mCirclePaint3.setColor(getContext().getResources().getColor(R.color.point_one));
                break;
        }

    }

    public void startSearchAnimation() {

        stopSearchAnimation();

        timerTask = new TimerTask() {
            @Override
            public void run() {
                count++;
                postInvalidate();
            }
        };

        timer = new Timer();
        timer.schedule(timerTask, 100, 150);
    }

    public void stopSearchAnimation() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        count = 0;
        invalidate();
    }


    private Timer timer;
    private TimerTask timerTask;

    private int count = 0;

}
