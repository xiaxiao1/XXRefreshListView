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
     void moveAsFinger(int reference,int current ,int end);
     Animation getBackAnimation(int current ,int end);
     Animation getRefreshingAnimation();
     Animation getDismissAnimation(int current ,int end);

     View getHeaderView();

    void onMoveAsFinger();
    void onBackAnimation();
    void onRefreshAnimation();
    void onDismissAnimation();
}
