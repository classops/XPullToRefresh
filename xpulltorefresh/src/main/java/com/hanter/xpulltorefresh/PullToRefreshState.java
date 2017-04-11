package com.hanter.xpulltorefresh;

/**
 * 类名：PullToRefreshState <br/>
 * 描述：刷新状态 <br/>
 * 创建时间：2017/1/18 10:54
 *
 * @author wangmingshuo
 * @version 1.0
 */

public class PullToRefreshState {

    /** 初始状态 */
    public final static int NONE = 0;

    /** 重置状态 */
    public final static int RESET = 1;

    /** 拉动刷新 */
    public final static int PULL_TO_REFRESH = 2;

    /** 释放刷新 */
    public final static int RELEASE_TO_REFRESH = 3;

    /** 正在刷新 */
    public final static int REFRESHING = 4;

    /** 没有更多数据，禁止刷新 */
    public final static int NO_MORE_DATA = 5;
}
