package com.hanter.xpulltorefresh.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.hanter.xpulltorefresh.DebugLogger;
import com.hanter.xpulltorefresh.XPullToRefreshLayout;
import com.hanter.xpulltorefresh.R;

public class MainActivity extends AppCompatActivity {

    private XPullToRefreshLayout refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        refresh = (XPullToRefreshLayout) findViewById(R.id.refresh);
        refresh.setOverScrollMode(View.OVER_SCROLL_NEVER);
        refresh.setPullDownRefreshEnabled(false);
        refresh.setOnRefreshListener(new XPullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onPullDownToRefresh(XPullToRefreshLayout refreshView) {
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
            public void onPullUpToRefresh(XPullToRefreshLayout refreshView) {
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
    }
}
