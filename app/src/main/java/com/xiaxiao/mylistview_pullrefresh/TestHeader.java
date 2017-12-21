package com.xiaxiao.mylistview_pullrefresh;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/12/20.
 */

public class TestHeader implements IHeader {
    ImageView img;
    ImageView img2;
    TextView tv;
    View header;
    Context mContext;
    /*Animation backanimation;
    Animation refreshanimation;
    Animation dismissanimation;*/
    public TestHeader(Context context) {
        this.mContext = context;
    }
    @Override
    public int getViewId() {
        return R.layout.header;
    }

    @Override
    public void init(View view) {
        header = view;
        img = (ImageView) view.findViewById(R.id.header_img);
        img2 = (ImageView) view.findViewById(R.id.img2);
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
        tv.setText("正在随手指移动。。。from=="+from+"   r0=="+img.getRotation());
    }

    @Override
    public Animation getBackAnimation(int current ,int end) {
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.you_yi);
        animation.setDuration(2000);
        tv.setText("正在返回。。。。");
       /* tv.setAnimation(animation);*/
        return animation;
    }

    @Override
    public Animation getRefreshingAnimation() {
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360,img.getMeasuredWidth()/2,img.getMeasuredHeight()/2);
        rotateAnimation.setDuration(1000);
        /*rotateAnimation.setRepeatMode(Animation.RESTART);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        tv.setText("正在刷新。。。。");
        tv.setAnimation(rotateAnimation);*/
        return rotateAnimation;
    }

    @Override
    public Animation getDismissAnimation(int current ,int end) {
        AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
        animation.setDuration(2000);
        /*animation.setRepeatCount(Animation.RESTART);
        animation.setRepeatCount(3);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        img2.setAnimation(animation);*/
        tv.setText("正在消失。。。");
        return animation;
    }

    @Override
    public View getHeaderView() {
        return header;
    }

    @Override
    public void onMoveAsFinger() {
        XUtil.l("onMoveAsFinger");
    }

    @Override
    public void onBackAnimation() {
        XUtil.l("onBackAnimation");
    }

    @Override
    public void onRefreshAnimation() {
        XUtil.l("onRefreshAnimation");
    }

    @Override
    public void onDismissAnimation() {
        XUtil.l("onDismissAnimation");
    }
}
