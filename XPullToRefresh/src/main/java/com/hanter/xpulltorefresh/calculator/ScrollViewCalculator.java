package com.hanter.xpulltorefresh.calculator;

import android.view.View;
import android.widget.ScrollView;

import com.hanter.xpulltorefresh.PullToRefreshLayout;

/**
 * 类名：ScrollViewCalculator <br/>
 * 描述：ScrollView <br/>
 * 创建时间：2017/2/22 13:31
 *
 * @author wangmingshuo
 * @version 1.0
 */

public class ScrollViewCalculator extends Calculator<ScrollView> {

    public ScrollViewCalculator(PullToRefreshLayout refreshLayout, ScrollView refreshableView) {
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
