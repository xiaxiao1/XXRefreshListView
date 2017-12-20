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

        headerBackAnimation = this.iHeader.getBackAnimation(mCurrentY, endY);
        headerRefreshingAnimation = this.iHeader.getRefreshingAnimation();
        headerDismissAnimation = this.iHeader.getDismissAnimation(mCurrentY, endY);
    }

    public boolean isExist() {
        return this.iHeader != null;
    }
    public void moveAsFinger(int reference, int from, int to) {
        this.iHeader.moveAsFinger(reference, from, to);
    }

    public void runBackAnim(int from, int to) {
        this.iHeader.getBackAnimation(from, to).start();
    }
    public void runRefreshingAnim() {
        this.iHeader.getRefreshingAnimation().start();
    }

    public void runDismissAnim() {
        if (this.iHeader.getRefreshingAnimation()!=null) {
            this.iHeader.getRefreshingAnimation().cancel();
        }
        this.iHeader.getDismissAnimation(mCurrentY,0).start();
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
    }

    public View getHeaderView() {
        return this.iHeader.getHeaderView();
    }
}
