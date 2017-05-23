package com.hanter.xpulltorefresh.test;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hanter.xpulltorefresh.DebugLogger;
import com.hanter.xpulltorefresh.XPullToRefreshLayout;
import com.hanter.xpulltorefresh.R;

public class TestRecyclerViewRefreshActivity extends AppCompatActivity {

    XPullToRefreshLayout refresh;
    RecyclerView rlvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_recycler_view_refresh);

        rlvContent = (RecyclerView) findViewById(R.id.rlv_content);
        rlvContent.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rlvContent.setAdapter(new Adapter(this));

        refresh = (XPullToRefreshLayout) findViewById(R.id.refresh);
        refresh.setOverScrollMode(View.OVER_SCROLL_NEVER);
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

        refresh.doPullRefreshing(true, 1000);
    }

    public static class Adapter extends RecyclerView.Adapter<ViewHolder> {

        public Adapter(Context context) {
            this.mContext = context;
        }

        private Context mContext;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_recycler_view, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 10;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

}
