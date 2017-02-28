package com.hackill.demo;


import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

@SuppressWarnings("deprecation")
public class CycleScrollView<T> extends ViewGroup implements OnGestureListener {

    static final String TAG = "CycleScrollView";
    Context mContext;

    /**
     * Scroll velocity.
     */
    public static final long SCROLL_VELOCITY = 50;

    /**
     * Scroll offset.
     */
    public static final int SCROLL_OFFSET = -1;

    /**
     * Touch delay.
     */
    public static final long TOUCH_DELAYMILLIS = 2000;

    /**
     * Fling duration.
     */
    public static final int FLING_DURATION = 2000;

    /**
     * Filing max velocity x.
     */
    public static final int MAX_VELOCITY_X = 1000;

    private GestureDetector detector;
    private Handler mHandler;
    private Scroller mScroller;

    /**
     * Callback interface adapter and OnItemClick.
     */
    private CycleScrollAdapter<T> mAdapter;
    private OnItemClickListener mOnItemClickListener;

    /**
     * Scroll index
     */
    private int mPreIndex;
    private int mCurrentIndex;
    private int mNextIndex;
    private View mCurrentView;
    private View mPreView;
    private View mNextView;

    private float mLastMotionX;

    // The reLayout is false can not invoke onLayout.
    private boolean reLayout = false;

    // If the item count more than screen that can scroll.
    private boolean canScroll = false;

    // A flag for switch current view.
    private boolean mCurrentViewAtLeft = true;

    // Fling distance.
    private int mFlingX = 0;

    private boolean isMoveAction = false;

    private int defaultItemY = 10;

    private int maxItemCount = 7;

    private int initItemX = 20;

    /**
     * The screen width.
     */
    private int screenWidth;

    /**
     * Item view height.
     */
    private int itemHeight;

    /**
     * Item view width.
     */
    private int itemWidth;

    /**
     * Item view layout x.
     */
    private int itemX = getInitItemX();

    /**
     * Item view layout y.
     */
    private int itemY = defaultItemY;

    // Auto scroll view task.
    private final Runnable mScrollTask = new Runnable() {

        @Override
        public void run() {
            if (canScroll) {
                scrollView(SCROLL_OFFSET);
                mHandler.postDelayed(this, SCROLL_VELOCITY);// Loop self.
            }
        }
    };

    public CycleScrollView(Context context) {
        super(context);
        onCreate(context);
    }

    public CycleScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        onCreate(context);
    }

    public CycleScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        onCreate(context);
    }

    private void onCreate(Context context) {
        mContext = context;
        detector = new GestureDetector(this);
        mHandler = new Handler();
        mScroller = new Scroller(context);
    }

    /**
     * Create scroll index.
     */
    public void createIndex() {
        if (canScroll) {
            mPreIndex = maxItemCount - 1;
            mCurrentIndex = 0;
            mNextIndex = 1;
            mPreView = getChildAt(mPreIndex);
            mCurrentView = getChildAt(mCurrentIndex);
            mNextView = getChildAt(mNextIndex);
        }
    }

    /**
     * Set item click callback.
     *
     * @param onItemClickListener The callback
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    /**
     * Set itemAdapter for addItem and bindItem.
     *
     * @param adapter
     */
    public void setAdapter(CycleScrollAdapter<T> adapter) {
        mAdapter = adapter;
    }

    /**
     * Start auto scroll.
     */
    public void startScroll() {
        if (canScroll) {
            mHandler.post(mScrollTask);
        }
    }

    /**
     * Stop auto scroll and filing scroll task.
     */
    public void stopScroll() {
        mHandler.removeCallbacks(mScrollTask);
    }

    /**
     * Delay start auto scroll task.
     */
    public void delayStartScroll() {
        if (canScroll) {
            mHandler.postDelayed(mScrollTask, TOUCH_DELAYMILLIS);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = this.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = this.getChildAt(i);
            child.measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        /**
         * On layout set the child view layout by x y width and height.
         */
        if (reLayout) {// Run one times.
            for (int i = 0; i < getChildCount(); i++) {
                View child = this.getChildAt(i);
                child.setVisibility(View.VISIBLE);
                child.layout(itemX, getItemY(), itemX + getItemWidth(),
                        getItemY() + getItemHeight());
                itemX += getItemMargin();
            }
            reLayout = !reLayout;
        }
    }

    /**
     * When fling view run the fling task scroll view.
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {

        if (e1 == null || e2 == null) {
            return false;
        }

        // When deltaX and velocityX not good return false.
        if (Math.abs(velocityX) < MAX_VELOCITY_X) {
            return false;
        }

        // Get the delta x.
        float deltaX = (e1.getX() - e2.getX());

        /**
         * If can fling stop other scroll task at first , delay the task after
         * fling.
         */
        mHandler.removeCallbacks(mScrollTask);
        if (canScroll) {
            mHandler.postDelayed(mScrollTask, TOUCH_DELAYMILLIS
                    + FLING_DURATION - 1000);
        }

        /**
         * The flingX is fling distance.
         */
        mFlingX = (int) deltaX;

        // Start scroll with fling x.
        mScroller.startScroll(0, 0, mFlingX, 0, FLING_DURATION);
        return false;
    }

    @Override
    public void computeScroll() {
        if (canScroll && mScroller.computeScrollOffset()) {
            /**
             * The Scroller.getCurrX() approach mFlingX , the deltaX more and
             * more small.
             */
            int deltaX = mFlingX - mScroller.getCurrX();
            scrollView(-deltaX / 10);
            postInvalidate();
        }
    }

    /**
     * When touch event is move scroll child view.
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        // Get event x,y at parent view.
        final float x = ev.getX();

        /**
         * Get event x,y at screen.
         */
        final int rawX = (int) ev.getRawX();
        final int rawY = (int) ev.getRawY();

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Reset isMoveAction.
                isMoveAction = false;
                // Get motionX.
                mLastMotionX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                // When action move set isMoveAction true.
                isMoveAction = true;
                // Only support one pointer.
                if (ev.getPointerCount() == 1) {
                    // Compute delta X.
                    int deltaX = 0;
                    deltaX = (int) (x - mLastMotionX);
                    mLastMotionX = x;
                    // When canScroll is true, scrollView width deltaX.
                    if (canScroll) {
                        scrollView(deltaX);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                /**
                 * If not move find click item and invoke click event.
                 */
                if (!isMoveAction) {
                    View view = getClickItem(rawX, rawY);
                    if (view != null) {
                        mOnItemClickListener.onItemClick(Integer.valueOf(view
                                .getTag().toString()));
                    }
                }
                break;
        }
        return this.detector.onTouchEvent(ev);
    }

    /**
     * Get click item view by rawX and rawY.
     *
     * @param rawX the x at screen.
     * @param rawY the y at screen.
     * @return the click item view.
     */
    private View getClickItem(final int rawX, final int rawY) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            // Get item view rect.
            Rect rect = new Rect();
            child.getGlobalVisibleRect(rect);
            // If click point on the item view, invoke the click event.
            if (rect.contains(rawX, rawY)) {
                return child;
            }
        }
        return null;
    }

    /**
     * Scroll view by delta x.
     *
     * @param deltaX The scroll distance.
     */
    private void scrollView(int deltaX) {
        // Move child view by deltaX.
        moveChildView(deltaX);
        // After move change index.
        if (deltaX < 0) {// move left
            // If current at right switch current view to left.
            switchCurrentViewToLeft();
            // change previous current next index.
            moveToNext();
        } else {// move right
            // If current at left switch current view to right.
            switchCurrentViewToRight();
            // change previous current next index.
            moveToPre();
        }
        invalidate();
    }

    /**
     * Move view by delta x.
     *
     * @param deltaX The move distance.
     */
    private void moveChildView(int deltaX) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.layout(child.getLeft() + deltaX, child.getTop(),
                    child.getRight() + deltaX, child.getBottom());
        }
    }

    /**
     * Current event is move to left, if current view at right switch current
     * view to left.
     */
    private void switchCurrentViewToLeft() {
        if (!mCurrentViewAtLeft) {
            mPreIndex = mCurrentIndex;
            mCurrentIndex = mNextIndex;
            mNextIndex++;
            if (mNextIndex > maxItemCount - 1) {
                mNextIndex = 0;
            }
            mCurrentView = getChildAt(mCurrentIndex);
            mPreView = getChildAt(mPreIndex);
            mNextView = getChildAt(mNextIndex);
            mCurrentViewAtLeft = !mCurrentViewAtLeft;
        }
    }

    /**
     * Current event is move to right, if current view at left switch current
     * view to right.
     */
    private void switchCurrentViewToRight() {
        if (mCurrentViewAtLeft) {
            mNextIndex = mCurrentIndex;
            mCurrentIndex = mPreIndex;
            mPreIndex--;
            if (mPreIndex < 0) {
                mPreIndex = maxItemCount - 1;
            }
            mCurrentView = getChildAt(mCurrentIndex);
            mPreView = getChildAt(mPreIndex);
            mNextView = getChildAt(mNextIndex);
            mCurrentViewAtLeft = !mCurrentViewAtLeft;
        }
    }

    /**
     * Current event is move to left,if current view move out of screen move the
     * current view to right and reBind the item change index.
     */
    private void moveToNext() {
        if (mCurrentView.getRight() < 0) {
            mCurrentView.layout(mPreView.getLeft() + getItemMargin(),
                    getItemY(), mPreView.getLeft() + getItemMargin()
                            + getItemWidth(), getItemY() + getItemHeight());

            if (mCurrentView.getTag() != null) {
                int listIndex = (Integer) mCurrentView.getTag();
                int index = (listIndex + maxItemCount) % mAdapter.getCount();
                mAdapter.bindView(mCurrentView, mAdapter.get(index));
                mCurrentView.setTag(index);
            }

            mPreIndex = mCurrentIndex;
            mCurrentIndex = mNextIndex;
            mNextIndex++;
            if (mNextIndex > maxItemCount - 1) {
                mNextIndex = 0;
            }
            mCurrentView = getChildAt(mCurrentIndex);
            mPreView = getChildAt(mPreIndex);
            mNextView = getChildAt(mNextIndex);
            moveToNext();
        }
    }

    /**
     * Current event is move to right,if current view move out of screen move
     * the current view to left and reBind the item change index.
     */
    private void moveToPre() {
        if (mCurrentView.getLeft() > getScreenWidth()) {
            mCurrentView.layout(mNextView.getLeft() - getItemMargin(),
                    getItemY(), mNextView.getLeft() - getItemMargin()
                            + getItemWidth(), getItemY() + getItemHeight());

            if (mCurrentView.getTag() != null) {
                int listIndex = (Integer) mCurrentView.getTag();
                int index = (listIndex - maxItemCount + mAdapter.getCount())
                        % mAdapter.getCount();
                mAdapter.bindView(mCurrentView, mAdapter.get(index));
                mCurrentView.setTag(index);
            }

            mNextIndex = mCurrentIndex;
            mCurrentIndex = mPreIndex;
            mPreIndex--;
            if (mPreIndex < 0) {
                mPreIndex = maxItemCount - 1;
            }
            mCurrentView = getChildAt(mCurrentIndex);
            mPreView = getChildAt(mPreIndex);
            mNextView = getChildAt(mNextIndex);
            moveToPre();
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    public int getMaxItemCount() {
        return maxItemCount;
    }

    public void setMaxItemCount(int maxItemCount) {
        this.maxItemCount = maxItemCount;
    }

    public void setReLayout(boolean reLayout) {
        this.reLayout = reLayout;
    }

    public void setCanScroll(boolean canScroll) {
        this.canScroll = canScroll;
    }

    public int getItemX() {
        return itemX;
    }

    public void setItemX(int itemX) {
        this.itemX = itemX;
    }

    public int getItemY() {
        return itemY;
    }

    public void setItemY(int itemY) {
        this.itemY = itemY;
    }

    public int getItemWidth() {
        return itemWidth;
    }

    public void setItemWidth(int itemWidth) {
        this.itemWidth = itemWidth;
    }

    public int getItemHeight() {
        return itemHeight;
    }

    public void setItemHeight(int itemHeight) {
        this.itemHeight = itemHeight;
    }

    public int getItemMargin() {
        return (screenWidth - itemWidth * (maxItemCount - 1) - initItemX * 2) / (maxItemCount - 2) + itemWidth;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public int getInitItemX() {
        return initItemX;
    }

    public void setInitItemX(int initItemX) {
        this.initItemX = initItemX;
    }

    /**
     * The interface for item click callback.
     */
    interface OnItemClickListener {
        public boolean onItemClick(int position);
    }

}