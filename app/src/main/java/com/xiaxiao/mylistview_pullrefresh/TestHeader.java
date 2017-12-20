package com.xiaxiao.mylistview_pullrefresh;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/12/20.
 */

public class TestHeader implements IHeader {
    ImageView img;
    TextView tv;
    View header;
    @Override
    public int getViewId() {
        return R.layout.header;
    }

    @Override
    public void init(View view) {
        header = view;
        img = (ImageView) view.findViewById(R.id.header_img);
        tv = (TextView) view.findViewById(R.id.tv);
    }

    @Override
    public void moveAsFinger(int reference, int from, int to) {
        int x = from - to;
        /*if (x>reference) {
            x = reference;
        } else if (x<0) {
            x=0;
        }*/
        img.setRotation(360*x*0.1f/reference);
        tv.setText("正在下拉。。。from=="+from+"   r0=="+img.getRotation());
    }

    @Override
    public Animation getBackAnimation(int from, int to) {
        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(2000);
        tv.setText("正在返回。。。。");
        tv.setAnimation(animation);
        return animation;
    }

    @Override
    public Animation getRefreshingAnimation() {
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360,img.getMeasuredWidth()/2,img.getMeasuredHeight()/2);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setRepeatMode(Animation.RESTART);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        img.setAnimation(rotateAnimation);
        return rotateAnimation;
    }

    @Override
    public Animation getDismissAnimation(int from, int to) {
        AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
        animation.setDuration(1000);
        header.setAnimation(animation);
        tv.setText("正在消失。。。");
        return animation;
    }

    @Override
    public View getHeaderView() {
        return header;
    }
}
