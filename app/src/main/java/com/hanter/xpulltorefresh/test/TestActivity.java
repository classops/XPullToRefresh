package com.hanter.xpulltorefresh.test;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.hanter.xpulltorefresh.PullToRefreshLayout;
import com.hanter.xpulltorefresh.R;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CoordinatorLayout cdlContent = (CoordinatorLayout) findViewById(R.id.cdl_content);
        cdlContent.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

            }
        });

        final PullToRefreshLayout prlContent = (PullToRefreshLayout) findViewById(R.id.prl_content);
        prlContent.setPullDownRefreshEnabled(true);
        prlContent.setPullUpRefreshEnabled(false);

        final PullToRefreshLayout prl = (PullToRefreshLayout) findViewById(R.id.prl);
        prl.setEnabled(false);
        prl.setNestedScrollingEnabled(false);
        prl.setPullDownRefreshEnabled(false);
        prl.setPullUpRefreshEnabled(true);

        prl.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onPullDownToRefresh(PullToRefreshLayout refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshLayout refreshView) {

            }
        });


        prl.postDelayed(new Runnable() {
            @Override
            public void run() {
                prl.setNestedScrollingEnabled(true);
                prl.setEnabled(true);
            }
        }, 7000);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}

