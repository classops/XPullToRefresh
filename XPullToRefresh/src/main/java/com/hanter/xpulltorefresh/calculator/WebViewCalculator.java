package com.hanter.xpulltorefresh.calculator;


import android.webkit.WebView;
import com.hanter.xpulltorefresh.DebugLogger;
import com.hanter.xpulltorefresh.PullToRefreshLayout;

/**
 * 类名：WebViewCalculator <br/>
 * 描述：WebView位置计算器 <br/>
 * 创建时间：2017/2/22 11:10
 *
 * @author wangmingshuo
 * @version 1.0
 */

public class WebViewCalculator extends Calculator<WebView> {


    public WebViewCalculator(PullToRefreshLayout refreshLayout, WebView refreshableView) {
        super(refreshLayout, refreshableView);
    }

    @Override
    public boolean isTargetStart() {
        return mRefreshableView.getScrollY() == 0;
    }

    @Override
    public boolean isTargetEnd() {

        DebugLogger.d("PullToRefresh", "isTargetEnd");

        @SuppressWarnings("deprecation")
        float exactContentHeight = mRefreshableView.getContentHeight() * mRefreshableView.getScale();
//        float exactContentHeight = (float) Math.floor(mRefreshableView.getContentHeight() * mRefreshableView.getScale());


        float height = mRefreshableView.getMeasuredHeight() + mRefreshableView.getPaddingTop() + mRefreshableView.getPaddingBottom();

        float y = mRefreshableView.getScrollY();


        DebugLogger.d("PullToRefresh", "content height - " + exactContentHeight
                + "\ncontent view height - " + height
                + "\nscrollY - " + y);


        return y >= (exactContentHeight
                - height
                - mRefreshableView.getPaddingTop()
                - mRefreshableView.getPaddingBottom());
    }
}
