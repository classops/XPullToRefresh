package com.hanter.xpulltorefresh.calculator;

import com.hanter.xpulltorefresh.XPullToRefreshLayout;

/**
 * 类名：RefreshStateCalculator <br/>
 * 描述：策略模式，来判断不同View是否开始计算 <br/>
 * 创建时间：2017/2/14 14:11
 *
 * @author wangmingshuo
 * @version 1.0
 */

public abstract class Calculator<T> {

    public final static int SCROLL_DIRECTION_DOWN = -1;
    public final static int SCROLL_DIRECTION_UP = 1;

    protected final XPullToRefreshLayout mPullToRefreshLayout;
    protected final T mRefreshableView;

    public Calculator(XPullToRefreshLayout refreshLayout, T refreshableView) {
        mPullToRefreshLayout = refreshLayout;
        mRefreshableView = refreshableView;
    }

    /** 是否在顶部刷新开始位置 */
    public abstract boolean isTargetStart();

    /** 是否在底部刷新开始位置 */
    public abstract boolean isTargetEnd();
}
