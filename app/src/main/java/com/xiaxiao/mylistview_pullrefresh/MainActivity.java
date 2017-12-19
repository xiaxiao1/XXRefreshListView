package com.xiaxiao.mylistview_pullrefresh;

import android.animation.ValueAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RefreshListView listView;
    MyAdapter myAdapter;
    List<String> datas = new ArrayList<>();
    RefreshListView.RefreshListener refreshListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (RefreshListView) findViewById(R.id.listview);
        for (int i=0;i<40;i++) {
            datas.add(i + " hahahaha");
        }
        myAdapter = new MyAdapter(this, datas);
        listView.setAdapter(myAdapter);

        XUtil.l("totalcount:"+listView.getCount()+"  childcount: "+listView.getChildCount());

        refreshListener=new RefreshListView.RefreshListener() {
            @Override
            public void onHeaderRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        datas.add(0,"我是新来的，" + System.currentTimeMillis());
                        datas.add(0,"我是新来的，" + System.currentTimeMillis());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                myAdapter.notifyDataSetChanged();
                            }
                        });

                        listView.stopHeaderRefresh();
                    }
                }).start();

            }

            @Override
            public void onFooterRefresh() {

            }
        };
        listView.setRefreshListener(refreshListener);

    }




}
