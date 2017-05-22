package com.hanter.xpulltorefresh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goldenhanter.testglide.xpulltorefresh.R;

/**
 * 类名：HeaderLoadingLayout <br/>
 * 描述：下拉刷新所使用布局
 * 创建时间：2017/02/18 20:24
 *
 * @author hanter
 * @version 1.0
 */
public class XHeaderLoadingLayout extends XLoadingLayout {

    /** 旋转动画时间 */
    private static final int ROTATE_ANIM_DURATION = 150;
    /**Header的容器*/
    private RelativeLayout mHeaderContainer;
    /**箭头图片*/
    private ImageView mArrowImageView;
    /**进度条*/
    private ProgressBar mProgressBar;
    /**状态提示TextView*/
    private TextView mHintTextView;
    /**向上的动画*/
    private Animation mRotateUpAnim;
    /**向下的动画*/
    private Animation mRotateDownAnim;

    public XHeaderLoadingLayout(XPullToRefreshLayout container) {
        super(container);

        initViews();
    }

    private void initViews() {
        mHeaderContainer = (RelativeLayout) mLoadingView.findViewById(R.id.pull_to_refresh_header_content);
        mArrowImageView = (ImageView) mLoadingView.findViewById(R.id.pull_to_refresh_header_arrow);
        mHintTextView = (TextView) mLoadingView.findViewById(R.id.pull_to_refresh_header_hint_textview);
        mProgressBar = (ProgressBar) mLoadingView.findViewById(R.id.pull_to_refresh_header_progressbar);

        float pivotValue = 0.5f;    // SUPPRESS CHECKSTYLE
        float toDegree = -180f;     // SUPPRESS CHECKSTYLE
        // 初始化旋转动画
        mRotateUpAnim = new RotateAnimation(0.0f, toDegree, Animation.RELATIVE_TO_SELF, pivotValue,
                Animation.RELATIVE_TO_SELF, pivotValue);
        mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateUpAnim.setFillAfter(true);
        mRotateDownAnim = new RotateAnimation(toDegree, 0.0f, Animation.RELATIVE_TO_SELF, pivotValue,
                Animation.RELATIVE_TO_SELF, pivotValue);
        mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateDownAnim.setFillAfter(true);
    }

    @Override
    protected View createLoadingView(Context context, ViewGroup container) {
        return LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header, container, false);
    }

    @Override
    protected int getLoadingLayoutType() {
        return LoadingLayout.LAYOUT_TYPE_HEADER;
    }

    @Override
    public void onStateChanged(int oldState, int newState) {

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
        mArrowImageView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mArrowImageView.clearAnimation();
        mHintTextView.setText(R.string.pull_to_refresh_header_hint_normal);
    }

    @Override
    protected void onPullToRefresh() {

        DebugLogger.d("HeaderLoadingLayout", "state - " +  mPreviousState);

        if (PullToRefreshState.RELEASE_TO_REFRESH == mPreviousState) {
            mArrowImageView.clearAnimation();
            mArrowImageView.startAnimation(mRotateDownAnim);
        }

        mHintTextView.setText(R.string.pull_to_refresh_header_hint_normal);
    }

    @Override
    protected void onReleaseToRefresh() {
        mArrowImageView.clearAnimation();
        mArrowImageView.startAnimation(mRotateUpAnim);
        mHintTextView.setText(R.string.pull_to_refresh_hint_ready);
    }

    @Override
    protected void onRefreshing() {
        mArrowImageView.clearAnimation();
        mArrowImageView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mHintTextView.setText(R.string.pull_to_refresh_hint_loading);
    }
}
