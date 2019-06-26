package com.hanter.xpulltorefresh;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * 类名：LoadingLayout <br/>
 * 描述：刷新布局抽象实现方法
 * 创建时间：2017/02/18 20:16
 *
 * @author hanter
 * @version 1.0
 */
public abstract class LoadingLayout {

    /** Header类型布局 */
    public static final int LAYOUT_TYPE_HEADER = 0;

    /** Footer类型布局 */
    public static final int LAYOUT_TYPE_FOOTER = 1;

    protected final View mLoadingView;

    protected int mPreviousState = PullToRefreshState.NONE;
    protected int mCurrentState = PullToRefreshState.NONE;

    public LoadingLayout(PullToRefreshLayout container) {
        mLoadingView = createLoadingView(container.getContext(), container);
    }

    protected abstract View createLoadingView(Context context, ViewGroup container);

    protected View getLoadingView() {
        return mLoadingView;
    }

    protected abstract int getLoadingLayoutType();

    /**
     * 设置当前状态
     * @param newState 设置状态
     */
    public void setState(int newState) {
        if (mCurrentState != newState) {
            mPreviousState = mCurrentState;
            mCurrentState = newState;
            onStateChanged(mPreviousState, newState);
        }
    }

    public int getState() {
        return mCurrentState;
    }

    public void onStateChanged(int oldState, int newState) {

    }


    /**
     * 当状态设置为{@link PullToRefreshState#RESET}时调用
     */
    protected void onReset() {

    }

    /**
     * 当状态设置为{@link PullToRefreshState#PULL_TO_REFRESH}时调用
     */
    protected void onPullToRefresh() {

    }

    /**
     * 当状态设置为{@link PullToRefreshState#RELEASE_TO_REFRESH}时调用
     */
    protected void onReleaseToRefresh() {

    }

    /**
     * 当状态设置为{@link PullToRefreshState#REFRESHING}时调用
     */
    protected void onRefreshing() {

    }

    /**
     * 当状态设置为{@link PullToRefreshState#NO_MORE_DATA}时调用
     */
    protected void onNoMoreData() {

    }

}
