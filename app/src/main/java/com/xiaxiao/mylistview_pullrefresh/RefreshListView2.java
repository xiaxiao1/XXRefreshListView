package com.xiaxiao.mylistview_pullrefresh;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * Created by xiaxiao on 2017/8/11.
 */

public class RefreshListView2 extends ListView {

    Context mContext;
    RefreshManager refreshManager;
    IHeaderProxy iHeaderProxy;
    //头部刷新view
    View refreshHeaderView;
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
    public RefreshListView2(Context context) {
        super(context);
        init();
    }

    public RefreshListView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RefreshListView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RefreshListView2(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    private void preInit(Context context) {
        this.mContext = context;
        refreshManager = new RefreshManager();
        iHeaderProxy = new IHeaderProxy(mContext,null);
        init();
    }

    public void init() {

//        initHeaderView();
        refreshManager.initHeaderView();
//        addHeaderView(headerView);
        this.setOnTouchListener(new OnTouchListener() {
            int action;
            int downY=0;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                action = event.getAction();

                if (action==MotionEvent.ACTION_DOWN) {
                    downY = (int) event.getY();
                    currentTouchHeight=refreshHeaderView.getPaddingTop();
                }

                if (action==MotionEvent.ACTION_MOVE) {
                    //滑动到顶部了
                    if (getFirstVisiblePosition()==0&&getChildAt(0).getTop()==0) {
                        XUtil.l("keyike");
                        //是否在下拉
                        if (down) {
                            refreshManager.headerMove((int)event.getY(),downY);
                            if (refreshHeaderView.getTop()<=-headerHeight) {
                                return false;
                            }else {
                                refreshHeaderView.setPadding(0, currentTouchHeight + (int) (event.getY() - downY)
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
                //在UP的时候才算是一次向下拉或者向上拉的session的结束 判断
                if (action==MotionEvent.ACTION_UP) {
//                    headerView.setPadding(0,-headerHeight,0,0);
                    if (down) {
//                        refreshManager.headerBack(refreshHeaderView.getPaddingTop(), -headerHeight);
                        refreshManager.headerBack(refreshHeaderView.getPaddingTop());
//                        iHeaderProxy.backWhileRealse(refreshHeaderView.getPaddingTop(),-headerHeight);
                        down=false;
                    }
                }
                XUtil.l(getChildAt(0).getTop()+"");
                return false;
            }
        });
    }





    public void stopHeaderRefresh() {
        ((Activity)getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshManager.stopHeadRefresh();
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

    public class RefreshManager{
        Animation headerBackAnimation;
        Animation headerRefreshingAnimation;
        Animation headerDismissAnimation;
        ValueAnimator animWatcher;
        public void initHeaderView() {
            if (!iHeaderProxy.isExist()) {
                return;
            }
            refreshHeaderView = iHeaderProxy.getHeaderView();
            iHeaderProxy.init();
            afterInitHeader();
            addHeaderView(refreshHeaderView);
        }



        /**
         * 初始化头部位置
         */
        private void afterInitHeader() {
            ViewTreeObserver observer = refreshHeaderView.getViewTreeObserver();
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onGlobalLayout() {
                    refreshHeaderView.measure(0, 0);
                    headerHeight = refreshHeaderView.getMeasuredHeight();
                    refreshHeaderView.setPadding(0,-headerHeight,0,0);
                    refreshHeaderView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
            headerBackAnimation = iHeaderProxy.getBackAnimation();

        }

        public void headerMove(int from, int to) {
            iHeaderProxy.moveAsFinger(headerHeight,from,to);
        }

        /**
         * 头部返回时动画
         * @param currentY
         */
        public void headerBack(int currentY) {
            iHeaderProxy.setRange(currentY,-headerHeight);
            animWatcher=wrapBackAnimation(iHeaderProxy.getBackAnimation(),currentY,-headerHeight);
            //这一个if加的漂亮，这是在头部正在刷新的时候再进行一些上啦下啦的操作时，就只是在padding上改变一下，不执行其他逻辑了。
            if (oneHeaderSession) {
//                backAnimation(from, 0);
//                iHeaderProxy.backWhileRealse(currentY,-headerHeight);
                runHeaderBack(currentY);
                return;
            }
            //未达到刷新界限
            if (refreshHeaderView.getPaddingTop() < 0) {
//                backAnimation(from, to);
//                iHeaderProxy.backWhileRealse(currentY,-headerHeight);
                runHeaderBack(currentY);
            } else {
                //达到刷新界限，执行刷新
                headerRefreshing = true;
                runHeaderBack(currentY);
//                backAnimation(from, 0);
//                iHeaderProxy.backWhileRealse(currentY,-headerHeight);
            }

        }

        public void runHeaderBack(int currentY) {
            iHeaderProxy.runBackAnim(currentY,-headerHeight);
            animWatcher.start();
        }

        /**
         * 返回时动画
         * @param from
         * @param to
         */
        public ValueAnimator wrapBackAnimation(Animation backAnim ,int from, final int to) {
            ValueAnimator valueAnimator = ValueAnimator.ofInt(from, to);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    refreshHeaderView.setPadding(0,(int)animation.getAnimatedValue(),0,0);
                    if (headerRefreshing &&(int)animation.getAnimatedValue()==to) {
                        headerRefreshing =false;
//                        headerRefreshingAnimation();
                        runHeadRefreshingAnimation();
                        if (refreshListener!=null) {
                            refreshListener.onHeaderRefresh();
                        }
                    }
                }
            });
            valueAnimator.setDuration(backAnim.getDuration());
            return valueAnimator;
        }

        public void runHeadRefreshingAnimation() {
            if (!oneHeaderSession) {
                oneHeaderSession=true;
                iHeaderProxy.runRefreshingAnim();
            }

        }

        public void stopHeadRefresh() {
            oneHeaderSession=false;
            iHeaderProxy.runDismissAnim();
        }
    }


}
