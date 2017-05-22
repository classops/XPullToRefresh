package com.hanter.xpulltorefresh.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hanter.xpulltorefresh.R;
import com.hanter.xpulltorefresh.XPullToRefreshLayout;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;
import static android.view.View.X;

public class TestXPullNestedScrollingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_xpull_nested_scrolling);


//        XPullToRefreshLayout prlContent = (XPullToRefreshLayout) findViewById(R.id.prl_content);
//        prlContent.setNestedScrollingEnabled(false);
    }
}
