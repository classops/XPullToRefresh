package com.hanter.xpulltorefresh;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Px;
import android.support.annotation.RequiresApi;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.goldenhanter.testglide.xpulltorefresh.R;
import com.hanter.xpulltorefresh.calculator.Calculator;

/**
 * 类名：PullToRefreshLayout <br/>
 * 描述：Nested下拉刷新 <br/>
 * 创建时间：2017/1/17 16:36
 *
 * @author wangmingshuo
 * @version 1.0
 */
public class PullToRefreshLayout extends RelativeLayout implements NestedScrollingParent {

    private static final String TAG = "PullToRefreshLayout";

    private static final float DAMPING_FACTOR = 2.2f;

    /** 回滚的时间, 单位MS */
    private static final int SCROLL_DURATION = 200;

    private int mTouchSlop; // 区分触摸滑动和点击的临界距离

    private boolean mNestedScroll = true; // Content视图是否内嵌滚动

    private int mRefreshState = PullToRefreshState.NONE; // 当前刷新状态

    private int mLastTouchY;

    private boolean mIsHandledTouchEvent = false; // 是否消耗

    private boolean mIsBeginPulled = false; // 是否 滑动Footer或者Header

    // 兼容 非 NestedScroll控件
    private final int[] mScrollConsumed = new int[2];

    protected int mScrollDirection; // 滚动方向

    private int mHeaderHeight;
    private int mFooterHeight;

    protected View mHeader;
    protected View mFooter;
    protected View mContent; // 实际内容视图
    protected FrameLayout mContentWrapper;

    protected LoadingLayout mHeaderLayout;
    protected LoadingLayout mFooterLayout;

    private boolean mAdded; // 是否添加了Header和Footer

    private boolean mResetTouch = false;

    private boolean mPullDownRefreshEnabled = true; // 是否能下拉
    private boolean mPullUpRefreshEnabled = true; // 是否能上拉
    private boolean mScrollUpLoadEnabled = false; // TODO 滚动加载

    private NestedScrollingParentHelper mParentHelper;
    private NestedScrollingChildHelper mChildHelper;

    private final int[] mParentOffsetInWindow = new int[2];
    private final int[] mParentScrollConsumed = new int[2];

    private OnRefreshListener mOnRefreshListener;

    private Calculator mRefreshStateCalculator;

    private SmoothScrollRunnable mSmoothScrollRunnable;

    /**
     * 滚动方向
     */
    static class ScrollDirection {
        public static final int SCROLL_NONE = 0;
        public static final int SCROLL_UP = 1;
        public static final int SCROLL_DOWN = 2;
    }

    public interface OnRefreshListener {

        /**
         * 下拉松手后会被调用
         * @param refreshView 刷新的View
         */
        void onPullDownToRefresh(final PullToRefreshLayout refreshView);

        /**
         * 加载更多时会被调用或上拉时调用
         * @param refreshView 刷新的View
         */
        void onPullUpToRefresh(final PullToRefreshLayout refreshView);
    }

    public PullToRefreshLayout(Context context) {
        this(context, null);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    public OnRefreshListener getOnRefreshListener() {
        return mOnRefreshListener;
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.mOnRefreshListener = onRefreshListener;
    }

    private void init(Context context, AttributeSet attrs) {
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        mParentHelper = new NestedScrollingParentHelper(this);
        mChildHelper = new NestedScrollingChildHelper(this);
        mChildHelper.setNestedScrollingEnabled(true);

        setOverScrollMode(OVER_SCROLL_NEVER);

        addRefreshableView(context);

        // 初始化 Wrapper Padding
        setPadding(super.getPaddingLeft(), super.getPaddingTop(), super.getPaddingRight(), super.getPaddingBottom());

        mHeaderLayout = createRefreshHeaderLayout(context);
        mFooterLayout = createRefreshFooterLayout(context);

        mHeader = mHeaderLayout.getLoadingView();
        mFooter = mFooterLayout.getLoadingView();

//        addHeaderAndFooter(context);
    }

    protected void addRefreshableView(Context context) {
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;

        // 创建一个包装容器
        mContentWrapper = new FrameLayout(context);
        mContentWrapper.setId(R.id.xpull_to_refresh_content_wrapper);
        addViewInternal(mContentWrapper, new RelativeLayout.LayoutParams(width, height));
    }

    private void createScrollCalculator() {
        // 设置是否支持内嵌滚动
        mNestedScroll = mContent instanceof NestedScrollingChild;

        Class clazz = CalculatorManager.findCalculator(mContent);

        if (clazz != null ) {
            DebugLogger.d(TAG, "calculaor class name is " + clazz.getName());

            mRefreshStateCalculator = CalculatorManager.createCalculator(clazz, this, mContent);

            if (mRefreshStateCalculator == null) {
                throw new UnsupportedOperationException("create calculator is failed.");
            }

        } else {
            throw new UnsupportedOperationException("don't support the view type.");
        }

        /*
        if (mContent instanceof NestedScrollView) {
            NestedScrollView content = (NestedScrollView) mContent;
            mRefreshStateCalculator = new NestedScrollViewCalculator(this, content);
        } else if (mContent instanceof RecyclerView) {
            RecyclerView content = (RecyclerView) mContent;
            mRefreshStateCalculator = new RecyclerViewCalculator(this, content);
        } else if (mContent instanceof ScrollingView) {
            ScrollingView content = (ScrollingView) mContent;
            mRefreshStateCalculator = new ScrollingViewCalculator(this, content);
        } else if (mContent instanceof ListView) {
            ListView content = (ListView) mContent;

            // 兼容性处理，Android5.0则可以开启内部NestedScrolling特性
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                content.setNestedScrollingEnabled(true);
                mNestedScroll = true;
            }

            mRefreshStateCalculator = new ListViewCalculator(this, content);
        } else if (mContent instanceof WebView) {
            WebView content = (WebView) mContent;

            // 兼容性处理，Android5.0则可以开启内部NestedScrolling特性
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                content.setNestedScrollingEnabled(true);
//                mNestedScroll = true;
//            }

            mRefreshStateCalculator = new WebViewCalculator(this, content);
        } else if (mContent instanceof ScrollView) {
            ScrollView content = (ScrollView) mContent;

            // 兼容性处理，Android5.0则可以开启内部NestedScrolling特性
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                content.setNestedScrollingEnabled(true);
//                mNestedScroll = true;
//            }

            mRefreshStateCalculator = new ScrollViewCalculator(this, content);
        } else {
            throw new UnsupportedOperationException("don't support this class view!");
        }

        */

        // DebugLogger.d("createScrollCalculator", "mNestedScroll:" + mNestedScroll);
    }

    /**
     * 刷新内容布局是否支持 内嵌滚动
     * @return true - 支持内嵌滚动，false - 不支持
     */
    protected boolean isSupportedNestedScroll() {
        return mNestedScroll && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * 创建Header视图
     * @param context Context
     * @return Header
     */
    protected LoadingLayout createRefreshHeaderLayout(Context context) {
        return new HeaderLoadingLayout(this);
    }

    /**
     * 创建Footer视图
     * @param context Context
     * @return Footer
     */
    protected LoadingLayout createRefreshFooterLayout(Context context) {
        return new FooterLoadingLayout(this);
    }

    private void addHeaderAndFooter(Context context) {
//        addViewInternal(mHeader, 0, new LayoutParams(LayoutParams.MATCH_PARENT,
//                LayoutParams.WRAP_CONTENT));

        if (mAdded)
            return;

//        RelativeLayout.LayoutParams headerParams = (LayoutParams) mHeader.getLayoutParams();


//        RelativeLayout.LayoutParams headerParams = new RelativeLayout
//                .LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//        headerParams.addRule(RelativeLayout.ABOVE, R.id.xpull_to_refresh_content_wrapper);
        addViewInternal(mHeader, 0);
        RelativeLayout.LayoutParams headerParams = (LayoutParams) mHeader.getLayoutParams();
//        headerParams.addRule(RelativeLayout.ABOVE, mContentWrapper.getId());
        headerParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
//        mHeader.setLayoutParams(headerParams);


//        RelativeLayout.LayoutParams footerParams = (LayoutParams) mFooter.getLayoutParams();
//        RelativeLayout.LayoutParams footerParams = new RelativeLayout
//                .LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//        headerParams.addRule(RelativeLayout.BELOW, R.id.xpull_to_refresh_content_wrapper);
//        addViewInternal(mFooter, -1, footerParams);

        addViewInternal(mFooter, -1);
        RelativeLayout.LayoutParams footerParams = (LayoutParams) mFooter.getLayoutParams();
//        footerParams.addRule(RelativeLayout.BELOW, mContentWrapper.getId());
        footerParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
//        mFooter.setLayoutParams(footerParams);


        RelativeLayout.LayoutParams contentParams = (LayoutParams) mContentWrapper.getLayoutParams();
        if (isInEditMode()) { // 布局编辑模式，默认只显示待刷新布局
            mFooter.setVisibility(View.INVISIBLE);
            mHeader.setVisibility(View.INVISIBLE);
        } else {
            contentParams.addRule(RelativeLayout.BELOW, mHeader.getId());
            contentParams.addRule(RelativeLayout.ABOVE, mFooter.getId());
        }

        mHeaderHeight = mHeader != null ? mHeader.getHeight() : 0;
        mFooterHeight = mFooter != null ? mFooter.getHeight() : 0;

        int paddingTop = - mHeaderHeight;
        int paddingBottom = - mFooterHeight;
        setPaddingInternal(0, paddingTop, 0, paddingBottom);
    }

    @Override
    public int getPaddingLeft() {
        return mContentWrapper.getPaddingLeft();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public int getPaddingStart() {
        return mContentWrapper.getPaddingStart();
    }

    @Override
    public int getPaddingRight() {
        return mContentWrapper.getPaddingRight();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public int getPaddingEnd() {
        return mContentWrapper.getPaddingEnd();
    }

    @Override
    public int getPaddingTop() {
        return mContentWrapper.getPaddingTop();
    }

    public int getPaddingTopInternal() {
        return super.getPaddingTop();
    }

    @Override
    public int getPaddingBottom() {
        return mContentWrapper.getPaddingBottom();
    }

    public int getPaddingBottomInternal() {
        return super.getPaddingBottom();
    }

    public void setPaddingInternal(@Px int left, @Px int top, @Px int right, @Px int bottom) {
        super.setPadding(left, top, right, bottom);
    }

    @Override
    public void setPadding(@Px int left, @Px int top, @Px int right, @Px int bottom) {
        mContentWrapper.setPadding(left, top, right, bottom);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void setPaddingRelativeInternal(@Px int left, @Px int top, @Px int right, @Px int bottom) {
        super.setPaddingRelative(left, top, right, bottom);
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void setPaddingRelative(@Px int start, @Px int top, @Px int end, @Px int bottom) {
        mContentWrapper.setPaddingRelative(start, top, end, bottom);
    }

    // NestedParent

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        addView(child, -1, params);
//        mContentWrapper.addView(child, params);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
//        super.addView(child, index, params);
        mContentWrapper.addView(child, index, params);

        mContent = child;
        createScrollCalculator();

        addHeaderAndFooter(getContext());
    }

    @Override
    public void addView(View child) {
//        if (mContent != null)
//            ((ViewGroup) mContent).addView(child);
        addView(child, -1);
    }

    @Override
    public void addView(View child, int index) {
        if (child == null) {
            throw new IllegalArgumentException("Cannot add a null child view to a ViewGroup");
        }
        ViewGroup.LayoutParams params = child.getLayoutParams();
        if (params == null) {
            params = generateDefaultLayoutParams();
            if (params == null) {
                throw new IllegalArgumentException("generateDefaultLayoutParams() cannot return null");
            }
        }
        addView(child, index, params);
    }

    @Override
    public void addView(View child, int width, int height) {
        ViewGroup.LayoutParams params = generateDefaultLayoutParams();
        params.width = width;
        params.height = height;
        addView(child, -1, params);
    }

    public void addViewInternal(View child, int index) {
        if (child == null) {
            throw new IllegalArgumentException("Cannot add a null child view to a ViewGroup");
        }
        ViewGroup.LayoutParams params = child.getLayoutParams();
        if (params == null) {
            params = generateDefaultLayoutParams();
            if (params == null) {
                throw new IllegalArgumentException("generateDefaultLayoutParams() cannot return null");
            }
        }
        super.addView(child, index, params);
    }

    public void addViewInternal(View child, ViewGroup.LayoutParams params) {
        super.addView(child, -1, params);
    }

    public void addViewInternal(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mResetTouch) {
            mResetTouch = false;
            super.dispatchTouchEvent(ev);
            return false;
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (mNestedScroll) { // 内嵌滚动，则默认处理
            return super.onInterceptTouchEvent(event);
        } else {
            int action = event.getActionMasked();

            // 一直处理事件，直到手指抬起

            if (action != MotionEvent.ACTION_DOWN && mIsHandledTouchEvent) {
                return true;
            }

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mLastTouchY = (int) (event.getY() + 0.5f);
                    mIsHandledTouchEvent = false;
                    break;

                case MotionEvent.ACTION_MOVE:

                    DebugLogger.d("onInterceptTouchEvent", "ACTION_MOVE, mIsHandledTouchEvent: " + mIsHandledTouchEvent);

                    final int y = (int) (event.getY() + 0.5f);
                    final int dy = mLastTouchY - y;
                    final int absDiff = Math.abs(dy);

                    int direction = parseScrollDirection(dy);

                    // 这里有三个条件：
                    // 1，位移差大于mTouchSlop，这是为了防止快速拖动引发刷新
                    // 2，isPullRefreshing()，如果当前正在下拉刷新的话，是允许向上滑动，并把刷新的HeaderView挤上去
                    // 3，isPullLoading()，理由与第2条相同
                    if (absDiff > mTouchSlop || isPullRefreshing() || isPullLoading()) {

                        DebugLogger.d("onInterceptTouchEvent", "1");

                        // 第一个显示出来，Header已经显示或拉下
                        if (isPullRefreshEnabled() && isTargetStart()) {
                            // 1，Math.abs(getScrollY()) > 0：表示当前滑动的偏移量的绝对值大于0，表示当前HeaderView滑出来了或完全
                            // 不可见，存在这样一种case，当正在刷新时并且RefreshableView已经滑到顶部，向上滑动，那么我们期望的结果是
                            // 依然能向上滑动，直到HeaderView完全不可见
                            // 2，deltaY > 0.5f：表示下拉的值大于0.5f

                            DebugLogger.d("onInterceptTouchEvent", "a");

                            mIsHandledTouchEvent = (Math.abs(getScrollYValue()) > 0 || dy < -0.5f);

                            if (!mIsHandledTouchEvent && isTargetEnd() && direction == ScrollDirection.SCROLL_UP) {
                                mIsHandledTouchEvent = (Math.abs(getScrollYValue()) > 0 || dy > 0.5f);
                            }

                            // 如果截断事件，我们则仍然把这个事件交给刷新View去处理，典型的情况是让ListView/GridView将按下
                            // Child的Selector隐藏

                            /*
                            if (mIsHandledTouchEvent) {
                                mContent.onTouchEvent(event);
                            }
                            */

                        } else if (isPullLoadEnabled() && isTargetEnd()) {

                            DebugLogger.d("onInterceptTouchEvent", "b");

                            mIsHandledTouchEvent = (Math.abs(getScrollYValue()) > 0 || dy > 0.5f);
                        } else if (absDiff > mTouchSlop && isScrollLoadEnabled() && isTargetEnd()) {

                            DebugLogger.d("onInterceptTouchEvent", "c");

                            mIsHandledTouchEvent = (Math.abs(getScrollYValue()) > 0 || dy > 0.5f);
                        }
                    }
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
//                    DebugLogger.d("onInterceptTouchEvent", "ACTION_UP, mIsHandledTouchEvent: " + mIsHandledTouchEvent);
//                    mIsHandledTouchEvent = false;
                    break;

                default:
                    break;
            }

            DebugLogger.d("onInterceptTouchEvent", "handle touch event - " + mIsHandledTouchEvent);

            return mIsHandledTouchEvent;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mNestedScroll) {
            return super.onTouchEvent(event);
        } else {
            boolean handled = false;

            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    mLastTouchY = (int) (event.getY() + 0.5);
                    mIsHandledTouchEvent = false;
                    break;

                case MotionEvent.ACTION_MOVE:

                    final int y = (int) (event.getY() + 0.5f);

                    DebugLogger.d("onTouchEvent", "ACTION_MOVE lastY:" + mLastTouchY + ", y:" + y);

                    int dx = 0;
                    int dy = mLastTouchY - y;
                    mLastTouchY = y;

                    DebugLogger.d("onTouchEvent", "ACTION_MOVE dy : " + dy);

                    if (Math.abs(dy) <= 0.5f) { // TODO 这里测试更新

                        DebugLogger.d("onTouchEvent", "测试完成移动 scrollYValue - " + getScrollYValue());

                        mIsBeginPulled = false;
                        mIsHandledTouchEvent = true;

//                        mContentWrapper.onTouchEvent(event);

                        mResetTouch = true;

                        handled = false;
                    } else {
                        DebugLogger.d("onTouchEvent", "测试移动 scrollYValue - " + getScrollYValue());

                        mScrollConsumed[0] = 0;
                        mScrollConsumed[1] = 0;

                        nestedPreScroll(mContent, dx, dy, mScrollConsumed);

                        // 消耗了事件，则不进行处理
                        if (mScrollConsumed[1] != 0 && mIsBeginPulled) {

                            if (getScrollYValue() == 0) {
                                mIsHandledTouchEvent = false;
                                handled = false;

                                mResetTouch = true;

//                                mContentWrapper.onInterceptTouchEvent(event);

//                                mContentWrapper.onTouchEvent(event);

                            } else {
                                handled = true;
                            }

                            DebugLogger.d("onTouchEvent", "ACTION_MOVE 1 mIsHandledTouchEvent:"
                                    + true + ", dy:" + dy);

                            // FIXME RelativeLayout+Wrapper，快速切换有抖动现象
                            // FIXME 更新？？？,处理未消耗掉部分，因为无法传递，暂时不做……
                        } else {
                            mIsHandledTouchEvent = false;

                            handled = false;

                            mResetTouch = true;

//                            mContentWrapper.requestDisallowInterceptTouchEvent(true);
                            mContentWrapper.onInterceptTouchEvent(event);

                            DebugLogger.d("onTouchEvent", "ACTION_MOVE 2 mIsHandledTouchEvent:"
                                    + false + ", dy:" + dy);
                        }
                    }
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:

                    DebugLogger.d("onTouchEvent", "ACTION_UP, mIsHandledTouchEvent: " + mIsHandledTouchEvent);

                    if (mIsHandledTouchEvent && mIsBeginPulled) {
                        mIsHandledTouchEvent = false;
                        mIsBeginPulled = false;
                        // 当第一个显示出来时

                        switch (mHeaderLayout.getState()) {
                            case PullToRefreshState.RELEASE_TO_REFRESH:
                                mHeaderLayout.setState(PullToRefreshState.REFRESHING);

                                post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mOnRefreshListener != null)
                                            mOnRefreshListener.onPullDownToRefresh(PullToRefreshLayout.this);
                                    }
                                });

                                handled = true;
                                break;

                            case PullToRefreshState.PULL_TO_REFRESH:
                                mHeaderLayout.setState(PullToRefreshState.RESET);
                                break;
                        }

                        switch (mFooterLayout.getState()) {
                            case PullToRefreshState.RELEASE_TO_REFRESH:

                                mFooterLayout.setState(PullToRefreshState.REFRESHING);

                                post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mOnRefreshListener != null)
                                            mOnRefreshListener.onPullUpToRefresh(PullToRefreshLayout.this);
                                    }
                                });

                                handled = true;
                                break;

                            case PullToRefreshState.PULL_TO_REFRESH:
                                mFooterLayout.setState(PullToRefreshState.RESET);
                                break;
                        }

                        resetHeaderAndFooterLayout();
                    }
                    break;
            }

            DebugLogger.d("onTouchEvent",  "handled " + handled);

            return handled;
        }
    }

    // nested parent

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        DebugLogger.d(TAG, "onStartNestedScroll" + " child scrollY " + target.getScrollY());
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        DebugLogger.d(TAG, "onNestedScrollAccepted");

        if (ViewCompat.SCROLL_AXIS_VERTICAL == nestedScrollAxes)
            mParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);

        startNestedScroll(nestedScrollAxes);
    }

    @Override
    public void onStopNestedScroll(View target) {
//        stopNestedScroll();
        DebugLogger.d(TAG, "onStopNestedScroll");

        mParentHelper.onStopNestedScroll(target);

        if (mIsBeginPulled) {
            mIsBeginPulled = false;

            switch (mHeaderLayout.getState()) {
                case PullToRefreshState.RELEASE_TO_REFRESH:
                    mHeaderLayout.setState(PullToRefreshState.REFRESHING);

                    post(new Runnable() {
                        @Override
                        public void run() {
                            if (mOnRefreshListener != null)
                                mOnRefreshListener.onPullDownToRefresh(PullToRefreshLayout.this);
                        }
                    });
                    break;

                case PullToRefreshState.PULL_TO_REFRESH:
                    mHeaderLayout.setState(PullToRefreshState.RESET);
                    break;
            }

            switch (mFooterLayout.getState()) {
                case PullToRefreshState.RELEASE_TO_REFRESH:

                    mFooterLayout.setState(PullToRefreshState.REFRESHING);

                    post(new Runnable() {
                        @Override
                        public void run() {
                            if (mOnRefreshListener != null)
                                mOnRefreshListener.onPullUpToRefresh(PullToRefreshLayout.this);
                        }
                    });
                    break;

                case PullToRefreshState.PULL_TO_REFRESH:
                    mFooterLayout.setState(PullToRefreshState.RESET);
                    break;
            }

            // TODO Pull to 处理状态改变？？？
            resetHeaderAndFooterLayout();
        }

        // Dispatch up our nested parent
        stopNestedScroll();
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        DebugLogger.d(TAG, "onNestedScroll" + " dyConsumed:" + dyConsumed + " dyUnconsumed:" + dyUnconsumed);

        if (mIsBeginPulled) { // 不处理 未消耗的部分
            DebugLogger.d(TAG, "onNestedScroll disable unconsumed portion.");
        } else {

        }

        // TODO 移动Header和Footer
        // Dispatch up to the nested parent first

        // TODO 分发 onNestedScroll 事件，分发给Parent，如果实现ChildHelper则调用
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                mParentOffsetInWindow);

        final int dy = dyUnconsumed + mParentOffsetInWindow[1];
        if (dy < 0) {
            // TODO 移动窗口
        }

        DebugLogger.d("TAG", "mParentOffsetInWindow - " + mParentOffsetInWindow[1]);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        nestedPreScroll(target, dx, dy, consumed);

        // TODO 分发 onNestedPreScroll 事件 , ??? 传入 null
        // Now let our nested parent consume the leftovers
        final int[] parentConsumed = mParentScrollConsumed;
        if (dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], parentConsumed, null)) {
            consumed[0] += parentConsumed[0];
            consumed[1] += parentConsumed[1];

            DebugLogger.e(TAG, "dispatchNestedPreScroll");
        }

        DebugLogger.d(TAG, "onNestedPreScroll:"
                + ", dy = " + dy
                + ", consumed[1] = " + consumed[1]);
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        DebugLogger.d(TAG, "onNestedFling");
        // 分发 nestedFling
//        return dispatchNestedFling(velocityX, velocityY, consumed);

        return false;
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        DebugLogger.d(TAG, "onNestedPreFling");
        // 分发 nestedPreFling
//        return dispatchNestedPreFling(velocityX, velocityY);

        return false;
    }

    @Override
    public int getNestedScrollAxes() {
        DebugLogger.d(TAG, "getNestedScrollAxes");
        return mParentHelper.getNestedScrollAxes();
    }

    // nestedChild

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        DebugLogger.d(TAG, "setNestedScrollingEnabled - " + enabled);

        mChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        DebugLogger.d(TAG, "isNestedScrollingEnabled - " + mChildHelper.isNestedScrollingEnabled());

        return mChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
//        return super.startNestedScroll(axes);

        boolean scroll = (axes == ViewCompat.SCROLL_AXIS_VERTICAL)
                && mChildHelper.startNestedScroll(axes);

        Log.d("TAG", "startNestedScroll - " + scroll);

        // TODO 判断是否
        return scroll;
    }

    @Override
    public void stopNestedScroll() {
        mChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
//        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
        return false;
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
//        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY);
        return false;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mHeaderHeight = mHeader != null ? mHeader.getMeasuredHeight() : 0;
        mFooterHeight = mFooter != null ? mFooter.getMeasuredHeight() : 0;

        DebugLogger.d(TAG, "header height : " + mHeaderHeight);
        DebugLogger.d(TAG, "footer height : " + mFooterHeight);

//        setPadding(0, -mHeaderHeight, 0, -mFooterHeight);

        setPaddingInternal(0, -mHeaderHeight, 0, -mFooterHeight);


//        int paddingLeft = getPaddingLeft();
//        int paddingRight = getPaddingRight();
//        int paddingTop = getPaddingTop() - mHeaderHeight;
//        int paddingBottom = getPaddingBottom() - mFooterHeight;
//        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

        if (mContent != null) {
            /*
            LayoutParams params = (LayoutParams) mContent.getLayoutParams();
            params.height = h;
            mContent.setLayoutParams(params);
            mContent.requestLayout();
            */

            LayoutParams params = (LayoutParams) mContentWrapper.getLayoutParams();
            params.height = h;
            mContentWrapper.setLayoutParams(params);
            mContentWrapper.requestLayout();

            FrameLayout.LayoutParams contentParams = (FrameLayout.LayoutParams) mContent.getLayoutParams();
            contentParams.height = h;
            mContent.setLayoutParams(contentParams);
            mContent.requestLayout();
        }

        post(new Runnable() {
            @Override
            public void run() {
                requestLayout();
            }
        });
    }

    /**
     * 设置当前滑动状态
     */
    private void setPullToRefreshState() {

        int scrollY = getScrollY();

        // 正在刷新时，不进行任何处理

        int headerState = mHeaderLayout.getState();

        if (headerState != PullToRefreshState.REFRESHING) {
            if (scrollY < 0) {

                if (Math.abs(scrollY) >= mHeaderHeight) {
                    mHeaderLayout.setState(PullToRefreshState.RELEASE_TO_REFRESH);
                } else {
                    mHeaderLayout.setState(PullToRefreshState.PULL_TO_REFRESH);
                }

            } else {
                mHeaderLayout.setState(PullToRefreshState.RESET);
            }
        }

        int footerState = mFooterLayout.getState();

        if (footerState != PullToRefreshState.REFRESHING) {
           if (scrollY > 0) {

                if (scrollY >= mFooterHeight) { // RELEASE_UP_TO_REFRESH
                    mFooterLayout.setState(PullToRefreshState.RELEASE_TO_REFRESH);
                } else { // RESET
                    mFooterLayout.setState(PullToRefreshState.PULL_TO_REFRESH);
                }

            } else {
               mFooterLayout.setState(PullToRefreshState.RESET);
            }
        }


    }

    /**
     * 是否在开始位置
     * @return 开始位置
     */
    public boolean isTargetStart() {
        return mRefreshStateCalculator.isTargetStart();
    }

    /**
     * 是否在开始位置
     * @return 开始位置
     */
    public boolean isTargetEnd() {
        return mRefreshStateCalculator.isTargetEnd();
    }

    public boolean isPullRefreshing() {
        return mHeaderLayout.getState() == PullToRefreshState.REFRESHING;
    }

    public boolean isPullLoading() {
        return mFooterLayout.getState() == PullToRefreshState.REFRESHING;
    }

    public void setPullDownRefreshEnabled(boolean pullDownRefreshEnabled) {
        this.mPullDownRefreshEnabled = pullDownRefreshEnabled;
    }

    public void setPullUpRefreshEnabled(boolean pullUpRefreshEnabled) {
        this.mPullUpRefreshEnabled = pullUpRefreshEnabled;
    }

    public void setScrollUpLoadEnabled(boolean scrollUpLoadEnabled) {
        this.mScrollUpLoadEnabled = scrollUpLoadEnabled;
    }

    boolean isPullRefreshEnabled() {
        return mPullDownRefreshEnabled && mHeader != null;
    }

    boolean isPullLoadEnabled() {
        return mPullUpRefreshEnabled && mFooter != null;
    }

    boolean isScrollLoadEnabled() {
        return mScrollUpLoadEnabled && mFooter != null;
    }

    int getScrollYValue() {
        return getScrollY();
    }

    protected void nestedPreScroll(View target, int dx, int dy, int[] consumed) {

        /*
        int touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

        if (Math.abs(dy) < ViewConfiguration.get(getContext()).getScaledTouchSlop()) {

            DebugLogger.d(TAG, "onNestedPreScroll:" + " touchSlop = " + touchSlop);

            consumed[1] = dy;

            return;
        }
        */

        // consumed 代表当前消耗的值， 剩下会分配给 child
        scrollDirection(dy);

        if (isTargetStart()) {

            DebugLogger.d(TAG, "case 1");

            if (mScrollDirection == ScrollDirection.SCROLL_DOWN) { // 向下滑动
                consumeHeaderScroll(dy, consumed, true);
            } else { // 向上滑动

                if (isTargetEnd()) {

                    if (mScrollDirection == ScrollDirection.SCROLL_UP) { // 向下滑动
                        consumeFooterScroll(dy, consumed, true);
                    }

                } else {

                    int scrollY = getScrollY();

                    if (scrollY < 0) {
                        int offset = scrollY + dy;
                        if (offset > 0) {
                            consumeHeaderScroll(Math.abs(scrollY), consumed, false);
                        } else {
                            consumeHeaderScroll(dy, consumed, false);
                        }
                    } else {
                        consumed[1] = 0;
                        mIsBeginPulled = false;
                    }

                }
            }

        } else if (isTargetEnd()) {

            DebugLogger.d(TAG, "case 2");

            if (mScrollDirection == ScrollDirection.SCROLL_UP) { // 向上滑动
                consumeFooterScroll(dy, consumed, true);
            } else { // 向下滑动，并判断，是否Footer显示

                int scrollY = getScrollY();

                if (scrollY > 0) {
                    int offset = scrollY + dy;
                    if (offset < 0) {
                        consumeFooterScroll(scrollY, consumed, false);
                    } else {
                        consumeFooterScroll(dy, consumed, false);
                    }
                } else {
                    if (isTargetStart()) {
                        consumeHeaderScroll(dy, consumed, true);
                    } else {
                        consumed[1] = 0;
                        mIsBeginPulled = false;
                    }
                }
            }

        } else { // 处理中间位置时，仅仅内部滚动
            DebugLogger.d(TAG, "case 3");
            consumed[1] = 0;
            mIsBeginPulled = false;
        }

        setPullToRefreshState();
    }

    void consumeHeaderScroll(int dy, int[] consumed, boolean damped) {

        if (isPullRefreshEnabled()) {
            int moveDistance;
            if (damped) {
                moveDistance = (int) (dy / DAMPING_FACTOR);
            } else {
                moveDistance = dy;
            }
            if (moveDistance != 0)
                moveHeader(moveDistance);
            consumed[1] = moveDistance;

            mIsBeginPulled = true;
        } else {
            consumed[1] = 0;

            mIsBeginPulled = false;
        }
    }

    void consumeFooterScroll(int dy, int[] consumed, boolean damped) {
        if (isPullLoadEnabled()) {
            int moveDistance;
            if (damped) {
                moveDistance = (int) (dy / DAMPING_FACTOR);
            } else {
                moveDistance = dy;
            }
            if (moveDistance != 0)
                moveFooter(moveDistance);
            consumed[1] = moveDistance;
            mIsBeginPulled = true;
        } else {
            consumed[1] = 0;
            mIsBeginPulled = false;
        }
    }

    private int parseScrollDirection(int dy) {
        if (dy > 0) {
            return ScrollDirection.SCROLL_UP;
        } else if (dy < 0) {
            return ScrollDirection.SCROLL_DOWN;
        } else {
            return ScrollDirection.SCROLL_NONE;
        }
    }

    private void scrollDirection(int dy) {
        mScrollDirection = parseScrollDirection(dy);
    }

    /**
     * 移动Header
     * @param deltaY 距离
     */
    private void moveHeader(int deltaY) {
        scrollBy(0, deltaY);
    }

    protected void moveFooter(int deltaY) {
        scrollBy(0, deltaY);
    }

    void smoothScrollBy(int deltaY) {
        int newScrollValue = getScrollY() + deltaY;
        smoothScrollTo(newScrollValue);
    }

    void smoothScrollBy(int deltaY, long duration, long delayMillis) {
        int newScrollValue = getScrollY() + deltaY;
        smoothScrollTo(newScrollValue, duration, delayMillis);
    }

    void smoothScrollTo(int newScrollValue) {
        smoothScrollTo(newScrollValue, getSmoothScrollDuration(newScrollValue), 0);
    }

    void smoothScrollTo(int newScrollValue, long duration,
                        long delayMillis) {
        if (null != mSmoothScrollRunnable) {
            mSmoothScrollRunnable.stop();
        }

        int oldScrollValue = getScrollY();
        boolean post = (oldScrollValue != newScrollValue);
        if (post) {
            mSmoothScrollRunnable = new SmoothScrollRunnable(oldScrollValue,
                    newScrollValue, duration);
        }

        if (post) {
            if (delayMillis > 0) {
                postDelayed(mSmoothScrollRunnable, delayMillis);
            } else {
                post(mSmoothScrollRunnable);
            }
        }
    }

    int getSmoothScrollDuration(int newScrollValue) {
        return SCROLL_DURATION;
    }

    /**
     * 下拉刷新头部 重置到位置
     */
    protected void resetHeaderAndFooterLayout() {

        // FIXME 当刷新中时，滑动导致问题

        DebugLogger.d("PullToRefreshLayout", "resetHeaderAndFooterLayout");

        int scrollY = getScrollY();

        int headerState = mHeaderLayout.getState();
        int footerState = mFooterLayout.getState();

        if (headerState == PullToRefreshState.REFRESHING && scrollY < 0 && Math.abs(scrollY) > mHeaderHeight) {
            smoothScrollTo(-mHeaderHeight);
        } else if (footerState == PullToRefreshState.REFRESHING && scrollY > 0 && scrollY > mFooterHeight) {
            smoothScrollTo(mFooterHeight);
        } else {
            if (scrollY != 0)
                smoothScrollTo(0);
        }

        /*
        if (refreshing && scrollY <= mHeaderHeight) {
            smoothScrollTo(0);
            return;
        }
        */

        /*
        if (refreshing) {
            smoothScrollTo(-mHeaderHeight);
        } else {
            smoothScrollTo(0);
        }
        */
    }

    protected void onStateChanged(int oldState, int newState) {
        DebugLogger.d(TAG, "onStateChanged:" + " oldState - " + oldState + " --> " + "newState - " + newState);
    }

    public void completePullDownRefresh() {
        if (mHeaderLayout.getState() == PullToRefreshState.REFRESHING) {
            mHeaderLayout.setState(PullToRefreshState.RESET);
        }
        resetHeaderAndFooterLayout();
    }

    public void completePullUpRefresh() {
        if (mFooterLayout.getState() == PullToRefreshState.REFRESHING) {
            mFooterLayout.setState(PullToRefreshState.RESET);
        }
        resetHeaderAndFooterLayout();
    }

    public void completeRefresh() {
        if (mHeaderLayout.getState() == PullToRefreshState.REFRESHING) {
            mHeaderLayout.setState(PullToRefreshState.RESET);
        }

        if (mFooterLayout.getState() == PullToRefreshState.REFRESHING) {
            mFooterLayout.setState(PullToRefreshState.RESET);
        }

        resetHeaderAndFooterLayout();
    }

    /**
     * 实现了平滑滚动的Runnable
     *
     * @author Li Hong
     * @since 2013-8-22
     */
    final class SmoothScrollRunnable implements Runnable {
        /** 动画效果 */
        private final Interpolator mInterpolator;
        /** 结束Y */
        private final int mScrollToY;
        /** 开始Y */
        private final int mScrollFromY;
        /** 滑动时间 */
        private final long mDuration;
        /** 是否继续运行 */
        private boolean mContinueRunning = true;
        /** 开始时刻 */
        private long mStartTime = -1;
        /** 当前Y */
        private int mCurrentY = -1;

        /**
         * 构造方法
         *
         * @param fromY
         *            开始Y
         * @param toY
         *            结束Y
         * @param duration
         *            动画时间
         */
        SmoothScrollRunnable(int fromY, int toY, long duration) {
            mScrollFromY = fromY;
            mScrollToY = toY;
            mDuration = duration;
            mInterpolator = new DecelerateInterpolator();
        }

        @Override
        public void run() {
            /**
             * If the duration is 0, we scroll the view to target y directly.
             */
            if (mDuration <= 0) {
                scrollTo(0, mScrollToY);
                return;
            }

            /**
             * Only set mStartTime if this is the first time we're starting,
             * else actually calculate the Y delta
             */
            if (mStartTime == -1) {
                mStartTime = System.currentTimeMillis();
            } else {

                /**
                 * We do do all calculations in long to reduce software float
                 * calculations. We use 1000 as it gives us good accuracy and
                 * small rounding errors
                 */
                final long oneSecond = 1000; // SUPPRESS CHECKSTYLE
                long normalizedTime = (oneSecond * (System.currentTimeMillis() - mStartTime))
                        / mDuration;
                normalizedTime = Math.max(Math.min(normalizedTime, oneSecond),
                        0);

                final int deltaY = Math.round((mScrollFromY - mScrollToY)
                        * mInterpolator.getInterpolation(normalizedTime
                        / (float) oneSecond));
                mCurrentY = mScrollFromY - deltaY;

                scrollTo(0, mCurrentY);
            }

            // If we're not at the target Y, keep going...
            if (mContinueRunning && mScrollToY != mCurrentY) {
                postDelayed(this, 16);// SUPPRESS
                // CHECKSTYLE
            }
        }

        /**
         * 停止滑动
         */
        void stop() {
            mContinueRunning = false;
            removeCallbacks(this);
        }
    }
}
