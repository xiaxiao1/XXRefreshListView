package com.xiaxiao.mylistview_pullrefresh;

import android.view.View;
import android.view.animation.Animation;

/**
 * Created by Administrator on 2017/12/19.
 */

public interface IHeader {
     int getViewId();
     void init(View view);

    //随手指在上下滑动中
     void moveAsFinger(int reference,int from ,int to);
     Animation getBackAnimation(int from, int to);
     Animation getRefreshingAnimation();
     Animation getDismissAnimation(int from, int to);

     View getHeaderView();
}
