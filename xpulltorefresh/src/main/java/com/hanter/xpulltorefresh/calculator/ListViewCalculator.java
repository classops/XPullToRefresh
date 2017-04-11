package com.hanter.xpulltorefresh.calculator;

import android.view.View;
import android.widget.Adapter;
import android.widget.ListView;

import com.hanter.xpulltorefresh.PullToRefreshLayout;

/**
 * 类名：ListViewCalculator <br/>
 * 描述：ListView 的 计算位置
 * 创建时间：2017/02/20 21:54
 *
 * @author hanter
 * @version 1.0
 */
public class ListViewCalculator extends Calculator<ListView> {

    public ListViewCalculator(PullToRefreshLayout refreshLayout, ListView refreshableView) {
        super(refreshLayout, refreshableView);
    }

    @Override
    public boolean isTargetStart() {
        return isFirstItemVisible();
    }

    @Override
    public boolean isTargetEnd() {
        return isLastItemVisible();
    }

    /**
     * 判断第一个child是否完全显示出来
     *
     * @return true完全显示出来，否则false
     */
    private boolean isFirstItemVisible() {

        final Adapter adapter = mRefreshableView.getAdapter();

        if (null == adapter || adapter.isEmpty()) {
            return true;
        } else {

            /*
             * This check should really just be:
             * mRefreshView.getFirstVisiblePosition() == 0, but PtRListView
             * internally use a HeaderView which messes the positions up. For
             * now we'll just add one to account for it and rely on the inner
             * condition which checks getTop().
             */
            if (mRefreshableView.getFirstVisiblePosition() <= 1) {
                final View firstVisibleChild = mRefreshableView.getChildAt(0);
                if (firstVisibleChild != null) {
                    return firstVisibleChild.getTop() >= mRefreshableView.getTop();
                }
            }
        }

        return false;
    }

    /**
     * 判断最后一个child是否完全显示出来
     *
     * @return true完全显示出来，否则false
     */
    private boolean isLastItemVisible() {
        final Adapter adapter = mRefreshableView.getAdapter();

        if (null == adapter || adapter.isEmpty()) {
            return true;
        }

        final int lastItemPosition = adapter.getCount() - 1;
        final int lastVisiblePosition = mRefreshableView.getLastVisiblePosition();

        /*
         * This check should really just be: lastVisiblePosition == lastItemPosition, but ListView
         * internally uses a FooterView which messes the positions up. For me we'll just subtract
         * one to account for it and rely on the inner condition which checks getBottom().
         */
        if (lastVisiblePosition >= lastItemPosition - 1) {
            final int childIndex = lastVisiblePosition - mRefreshableView.getFirstVisiblePosition();
            final int childCount = mRefreshableView.getChildCount();
            final int index = Math.min(childIndex, childCount - 1);
            final View lastVisibleChild = mRefreshableView.getChildAt(index);
            if (lastVisibleChild != null) {
                return lastVisibleChild.getBottom() <= mRefreshableView.getBottom();
            }
        }

        return false;
    }
}
