package com.hanter.xpulltorefresh.calculator;

import android.support.v4.widget.NestedScrollView;
import android.view.View;

import com.hanter.xpulltorefresh.PullToRefreshLayout;

/**
 * 类名：ScrollingViewCalculator <br/>
 * 描述：判断 NestedScrollView 的位置计算器 <br/>
 * 创建时间：2017/2/14 14:15
 *
 * @author wangmingshuo
 * @version 1.0
 */

public class NestedScrollViewCalculator extends Calculator<NestedScrollView> {

    public NestedScrollViewCalculator(PullToRefreshLayout refreshLayout, NestedScrollView refreshableView) {
        super(refreshLayout, refreshableView);
    }

    @Override
    public boolean isTargetStart() {
        return mRefreshableView.getScrollY() == 0;
    }

    @Override
    public boolean isTargetEnd() {
        View scrollViewChild = mRefreshableView.getChildAt(0);
        return null != scrollViewChild && mRefreshableView.getScrollY() >= (scrollViewChild.getHeight() - mPullToRefreshLayout.getHeight());
    }
}
