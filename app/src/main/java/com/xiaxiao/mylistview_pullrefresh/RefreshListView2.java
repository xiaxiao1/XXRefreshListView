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
    Animation referenceAnim;
    int currentTouchHeight;
    public RefreshListView2(Context context) {
        super(context);
        preInit(context);
    }

    public RefreshListView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        preInit(context);
    }

    public RefreshListView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        preInit(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RefreshListView2(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        preInit(context);
    }


    private void preInit(Context context) {
        this.mContext = context;
        refreshManager = new RefreshManager(context);
        init();
    }

    public void init() {
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
        IHeaderProxy iHeaderProxy;
        ValueAnimator backWithNothingAnim;
        ValueAnimator backForRefreshAnim;
        Animation headerBackAnimation;
        Animation headerRefreshingAnimation;
        Animation headerDismissAnimation;
        ValueAnimator animWatcher;
        int mCurrentY;
        int mEndY;
        int mCurrentY4refresh;
//        int mEndY;

        public RefreshManager(Context context) {
            iHeaderProxy = new IHeaderProxy(context,new TestHeader(context));
            initAnims();
            initHeaderView();
        }
        public void initHeaderView() {
            if (!iHeaderProxy.isExist()) {
                return;
            }
            refreshHeaderView = iHeaderProxy.getHeaderView();
//            iHeaderProxy.init();
            headerBackAnimation = iHeaderProxy.getBackAnimation();
            headerRefreshingAnimation = iHeaderProxy.getRefreshingAnimation();
            headerDismissAnimation = iHeaderProxy.getDismissAnimation();
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

        }

        public void headerMove(int current, int end) {
            iHeaderProxy.moveAsFinger(headerHeight,current,end);
        }

        /**
         * 头部返回时动画
         * @param currentY
         */
        public void headerBack(int currentY) {
            iHeaderProxy.setRange(currentY,-headerHeight);
            if (refreshHeaderView.getPaddingTop()==-headerHeight) {
                return;
            }
            //这一个if加的漂亮，这是在头部正在刷新的时候再进行一些上啦下啦的操作时，就只是在padding上改变一下，不执行其他逻辑了。
            if (oneHeaderSession) {
                runHeaderBackWithNothing(true,iHeaderProxy.getBackAnimation(),currentY,0);
                return;
            }
            //未达到刷新界限
            if (refreshHeaderView.getPaddingTop() < 0) {
                runHeaderBackWithNothing(false,iHeaderProxy.getDismissAnimation(),currentY,-headerHeight);
            } else {
                //达到刷新界限，执行刷新
                headerRefreshing = true;
                runHeaderBackForRefresh(iHeaderProxy.getBackAnimation(),currentY);
            }

        }

        public void runHeaderBackForRefresh(Animation referenceAnim,int currentY) {
            if (animWatcher!=null&&animWatcher.isRunning()) {
                animWatcher.cancel();
            }
            this.mCurrentY4refresh = currentY;
            iHeaderProxy.runBackAnim(currentY,0);
            backForRefreshAnim.setDuration(referenceAnim.getDuration());
            backForRefreshAnim.start();
            animWatcher = backForRefreshAnim;
        }
        public void runHeaderBackWithNothing(boolean isBack,Animation referenceAnim,int currentY,int end) {
            if (animWatcher!=null&&animWatcher.isRunning()) {
                animWatcher.cancel();
            }
            this.mCurrentY = currentY;
            this.mEndY = end;
            if (isBack) {
                iHeaderProxy.runBackAnim(currentY, end);
            } else {
                iHeaderProxy.runDismissAnim();
            }
            backWithNothingAnim.setDuration(referenceAnim.getDuration());
            backWithNothingAnim.start();
            animWatcher = backWithNothingAnim;
        }

        /**
         * 返回时动画
         */
        public ValueAnimator wrapBackAnimation(Animation backAnim ) {
            if (animWatcher!=null) {
                animWatcher.cancel();
            }
            if (animWatcher==null) {
                animWatcher = ValueAnimator.ofInt(mCurrentY,mEndY);
                animWatcher.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        refreshHeaderView.setPadding(0,(int)animation.getAnimatedValue(),0,0);
                        if (headerRefreshing &&(int)animation.getAnimatedValue()==mEndY) {
                            headerRefreshing =false;
//                        headerRefreshingAnimation();
                            runHeadRefreshingAnimation();
                            if (refreshListener!=null) {
                                refreshListener.onHeaderRefresh();
                            }
                        }
                    }
                });
            }

            animWatcher.setDuration(backAnim.getDuration());
            return animWatcher;
        }


        public void clearAnim() {
            iHeaderProxy.clear();
            if (animWatcher != null) {
                animWatcher.cancel();
                animWatcher = null;
            }
        }

        public void runHeadRefreshingAnimation() {
            if (!oneHeaderSession) {
                oneHeaderSession=true;
                iHeaderProxy.runRefreshingAnim();
            }

        }

        public void stopHeadRefresh() {
            oneHeaderSession=false;
            runHeaderBackWithNothing(false,iHeaderProxy.getDismissAnimation(),0,-headerHeight);
        }
        public void initAnims() {
            backWithNothingAnim = ValueAnimator.ofInt(mCurrentY,mEndY);
            backWithNothingAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    refreshHeaderView.setPadding(0,(int)animation.getAnimatedValue(),0,0);
                }
            });

            backForRefreshAnim = ValueAnimator.ofInt(mCurrentY4refresh,0);
            backForRefreshAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    refreshHeaderView.setPadding(0,(int)animation.getAnimatedValue(),0,0);
                    if (headerRefreshing &&(int)animation.getAnimatedValue()==0) {
                        headerRefreshing =false;
//                        headerRefreshingAnimation();
                        runHeadRefreshingAnimation();
                        if (refreshListener!=null) {
                            refreshListener.onHeaderRefresh();
                        }
                    }
                }
            });
        }

    }




}
