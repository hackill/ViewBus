package com.hackill.body;

/**
 * Created by hackill on 16/6/2.
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Shader.TileMode;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Test extends TextView {
    private LinearGradient mGradient;
    private int[] mGradientColors = { Color.RED, Color.rgb(255, 165, 0),
            Color.YELLOW, Color.GREEN, Color.rgb(0, 255, 255), Color.BLUE,
            Color.rgb(160, 32, 240) };
    private int mTitleWidth;
    private String mTitle = "三十六天罡,七十二地煞,乃是天地正邪之氣所生";

    public Test(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT));
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        measure(0, 0);

        Paint paint = new Paint();
        paint.setStyle(Style.FILL_AND_STROKE);
        mGradient = new LinearGradient(0, 0, getWidth(), getMeasuredHeight(),
                mGradientColors, null, TileMode.REPEAT);
        paint.setShader(mGradient);

        mTitleWidth = 0;
        float[] width = new float[mTitle.length()];
        paint.getTextWidths(mTitle, width);
        Log.i("Tag", "Width.length= " + width.length);
        int start = 0;
        int offsetY = 10;
        for (int j = 0; j < mTitle.length(); j++) {
            mTitleWidth += (int) Math.ceil(width[j]);
            if (mTitleWidth > getWidth()) {
                canvas.drawText(mTitle.substring(start, j), 0, offsetY, paint);
                mTitleWidth = 0;
                start = start + j;
                offsetY = offsetY + getMeasuredHeight() + 5;
                j--;
            }
        }
        if (mTitleWidth > 0) {
            canvas.drawText(mTitle.substring(start), 0, offsetY, paint);
        }
    }

}