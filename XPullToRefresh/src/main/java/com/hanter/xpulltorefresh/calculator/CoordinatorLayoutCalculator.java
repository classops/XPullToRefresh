package com.hanter.xpulltorefresh.calculator;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.view.View;

import com.hanter.xpulltorefresh.XPullToRefreshLayout;

public class CoordinatorLayoutCalculator extends Calculator<CoordinatorLayout> {

    private AppBarStateChangeListener.State mAppBarState = AppBarStateChangeListener.State.IDLE;

    public CoordinatorLayoutCalculator(XPullToRefreshLayout refreshLayout, CoordinatorLayout refreshableView) {
        super(refreshLayout, refreshableView);

        AppBarLayout appBarLayout = null;

        for (int i = 0; i < mRefreshableView.getChildCount(); i++) {
            View view = mRefreshableView.getChildAt(i);
            if (view instanceof AppBarLayout) {
                appBarLayout = (AppBarLayout) view;
            }
        }

        if (appBarLayout != null) {

            Log.e("test", "appBarLayout is detected");

            appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {

                @Override
                void onStateChanged(AppBarLayout appBarLayout, State state) {
                    mAppBarState = state;
                }
            });
        }
    }

    @Override
    public boolean isTargetStart() {
        return (mRefreshableView.getScrollY() == 0) && (mAppBarState == AppBarStateChangeListener.State.EXPANDED);
    }

    @Override
    public boolean isTargetEnd() {
        return mRefreshableView.getScrollY() > mRefreshableView.getMeasuredHeight();
    }

    private static abstract class AppBarStateChangeListener implements AppBarLayout.OnOffsetChangedListener {

        enum State {
            EXPANDED,
            COLLAPSED,
            IDLE
        }

        private State mCurrentState = State.IDLE;

        @Override
        public final void onOffsetChanged(AppBarLayout appBarLayout, int i) {
            if (i == 0) {
                if (mCurrentState != State.EXPANDED) {
                    onStateChanged(appBarLayout, State.EXPANDED);
                }
                mCurrentState = State.EXPANDED;
            } else if (Math.abs(i) >= appBarLayout.getTotalScrollRange()) {
                if (mCurrentState != State.COLLAPSED) {
                    onStateChanged(appBarLayout, State.COLLAPSED);
                }
                mCurrentState = State.COLLAPSED;
            } else {
                if (mCurrentState != State.IDLE) {
                    onStateChanged(appBarLayout, State.IDLE);
                }
                mCurrentState = State.IDLE;
            }
        }

        abstract void onStateChanged(AppBarLayout appBarLayout, State state);
    }
}
