package com.hanter.xpulltorefresh.test;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.hanter.xpulltorefresh.DebugLogger;
import com.hanter.xpulltorefresh.XPullToRefreshLayout;
import com.hanter.xpulltorefresh.R;

public class ListViewRefreshActivity extends AppCompatActivity {

    XPullToRefreshLayout refresh;
    ListView lvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_refresh);

        lvContent = (ListView) findViewById(R.id.rlv_content);
        lvContent.setAdapter(new Adapter(this));

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
                }, 4000);
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
                }, 4000);
            }
        });

        refresh.doPullRefreshing(true, 1000);
    }


    static class Adapter extends BaseAdapter {

        private Context mContext;

        public Adapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return 50;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_view, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            return convertView;
        }


        static class ViewHolder {
            ViewHolder(View itemView) {

            }

        }
    }


}
