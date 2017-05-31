package com.hanter.xpulltorefresh.test;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hanter.xpulltorefresh.DebugLogger;
import com.hanter.xpulltorefresh.PullToRefreshLayout;
import com.hanter.xpulltorefresh.R;

public class TestRecyclerViewRefreshActivity extends AppCompatActivity {

    PullToRefreshLayout refresh;
    RecyclerView rlvContent;

    Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_recycler_view_refresh);

        rlvContent = (RecyclerView) findViewById(R.id.rlv_content);
        rlvContent.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new Adapter(this);
        rlvContent.setAdapter(mAdapter);

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
//                Toast.makeText(TestRecyclerViewRefreshActivity.this, "上拉刷新", Toast.LENGTH_SHORT).show();

                mAdapter.mPullLoad = true;
                mAdapter.notifyDataSetChanged();

                DebugLogger.d("onPullUpToRefresh", "上拉刷新");

                refreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refresh.completePullUpRefresh();

                        mAdapter.mPullLoad = false;
                        mAdapter.add();
                        mAdapter.notifyDataSetChanged();

                        DebugLogger.d("onPullUpToRefresh", "上拉刷新完成");
                    }
                }, 4000);
            }
        });

//        refresh.doPullRefreshing(true, 1000);
    }

    public static class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public Adapter(Context context) {
            this.mContext = context;
        }

        private Context mContext;
        private boolean mPullLoad = false;

        private int mCount = 10;

        @Override
        public int getItemViewType(int position) {
            if (mPullLoad && position == mCount) {
                return 1;
            } else {
                return 0;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if (viewType == 0) {
                return new ItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_recycler_view, parent, false));
            } else {
                return new MoreDataViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_recycler_more, parent, false));
            }

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            if (mPullLoad) {
                return mCount + 1;
            } else {
                return mCount;
            }
        }

        public void add() {
            mCount += 5;
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {

        public ItemViewHolder(View itemView) {
            super(itemView);
        }
    }

    static class MoreDataViewHolder extends RecyclerView.ViewHolder {

        public MoreDataViewHolder(View itemView) {
            super(itemView);
        }
    }

}
