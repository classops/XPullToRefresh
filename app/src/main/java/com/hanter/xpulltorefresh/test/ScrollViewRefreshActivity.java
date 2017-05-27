package com.hanter.xpulltorefresh.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;

import com.hanter.xpulltorefresh.DebugLogger;
import com.hanter.xpulltorefresh.PullToRefreshLayout;
import com.hanter.xpulltorefresh.R;

public class ScrollViewRefreshActivity extends AppCompatActivity {

    PullToRefreshLayout refresh;
    ScrollView slvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_view_refresh);

        slvContent = (ScrollView) findViewById(R.id.slv_content);

        refresh = (PullToRefreshLayout) findViewById(R.id.refresh);
        refresh.setOverScrollMode(View.OVER_SCROLL_NEVER);
        refresh.setPullDownRefreshEnabled(true);
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
                }, 4000);
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
                }, 4000);
            }
        });
    }
}
