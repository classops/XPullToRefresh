package com.hanter.xpulltorefresh.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hanter.xpulltorefresh.R;

public class TestXPullNestedScrollingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_xpull_nested_scrolling);


//        PullToRefreshLayout prlContent = (PullToRefreshLayout) findViewById(R.id.prl_content);
//        prlContent.setNestedScrollingEnabled(false);
    }
}
