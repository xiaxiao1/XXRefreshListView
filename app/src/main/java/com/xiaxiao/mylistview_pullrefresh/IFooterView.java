package com.xiaxiao.mylistview_pullrefresh;

/**
 * Created by Administrator on 2017/12/19.
 */

public interface IFooterView {
    public abstract void onMove(int from ,int to);
    public abstract void onBack(int from ,int to);
    public abstract void onRefresh();
    public abstract void onDismiss();
    public abstract void back();
}
