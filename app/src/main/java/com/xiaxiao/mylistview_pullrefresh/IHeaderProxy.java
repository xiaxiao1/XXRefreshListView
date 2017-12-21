package com.xiaxiao.mylistview_pullrefresh;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;

/**
 * Created by Administrator on 2017/12/19.
 */

public class IHeaderProxy  {
    IHeader iHeader;
    Animation headerBackAnimation;
    Animation headerRefreshingAnimation;
    Animation headerDismissAnimation;
    Context mContext;
    int referenceHeight;
    int mCurrentY;
    int endY;
    public IHeaderProxy(Context context, IHeader iHeaderView) {
        this.mContext = context;
        this.iHeader = iHeaderView;
        init();

    }

    /*public void setIHeader(IHeader iHeader) {
        this.iHeader=iHeader;
    }*/

    public void setRange(int mCurrentY, int endY) {
        this.mCurrentY = mCurrentY;
        this.endY = endY;
    }

    public boolean isExist() {
        return this.iHeader != null;
    }
    public void moveAsFinger(int reference, int current, int end) {
        this.mCurrentY = current;
        this.endY = end;
        this.iHeader.moveAsFinger(reference, this.mCurrentY,this.endY);
    }

    public void runBackAnim(int current, int end) {
        this.mCurrentY = current;
        this.endY = end;
        this.headerBackAnimation.start();
        this.iHeader.onBackAnimation();

    }
    public void runRefreshingAnim() {
        if (this.headerBackAnimation!=null) {
            this.headerBackAnimation.cancel();
        }
        this.headerRefreshingAnimation.start();
        this.iHeader.onRefreshAnimation();
    }

    public void runDismissAnim() {
        if (this.headerRefreshingAnimation!=null) {
            this.headerRefreshingAnimation.cancel();
        }
        this.headerDismissAnimation.start();
        this.iHeader.onDismissAnimation();
    }

    public Animation getBackAnimation(){
        return this.headerBackAnimation;
    }
    public Animation getRefreshingAnimation(){
        return this.headerRefreshingAnimation;
    }
    public Animation getDismissAnimation(){
        return this.headerDismissAnimation;
    }

    private void init() {
        int id = this.iHeader.getViewId();
        View v = View.inflate(mContext, id, null);
        this.iHeader.init(v);

        headerBackAnimation = this.iHeader.getBackAnimation(this.mCurrentY,this.endY);
        headerRefreshingAnimation = this.iHeader.getRefreshingAnimation();
        headerDismissAnimation = this.iHeader.getDismissAnimation(this.mCurrentY,this.endY);
    }

    public View getHeaderView() {
        return this.iHeader.getHeaderView();
    }
    public void clear() {

    }
}
