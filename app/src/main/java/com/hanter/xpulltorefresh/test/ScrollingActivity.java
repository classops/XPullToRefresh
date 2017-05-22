package com.hanter.xpulltorefresh.test;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.hanter.xpulltorefresh.R;
import com.hanter.xpulltorefresh.XPullToRefreshLayout;

import static android.view.View.X;

public class ScrollingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        XPullToRefreshLayout prlContent = (XPullToRefreshLayout) findViewById(R.id.prl_content);
    }
}
