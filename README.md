## XPullToRefresh

兼容NestedScroll的下拉刷新控件。可以使用ScrollView,ListView,WebView，RecyclerView，实现NestedScrollChild的View。

## 使用方法

### 添加依赖

1. 添加仓库到根build.gradle文件
``` gradle
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
```
2. 添加库依赖
``` gradle
    dependencies {
        implementation 'com.github.wangmingshuo:XPullToRefresh:1.0.3'
    }
````


- 使用下拉刷新，只需要把要刷新布局嵌套在 PullToRefreshLayout里。

```
<com.hanter.xpulltorefresh.PullToRefreshLayout
    android:id="@+id/refresh"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layout_alignParentLeft="true"
    android:layout_alignParentStart="true"
    android:layout_alignParentTop="true" >

    <ScrollView
        android:id="@+id/slv_content"
        android:overScrollMode="never"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <LinearLayout
            android:orientation="vertical"
            android:layout_width="match_parent"
            android:layout_height="wrap_content">

        </LinearLayout>

    </ScrollView>

</com.hanter.xpulltorefresh.PullToRefreshLayout>
```
- 设置刷新回调接口

```
PullToRefreshLayout refresh = (PullToRefreshLayout) findViewById(R.id.refresh);       
refresh.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
    @Override
    public void onPullDownToRefresh(PullToRefreshLayout refreshView) {
		// 下拉刷新
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshLayout refreshView) {
		// 上拉刷新
    }
});
```
- 上下拉刷新使能方法

```
refresh.setPullDownRefreshEnabled(true);
refresh.setPullUpRefreshEnabled(false);
```

## 支持NestedScrollChild，实现嵌套滚动特性

可以嵌套在CoodinatorLayout及NestedChildParent实现类容器内。

嵌套在CoodinatorLayout协调滑动：

![滑动](/screenshots/screenshots1.gif)



