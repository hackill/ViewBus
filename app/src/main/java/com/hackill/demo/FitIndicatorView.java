package com.hackill.demo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.hackill.util.BitmapUtil;
import com.hackill.util.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * fit 体重数据指标
 * Created by hackill
 */

public class FitIndicatorView extends View {

    private static final String TAG = "FitIndicatorView";

    private Paint mBlockPaint;
    private Paint mTextPaint;
    private Paint mAreaPaint;
    private RectF mLineRectF = new RectF();
    private RectF mFaceRectF = new RectF();
    private RectF mBlockBounds = new RectF();

    private List<RectF> mRectFs = new ArrayList<>();
    private DataStruct mDataStruct;

    private int mType0Color = Color.parseColor("#CCCCCC");
    private int mType1Color = Color.parseColor("#B06BFF");
    private int mType2Color = Color.parseColor("#08CFA8");
    private int mType3Color = Color.parseColor("#FFDA00");
    private int mType4Color = Color.parseColor("#F59704");
    private int mType5Color = Color.parseColor("#F03705");

    public FitIndicatorView(Context context) {
        this(context, null);
    }

    public FitIndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FitIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = getDefaultSize(getSuggestedMinimumWidth(), heightMeasureSpec);

        mBlockBounds.top = height / 2 - DisplayUtil.dp2Px(getContext(), 5);
        mBlockBounds.bottom = height / 2 + DisplayUtil.dp2Px(getContext(), 5);
        mBlockBounds.left = 0;
        mBlockBounds.right = width;


        mLineRectF.bottom = mBlockBounds.top;
        mLineRectF.top = mBlockBounds.top * 2 / 3;

        mFaceRectF.top = 0;
        mFaceRectF.bottom = mBlockBounds.top * 2 / 3;

        float size = Math.min(mFaceRectF.height(), DisplayUtil.dp2Px(getContext(), 26));
        mFaceRectF.top = mBlockBounds.top * 2 / 3 - size;

        setMeasuredDimension(width, height);
    }

    private void initPaint() {

        mBlockPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBlockPaint.setColor(mType0Color);
        mBlockPaint.setStyle(Paint.Style.FILL);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setColor(Color.parseColor("#666666"));
        mTextPaint.setTextSize(DisplayUtil.dp2Px(getContext(), 13));

        mAreaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAreaPaint.setStyle(Paint.Style.FILL);
        mAreaPaint.setColor(Color.parseColor("#999999"));
        mAreaPaint.setTextSize(DisplayUtil.dp2Px(getContext(), 12));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }
//
//    private void initData() {
//        FitIndicatorView.DataStruct dataStruct = new FitIndicatorView.DataStruct();
//        List<FitIndicatorView.DataArea> dataAreaList = new ArrayList<>();
//        List<String> displayList = new ArrayList<>();
//        List<Integer> levelList = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
//
//        for (int i = 0; i < 5; i++) {
//            FitIndicatorView.DataArea dataArea = new FitIndicatorView.DataArea();
//            String dispaly = null;
//            if (i == 0) {
//                dataArea.setEndBorder(true);
//                dataArea.setEnd(18.5f);
//                dispaly = "偏低";
//            } else if (i == 1) {
//                dataArea.setStart(18.6f);
//                dataArea.setEnd(24);
//                dispaly = "正常";
//            } else if (i == 2) {
//                dataArea.setStart(24.1f);
//                dataArea.setEnd(28f);
//                dispaly = "偏胖";
//            } else if (i == 3) {
//                dataArea.setStart(28.1f);
//                dataArea.setEnd(35f);
//                dispaly = "肥胖";
//            } else if (i == 4) {
//                dataArea.setStart(35.1f);
//                dataArea.setStartBorder(true);
//                dispaly = "中毒肥胖";
//            }
//            dataAreaList.add(dataArea);
//            displayList.add(dispaly);
//        }
//        dataStruct.setData(22.1f);
//
//        dataStruct.setDataAreaList(dataAreaList);
//        dataStruct.setDisplayList(displayList);
//        dataStruct.setStatusList(levelList);
//        setDataStruct(dataStruct);
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mRectFs.size() > 0) {
            int length = mRectFs.size();
            float data = mDataStruct.getData();
            for (int i = 0; i < length; i++) {

                RectF rectF = mRectFs.get(i);

                mBlockPaint.setColor(mType0Color);

                DataArea dataArea = mDataStruct.getDataAreaList().get(i);
                if (data > 0) {
                    if (dataArea.isEndBorder() && data <= dataArea.getEnd()) {
                        mBlockPaint.setColor(getTypeColor(mDataStruct.getStatusList().get(i)));

                        float position = rectF.left + rectF.width() / 2;
                        drawFace(canvas, mDataStruct.getStatusList().get(i), position);
                    }
                    if (dataArea.isStartBorder() && data >= dataArea.getStart()) {
                        mBlockPaint.setColor(getTypeColor(mDataStruct.getStatusList().get(i)));
                        float position = rectF.left + rectF.width() / 2;
                        drawFace(canvas, mDataStruct.getStatusList().get(i), position);
                    }
                    if (!dataArea.isStartBorder() && !dataArea.isEndBorder()) {
                        if (data >= dataArea.getStart() && data <= dataArea.getEnd()) {
                            mBlockPaint.setColor(getTypeColor(mDataStruct.getStatusList().get(i)));
                            float position = rectF.left + (data - dataArea.getStart()) / (dataArea.getEnd() - dataArea.getStart()) * rectF.width();
                            drawFace(canvas, mDataStruct.getStatusList().get(i), position);
                        }
                    }
                }
                canvas.drawRect(mRectFs.get(i), mBlockPaint);
                drawText(canvas, rectF, mDataStruct.getDisplayList().get(i), mDataStruct.getDataAreaList().get(i));
            }
        }
    }

    private void drawText(Canvas canvas, RectF rectF, String display, DataArea dataArea) {
        Rect rect = new Rect();
        mTextPaint.getTextBounds(display, 0, display.length(), rect);
        canvas.drawText(display, rectF.left, rectF.bottom + rect.height() + DisplayUtil.dp2Px(getContext(), 4), mTextPaint);

        String areaStr = "";
        if (dataArea.isEndBorder()) areaStr = "<" + dataArea.getEnd();
        if (dataArea.isStartBorder()) areaStr = ">" + dataArea.getStart();
        if (!dataArea.isStartBorder() && !dataArea.isEndBorder())
            areaStr = dataArea.getStart() + "-" + dataArea.getEnd();

        Rect rect2 = new Rect();
        mAreaPaint.getTextBounds(areaStr, 0, areaStr.length(), rect2);

        canvas.drawText(areaStr, rectF.left, rectF.bottom + rect.height() + DisplayUtil.dp2Px(getContext(), 12) + rect2.height(), mAreaPaint);
    }

    private void drawFace(Canvas canvas, int type, float position) {

        mFaceRectF.left = position - mFaceRectF.height() / 2;
        mFaceRectF.right = position + mFaceRectF.height() / 2;

        mLineRectF.left = position - DisplayUtil.dp2Px(getContext(), 1);
        mLineRectF.right = position + DisplayUtil.dp2Px(getContext(), 1);

        Bitmap bitmap = BitmapUtil.drawableToBitmap(getResources().getDrawable(getFaceResId(type)));


        canvas.drawBitmap(bitmap, null, mFaceRectF, new Paint());
        canvas.drawRect(mLineRectF, mBlockPaint);
    }

    private int getFaceResId(int type) {

        switch (type) {
            case 1:
                return R.drawable.face_1;
            case 2:
                return R.drawable.face_2;
            case 3:
                return R.drawable.face_3;
            case 4:
                return R.drawable.face_4;
            case 5:
                return R.drawable.face_5;
            case 6:
                return R.drawable.face_6;
        }
        return R.drawable.face_2;
    }

    private int getTypeColor(int type) {
        switch (type) {
            case 0:
                return mType0Color;
            case 1:
                return mType1Color;
            case 2:
                return mType2Color;
            case 3:
                return mType3Color;
            case 4:
                return mType4Color;
            case 5:
            case 6:
                return mType5Color;
        }
        return mType0Color;
    }

    public void setDataStruct(DataStruct dataStruct) {
        this.mDataStruct = dataStruct;
        calculateRectF();
        invalidate();
    }


    private void calculateRectF() {
        mRectFs.clear();

        int size = mDataStruct.getSize();

        RectF tmp = new RectF();
        tmp.set(mBlockBounds);

        for (int i = 0; i < size; i++) {
            RectF block = new RectF(tmp);
            if (i == size - 1) {
                block.right = block.left + mBlockBounds.width() / size;
            } else {
                block.right = block.left + mBlockBounds.width() / size - DisplayUtil.dp2Px(getContext(), 1.5f);
            }
            tmp.set(block);
            tmp.left = tmp.right + DisplayUtil.dp2Px(getContext(), 1.5f);
            mRectFs.add(block);
        }
    }

    public static class DataStruct {
        private List<String> displayList;
        private List<DataArea> dataAreaList;
        private List<Integer> statusList;

        private float data;

        public float getData() {
            return data;
        }

        public void setData(float data) {
            this.data = data;
        }

        private int getSize() {
            return dataAreaList.size();
        }

        public List<String> getDisplayList() {
            return displayList;
        }

        public void setDisplayList(List<String> displayList) {
            this.displayList = displayList;
        }

        public List<DataArea> getDataAreaList() {
            return dataAreaList;
        }

        public void setDataAreaList(List<DataArea> dataAreaList) {
            this.dataAreaList = dataAreaList;
        }

        public List<Integer> getStatusList() {
            return statusList;
        }

        public void setStatusList(List<Integer> statusList) {
            this.statusList = statusList;
        }
    }

    public static class DataArea {
        private float start;
        private boolean startBorder = false;
        private float end;
        private boolean endBorder = false;
        private boolean percent = false;

        public float getStart() {
            return start;
        }

        public void setStart(float start) {
            this.start = start;
        }

        public boolean isStartBorder() {
            return startBorder;
        }

        public void setStartBorder(boolean startBorder) {
            this.startBorder = startBorder;
        }

        public float getEnd() {
            return end;
        }

        public void setEnd(float end) {
            this.end = end;
        }

        public boolean isEndBorder() {
            return endBorder;
        }

        public void setEndBorder(boolean endBorder) {
            this.endBorder = endBorder;
        }
    }

}

