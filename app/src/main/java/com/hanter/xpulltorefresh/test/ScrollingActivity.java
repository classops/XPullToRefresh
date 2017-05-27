package com.hanter.xpulltorefresh.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.hanter.xpulltorefresh.R;
import com.hanter.xpulltorefresh.PullToRefreshLayout;

public class ScrollingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PullToRefreshLayout prlContent = (PullToRefreshLayout) findViewById(R.id.prl_content);
    }
}
