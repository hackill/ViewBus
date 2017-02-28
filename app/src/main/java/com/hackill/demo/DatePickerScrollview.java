package com.hackill.demo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hackill.util.DisplayUtil;

import java.util.ArrayList;
import java.util.List;


public class DatePickerScrollview extends HorizontalScrollView {

    public final static String TAG = DatePickerScrollview.class.getSimpleName();

    public final static int ONE_PAGE_COUNT = 7;


    private LinearLayout.LayoutParams defaultTabLayoutParams;

    private LinearLayout tabsContainer;

    private int tabCount;

    private int currentPosition = 0;

    private int tabTextSize = 13;
    private int tabTextColor = 0xFFFF00FF;
    private int lastScrollX = 0;
    private int tabSelectTextColor = 0xFF0000FF;


    private List<String> items = new ArrayList<>();

    private int viewWidth = 0;

    public DatePickerScrollview(Context context) {
        this(context, null);
    }

    public DatePickerScrollview(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DatePickerScrollview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        setFillViewport(true);
        setWillNotDraw(false);

        for (int i = 0; i < 30; i++) {
            items.add(i + "月");
        }

        tabsContainer = new LinearLayout(getContext());
        tabsContainer.setOrientation(LinearLayout.HORIZONTAL);

        viewWidth = DisplayUtil.getScreenWidthPixels(getContext()) - (int) DisplayUtil.dp2Px(getContext(), 30);

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.width = 2 * viewWidth;
        tabsContainer.setLayoutParams(params);
        //让最后一个居中显示
        tabsContainer.setPadding((viewWidth / 2 - viewWidth / 14), 0, (viewWidth / 2 - viewWidth / 14), 0);

        addView(tabsContainer);

        defaultTabLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        defaultTabLayoutParams.width = viewWidth / ONE_PAGE_COUNT;

        notifyDataSetChanged();
    }

    public void setSelectItem(int index) {
        if (index >= 0) {
            currentPosition = index;
            scrollToChild(index, 0);
            updateSelectTabStyles(index, 0);
        }
    }

    public void notifyDataSetChanged() {

        tabsContainer.removeAllViews();
        tabCount = items.size();

        for (int i = 0; i < tabCount; i++) {
            addTextTab(i, items.get(i));
        }

        updateTabStyles();

        //默认最后一个
        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @SuppressLint("NewApi")
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                currentPosition = tabCount - 1;
                setSelectItem(currentPosition);
            }
        });

    }

    private void addTextTab(final int position, String title) {

        TextView tab = new TextView(getContext());
        tab.setText(title);
        tab.setGravity(Gravity.CENTER);
        tab.setSingleLine();

        addTab(position, tab);
    }

    private void addTab(final int position, View tab) {
        tab.setFocusable(true);
        tabsContainer.addView(tab, position, defaultTabLayoutParams);
    }

    private void updateTabStyles() {

        for (int i = 0; i < tabCount; i++) {

            View v = tabsContainer.getChildAt(i);

            if (v instanceof TextView) {

                TextView tab = (TextView) v;
                tab.setTextSize(TypedValue.COMPLEX_UNIT_DIP, tabTextSize);
                tab.setTextColor(tabTextColor);
            }
        }

    }

    private void updateSelectTabStyles(int position, float positionOffset) {

        if (positionOffset > 0.6) {
            position = position + 1;
        }
        if (position >= tabCount) {
            position = tabCount - 1;
        }

        for (int i = 0; i < tabCount; i++) {

            View v = tabsContainer.getChildAt(i);

            if (v instanceof TextView) {
                TextView tab = (TextView) v;
                if (i == position) {
                    tab.setTextSize(TypedValue.COMPLEX_UNIT_DIP, tabTextSize + 3);
                    tab.setTextColor(tabSelectTextColor);
                } else {
                    tab.setTextSize(TypedValue.COMPLEX_UNIT_DIP, tabTextSize);
                    tab.setTextColor(tabTextColor);
                }
            }
        }

    }

    /**
     * @param position 当前位置
     * @param offset   偏移量
     */
    private void scrollToChild(int position, int offset) {

        if (tabCount == 0) {
            return;
        }
        int newScrollX = tabsContainer.getChildAt(position).getLeft() + offset;

        if (position >= 0 || offset > 0) {
            newScrollX -= (viewWidth / 2 - viewWidth / 14);
        }

        if (newScrollX != lastScrollX) {
            lastScrollX = newScrollX;
            smoothScrollTo(newScrollX, 0);
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        int mask = ev.getAction() & MotionEvent.ACTION_MASK;
        switch (mask) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
//                float x = ev.getX();
//                float y = ev.getY();
//                Log.i(TAG, "onTouchEvent: .. x  - " + x + ", y= " + y + ", currentLeft = " + currentLeft);
//
//
//                for (int i = 0; i < tabCount; i++) {
//
//                    View v = tabsContainer.getChildAt(i);
//
//                    if (v.getLeft() < currentLeft && v.getRight() > currentLeft) {
//                        currentPosition = i;
//                        Log.i(TAG, "onTouchEvent: currentPosition = " + currentPosition);
//                    }
//                    Log.i(TAG, "onTouchEvent: i = " + i + ", v left = " + v.getLeft() + ", right = " + v.getRight());
//                }
//                break;
        }

        return true;
    }
}