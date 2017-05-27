package com.hanter.xpulltorefresh.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hanter.xpulltorefresh.DebugLogger;
import com.hanter.xpulltorefresh.PullToRefreshLayout;
import com.hanter.xpulltorefresh.R;

public class WebViewRefreshActivity extends AppCompatActivity {

    private PullToRefreshLayout refresh;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_refresh);

        refresh = (PullToRefreshLayout) findViewById(R.id.refresh);
        refresh.setOverScrollMode(View.OVER_SCROLL_NEVER);
        refresh.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onPullDownToRefresh(PullToRefreshLayout refreshView) {
//                Toast.makeText(MainActivity.this, "下拉刷新", Toast.LENGTH_SHORT).show();

                DebugLogger.d("onPullDownToRefresh", "下拉刷新");

                refreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refresh.completePullDownRefresh();
                    }
                }, 10000);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshLayout refreshView) {
//                Toast.makeText(MainActivity.this, "上拉刷新", Toast.LENGTH_SHORT).show();

                DebugLogger.d("onPullUpToRefresh", "上拉刷新");

                refreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refresh.completePullUpRefresh();
                    }
                }, 10000);
            }
        });

        mWebView = (WebView) findViewById(R.id.wv_content);
        mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            public void onPageFinished(WebView view, String url) {
                refresh.completePullDownRefresh();
            }
        });

        loadUrl();
    }

    int mIndex = 0;

    private void loadUrl() {

        int length = sUrls.length;
        mIndex = mIndex % length;
        String url = sUrls[mIndex];
        mIndex++;
        mWebView.loadUrl(url);
    }

    private static final String[] sUrls = {
//        "file:///android_asset/html/test.html",
            "http://m.baidu.com",
            "http://www.163.com",
//        "http://www.sina.com.cn",
//        "http://www.sohu.com"
    };

    static class JSInterface {
        public void getClass2() {

        }
    }
}
