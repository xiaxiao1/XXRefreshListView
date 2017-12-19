package com.xiaxiao.mylistview_pullrefresh;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * Created by xiaxiao on 2017/8/11.
 */

public class RefreshListView extends ListView {
    //头部刷新view
    View headerView;
    //头部高度
    int headerHeight;
    boolean down=false;
    //头部正在刷新中
    boolean headerRefreshing =false;
    ImageView headerImg;
    //是否是一次滑动会话
    boolean oneHeaderSession=false;
    //刷新回调方法
    RefreshListener refreshListener;
    //头部刷新时的动画
    Animation onHeaderRefreshAnimation;
    int currentTouchHeight;
    public RefreshListView(Context context) {
        super(context);
        init();
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }



    public void init() {
        initHeaderView();
        addHeaderView(headerView);
        this.setOnTouchListener(new View.OnTouchListener() {
            int action;
            int downY=0;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                action = event.getAction();

                if (action==MotionEvent.ACTION_DOWN) {
                    downY = (int) event.getY();
                    currentTouchHeight=headerView.getPaddingTop();
                }

                if (action==MotionEvent.ACTION_MOVE) {
                    //滑动到顶部了
                    if (getFirstVisiblePosition()==0&&getChildAt(0).getTop()==0) {
                        XUtil.l("keyike");
                        if (down) {
                            if (headerView.getTop()<=-headerHeight) {
                                return false;
                            }else {
                                headerView.setPadding(0, currentTouchHeight + (int) (event.getY() - downY)

                                        / 4, 0, 0);
                                return true;
                            }
                        } else {
                            if (event.getY() > downY) {
                                down = true;
                            } else {
                                return false;
                            }
                        }

                    }
                }
                if (action==MotionEvent.ACTION_UP) {
//                    headerView.setPadding(0,-headerHeight,0,0);
                    if (down) {
                        headerBack(headerView.getPaddingTop(), -headerHeight);
                        down=false;
                    }
                }

                XUtil.l(getChildAt(0).getTop()+"");
                return false;
            }
        });
    }

    /**
     * 初始化头部
     */
    public void initHeaderView() {
        headerView = View.inflate(getContext(), R.layout.header, null);
        headerView.measure(0, 0);
        headerHeight = headerView.getMeasuredHeight();
        headerView.setPadding(0,-headerHeight,0,0);
        headerImg = (ImageView) headerView.findViewById(R.id.header_img);
    }

    /**
     * 头部返回时动画
     * @param from
     * @param to
     */
    public void headerBack(int from, int to) {
        //这一个if加的漂亮，这是在头部正在刷新的时候再进行一些上啦下啦的操作时，就只是在padding上改变一下，不执行其他逻辑了。
        if (oneHeaderSession) {
            backAnimation(from, 0);
            return;
        }
        //未达到刷新界限
        if (headerView.getPaddingTop() < 0) {
            backAnimation(from, to);
        } else {
            //达到刷新界限，执行刷新
            headerRefreshing = true;
            backAnimation(from, 0);
        }

    }

    /**
     * 返回时动画
     * @param from
     * @param to
     */
    public void backAnimation(int from, final int to) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(from, to);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                headerView.setPadding(0,(int)animation.getAnimatedValue(),0,0);
                if (headerRefreshing &&(int)animation.getAnimatedValue()==to) {
                    headerRefreshing =false;
                    headerRefreshingAnimation();
                    if (refreshListener!=null) {
                        refreshListener.onHeaderRefresh();
                    }
                }
            }
        });
        valueAnimator.setDuration(400);
        valueAnimator.start();
    }

    public void headerRefreshingAnimation() {
        if (!oneHeaderSession) {
            oneHeaderSession=true;
            onHeaderRefreshAnimation= AnimationUtils.loadAnimation(getContext(), R.anim.round);
            headerImg.setAnimation(onHeaderRefreshAnimation);
            onHeaderRefreshAnimation.start();
        }

    }

    public void stopHeaderRefresh() {
        ((Activity)getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onHeaderRefreshAnimation.cancel();

                headerImg.clearAnimation();
                onHeaderRefreshAnimation=null;
                oneHeaderSession=false;
                backAnimation(0,-headerHeight);
            }
        });

    }


    public void setRefreshListener(RefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }

    public interface  RefreshListener{
        public void onHeaderRefresh();
        public void onFooterRefresh();
    }
}
