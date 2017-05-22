package com.hanter.xpulltorefresh.calculator;

import android.support.v4.view.ScrollingView;

import com.hanter.xpulltorefresh.XPullToRefreshLayout;

/**
 * 类名：ScrollingViewCalculator <br/>
 * 描述：ScrollingView接口相关的View
 * 创建时间：2017/02/20 21:46
 *
 * @author hanter
 * @version 1.0
 */
public class ScrollingViewCalculator extends Calculator<ScrollingView> {

    public ScrollingViewCalculator(XPullToRefreshLayout refreshLayout, ScrollingView refreshableView) {
        super(refreshLayout, refreshableView);
    }

    @Override
    public boolean isTargetStart() {
        return !canScrollVertically(mRefreshableView, SCROLL_DIRECTION_DOWN);
    }

    @Override
    public boolean isTargetEnd() {
        return !canScrollVertically(mRefreshableView, SCROLL_DIRECTION_UP);
    }

    /**
     * Check if this view can be scrolled horizontally in a certain direction.
     *
     * @param direction Negative to check scrolling left, positive to check scrolling right.
     * @return true if this view can be scrolled in the specified direction, false otherwise.
     */
    public boolean canScrollHorizontally(ScrollingView recyclerView, int direction) {
        final int offset = recyclerView.computeHorizontalScrollOffset();
        final int range = recyclerView.computeHorizontalScrollRange() - recyclerView.computeHorizontalScrollExtent();
        if (range == 0) return false;
        if (direction < 0) {
            return offset > 0;
        } else {
            return offset < range - 1;
        }
    }

    /**
     * Check if this view can be scrolled vertically in a certain direction.
     *
     * @param direction Negative to check scrolling up, positive to check scrolling down.
     * @return true if this view can be scrolled in the specified direction, false otherwise.
     */
    public boolean canScrollVertically(ScrollingView recyclerView, int direction) {
        final int offset = recyclerView.computeVerticalScrollOffset();
        final int range = recyclerView.computeVerticalScrollRange() - recyclerView.computeVerticalScrollExtent();
        if (range == 0) return false;
        if (direction < 0) {
            return offset > 0;
        } else {
            return offset < range - 1;
        }
    }
}
