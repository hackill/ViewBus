package com.hackill.demo;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 内容垂直滚动的一个控件，内容子项项垂直滚动后，将自动把当前离中心最近的项回滚至中心位置。
 * 可根据{@link #getSelectedItem()} 获取当前选择的项，另外可添加选中事件监听器
 * {@link #setOnItemSelectedListener(OnItemSelectedListener)}
 * <p/>
 * This component may contains several sub items, and the items is able to
 * scroll up and down, by once released the scroll bar, the closest item will
 * roll back to the center of component. use {@link #getSelectedItem()} to
 * get the current selected item, and you can always use
 * {@link #setOnItemSelectedListener(OnItemSelectedListener)} to do something
 * after the item is selected.
 *
 * @author Wison
 * @date 2013/09/26
 */
public class VerticalScrollAutoSelector extends LinearLayout {

    private ScrollView mContentScrollView;
    private OnItemSelectedListener mOnItemSelectedListener;

    private AutoSelectorAdapter mAdapter;
    private LinearLayout mItemsContainer;
    private ViewGroup.LayoutParams mItemLayoutParams;
    private TextView mStartBlankView;
    private TextView mEndBlankView;
    private List<View> mCachedSubViewList = new ArrayList<View>();

    private int[] mItemTextViewsScrollYArr;
    private Point mTouchedPoint = new Point();
    private ScrollPointChecker mScrollPointChecker;
    private int mSelectedPosition = -1;

    public VerticalScrollAutoSelector(Context context) {
        this(context, null);
    }

    @SuppressWarnings("deprecation")
    public VerticalScrollAutoSelector(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContentScrollView = new ScrollView(getContext());
        LinearLayout.LayoutParams linearLP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mContentScrollView.setLayoutParams(linearLP);
        mContentScrollView.setVerticalScrollBarEnabled(false);
        addView(mContentScrollView);

        mStartBlankView = new TextView(context);
        mEndBlankView = new TextView(context);
        mItemLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mItemsContainer = new LinearLayout(context);
        mItemsContainer.setOrientation(LinearLayout.VERTICAL);
        mItemsContainer.setGravity(Gravity.CENTER);
        mItemsContainer.setLayoutParams(mItemLayoutParams);
        mContentScrollView.addView(mItemsContainer);
        mContentScrollView.setOnTouchListener(new TimeScrollViewOnTouchListener());
    }

    /**
     * Register a callback to be invoked when an item in this VerticalScrollAutoSelector has been selected.
     *
     * @param listener The callback that will run
     */
    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        mOnItemSelectedListener = listener;
    }

    /**
     * Sets the data behind this VerticalScrollAutoSelector.
     *
     * @param adapter
     */
    public void setAdapter(AutoSelectorAdapter adapter) {
        mAdapter = adapter;
        mSelectedPosition = -1;
        mItemTextViewsScrollYArr = null;
        mItemsContainer.removeAllViews();
        if (mAdapter == null || mAdapter.getCount() <= 0) {
            return;
        }

        if (getHeight() == 0) {
            // Waiting for component initialization finished
            mContentScrollView.postDelayed(new Thread() {
                @Override
                public void run() {
                    attachAdapter();
                }
            }, 1);
        } else {
            attachAdapter();
        }
    }

    private void attachAdapter() {
        if (getHeight() == 0) {
            // try again!
            setAdapter(mAdapter);
            return;
        }

        final int itemCount = mAdapter.getCount();
        int itemGroup = mAdapter.getItemsCountPerGroup();
        if (itemGroup <= 0) {
            itemGroup = 1;
        }

        final float height = getHeight();

        final int itemHeight = (int) (height / itemGroup);
        int additionHeight = (int) (height - itemHeight * itemGroup);

        int itemPosition = 0;
        final int totalHourItems = itemCount + 2;

        for (int i = 0; i < totalHourItems; i++) {

            if (i == 0 || i == totalHourItems - 1) {
                TextView tv = (i == 0 ? mStartBlankView : mEndBlankView);
                mItemLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                mItemLayoutParams.height = (--additionHeight >= 0) ? itemHeight + 1 : itemHeight;
                tv.setLayoutParams(mItemLayoutParams);
                mItemsContainer.addView(tv);
            } else {
                View convertView = null;
                boolean isCached = true;
                if (itemPosition < mCachedSubViewList.size()) {
                    convertView = mCachedSubViewList.get(itemPosition);
                } else {
                    isCached = false;
                }

                View view = mAdapter.getView(itemPosition, convertView, this);
                view.setId(mAdapter.getItemId(itemPosition));
                mItemLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                mItemLayoutParams.height = (--additionHeight >= 0) ? itemHeight + 1 : itemHeight;
                view.setLayoutParams(mItemLayoutParams);
                mItemsContainer.addView(view);

                if (!isCached) {
                    mCachedSubViewList.add(view);
                } else {
                    if (view != convertView) {
                        mCachedSubViewList.remove(itemPosition);
                        mCachedSubViewList.add(itemPosition, view);
                    }
                }
                itemPosition++;
            }
        }
    }


    /**
     * Returns the adapter currently in use in this VerticalScrollAutoSelector.
     *
     * @return
     */
    public AutoSelectorAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * Get the selected item.
     *
     * @return
     */
    public Object getSelectedItem() {
        if (mAdapter == null || mSelectedPosition < 0) return null;
        return mAdapter.getItem(mSelectedPosition);
    }

    private class TimeScrollViewOnTouchListener implements OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (mAdapter == null || mAdapter.getCount() < 1) {
                return false;
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mItemTextViewsScrollYArr == null) {
                        int maxY = getMaxScrollY();
                        int[] location = new int[2];
                        mContentScrollView.getLocationOnScreen(location);

                        final int itemsCount = getAdapter().getCount();
                        mItemTextViewsScrollYArr = new int[itemsCount];
                        mItemTextViewsScrollYArr[0] = 0;
                        mItemTextViewsScrollYArr[itemsCount - 1] = maxY;

                        for (int i = 0; i < itemsCount - 2; i++) {
                            mItemTextViewsScrollYArr[i + 1] = (int) (1f * (i + 1) * maxY / (itemsCount - 1));
                        }
                    }

                    mTouchedPoint.x = mContentScrollView.getScrollX();
                    mTouchedPoint.y = mContentScrollView.getScrollY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    if (mScrollPointChecker == null) {
                        mScrollPointChecker = new ScrollPointChecker();
                        mScrollPointChecker.execute(mTouchedPoint);
                    } else {
                        mScrollPointChecker.cancel(true);
                        mScrollPointChecker = new ScrollPointChecker();
                        mScrollPointChecker.execute(mTouchedPoint);
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    }

    /**
     * 获得ScrollView最大垂直滚动距离
     *
     * @return
     */
    private int getMaxScrollY() {
        int tmpY = mContentScrollView.getScrollY();

        mContentScrollView.scrollTo(getScrollX(), 5000);
        int maxY = mContentScrollView.getScrollY();

        mContentScrollView.scrollTo(mContentScrollView.getScrollX(), tmpY);
        return maxY;
    }

    private class ScrollPointChecker extends AsyncTask<Point, Integer, Integer> {

        private int oldScrollY = -1;

        @Override
        protected Integer doInBackground(Point... params) {
            if (params == null || params.length < 1) {
                return -1;
            }
            Point originalPoint = params[0];
            int scrollView_y = mContentScrollView.getScrollY();
            if (scrollView_y == originalPoint.y) {
                return -1;
            }

            int currentPosition = -1;

            while (true) {
                scrollView_y = mContentScrollView.getScrollY();

                if (oldScrollY == scrollView_y) {
                    int tempPosition = -1;

                    for (int i = 0; i < getAdapter().getCount(); i++) {
                        Rect visibleRect = new Rect();
                        boolean flag = mCachedSubViewList.get(i).getGlobalVisibleRect(visibleRect);
                        int[] location = new int[2];
                        mCachedSubViewList.get(i).getLocationOnScreen(location);

                        if (flag && Math.abs(visibleRect.top - visibleRect.bottom) == mCachedSubViewList.get(i).getHeight()) {
                            if (tempPosition != -1) {
                                // compare with previous item, get the closer one.
                                if (Math.abs(mItemTextViewsScrollYArr[tempPosition] - scrollView_y) > Math.abs(mItemTextViewsScrollYArr[i] - scrollView_y)) {
                                    tempPosition = i;
                                }
                            } else {
                                tempPosition = i;
                            }
                        }
                    }

                    if (tempPosition != -1) {
                        currentPosition = tempPosition;
                    }
                    break;
                } else {
                    oldScrollY = scrollView_y;
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    return -1;
                }
            }
            return currentPosition;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result != -1) {
                mSelectedPosition = result;
                mContentScrollView.scrollTo(mContentScrollView.getScrollX(), mItemTextViewsScrollYArr[mSelectedPosition]);
                if (mOnItemSelectedListener != null) {
                    mOnItemSelectedListener.onItemSelected(VerticalScrollAutoSelector.this,
                            mCachedSubViewList.get(mSelectedPosition), mSelectedPosition, mAdapter.getItemId(mSelectedPosition));
                }
            } else {
                if (mOnItemSelectedListener != null) {
                    mOnItemSelectedListener.onNothingSelected(VerticalScrollAutoSelector.this);
                }
            }
        }
    }

    /**
     * Interface definition for a callback to be invoked when
     * an item in this view has been selected.
     */
    public interface OnItemSelectedListener {

        void onItemSelected(VerticalScrollAutoSelector parent, View view, int position, int id);

        void onNothingSelected(VerticalScrollAutoSelector parent);
    }

    /**
     * Adapter for VerticalScrollAutoSelector
     *
     * @author Wison
     */
    public abstract static class AutoSelectorAdapter {

        public boolean isEmpty() {
            return getCount() == 0;
        }

        /**
         * Get the count of visible items when the VerticalScrollAutoSelector is displayed on the screen.
         *
         * @return
         */
        public abstract int getItemsCountPerGroup();

        /**
         * Get the count of items
         *
         * @return
         */
        public abstract int getCount();

        /**
         * Get the data item associated with the specified position in the data set.
         *
         * @param position
         * @return
         */
        public abstract Object getItem(int position);

        /**
         * Get the row id associated with the specified position in the list.
         *
         * @param position
         * @return
         */
        public abstract int getItemId(int position);

        /**
         * Get a View that displays the data at the specified position in the data set.
         *
         * @param position
         * @param convertView
         * @param parent
         * @return
         */
        public abstract View getView(int position, View convertView, ViewGroup parent);

    }
}