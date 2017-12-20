package com.xiaxiao.mylistview_pullrefresh;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RefreshListView2 listView;
    MyAdapter myAdapter;
    List<String> datas = new ArrayList<>();
    RefreshListView2.RefreshListener refreshListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (RefreshListView2) findViewById(R.id.listview);
        for (int i=0;i<40;i++) {
            datas.add(i + " hahahaha");
        }
        myAdapter = new MyAdapter(this, datas);
        listView.setAdapter(myAdapter);

        XUtil.l("totalcount:"+listView.getCount()+"  childcount: "+listView.getChildCount());

        refreshListener=new RefreshListView2.RefreshListener() {
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
