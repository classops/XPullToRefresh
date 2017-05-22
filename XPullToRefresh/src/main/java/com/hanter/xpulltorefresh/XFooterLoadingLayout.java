package com.hanter.xpulltorefresh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.goldenhanter.testglide.xpulltorefresh.R;

/**
 * 类名：FooterLoadingLayout <br/>
 * 描述：用于上拉刷新的头部
 * 创建时间：2017/02/19 11:13
 *
 * @author hanter
 * @version 1.0
 */
public class XFooterLoadingLayout extends XLoadingLayout {

    /**进度条*/
    private ProgressBar mProgressBar;
    /** 显示的文本 */
    private TextView mHintView;

    public XFooterLoadingLayout(XPullToRefreshLayout container) {
        super(container);
        initViews();
    }

    private void initViews() {
        mProgressBar = (ProgressBar) mLoadingView.findViewById(R.id.pull_to_load_footer_progressbar);
        mHintView = (TextView) mLoadingView.findViewById(R.id.pull_to_load_footer_hint_textview);
    }

    @Override
    protected View createLoadingView(Context context, ViewGroup container) {
        return LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_footer, container, false);
    }

    @Override
    protected int getLoadingLayoutType() {
        return LoadingLayout.LAYOUT_TYPE_FOOTER;
    }

    @Override
    public void onStateChanged(int oldState, int newState) {
        mProgressBar.setVisibility(View.GONE);
        mHintView.setVisibility(View.INVISIBLE);

        switch (newState) {
            case PullToRefreshState.NONE:
            case PullToRefreshState.RESET:
                onReset();
                break;

            case PullToRefreshState.PULL_TO_REFRESH:
                onPullToRefresh();
                break;

            case PullToRefreshState.RELEASE_TO_REFRESH:
                onReleaseToRefresh();
                break;

            case PullToRefreshState.REFRESHING:
                onRefreshing();
                break;
        }
    }

    @Override
    protected void onReset() {
        mHintView.setText(R.string.pull_to_refresh_hint_loading);
    }

    @Override
    protected void onPullToRefresh() {
        mHintView.setVisibility(View.VISIBLE);
        mHintView.setText(R.string.pull_to_refresh_footer_hint_normal);
    }

    @Override
    protected void onReleaseToRefresh() {
        mHintView.setVisibility(View.VISIBLE);
        mHintView.setText(R.string.pull_to_refresh_hint_ready);
    }

    @Override
    protected void onRefreshing() {
        mProgressBar.setVisibility(View.VISIBLE);
        mHintView.setVisibility(View.VISIBLE);
        mHintView.setText(R.string.pull_to_refresh_hint_loading);
    }

    @Override
    protected void onNoMoreData() {
        mHintView.setVisibility(View.VISIBLE);
        mHintView.setText(R.string.pull_to_refresh_no_more_data);
    }
}
