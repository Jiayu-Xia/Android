package com.arcsoft.arcfacedemo.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.database.SqliteDaoOrm;
import com.arcsoft.arcfacedemo.entity.NotificationBean;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementActivity extends AppCompatActivity {

    List<NotificationBean> notificationBeans=new ArrayList<>();
    SqliteDaoOrm sqliteDaoOrm=new SqliteDaoOrm(this);
    ListView listView;
    MyAdapter myAdapter = new MyAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);
        notificationBeans=sqliteDaoOrm.selectAllNotification();
        //实例化listView
        listView = findViewById(R.id.listview);
        listView.setAdapter(myAdapter);

    }
    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return notificationBeans.size();
        }
        @Override
        public Object getItem(int i) {
            return null;
        }
        @Override
        public long getItemId(int i) {
            return 0;
        }
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View adapter_layout_notification=View.inflate(AnnouncementActivity.this, R.layout.adapter_layout_notification, null);
            TextView textView=adapter_layout_notification.findViewById(R.id.notificationTextView);
            NotificationBean notificationBean=notificationBeans.get(i);
            String str="<font><big>"+notificationBean.getTitle()+"</big></font>"+"<br>"+ "<br>"+
                    notificationBean.getContent()+"&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;";
            textView.setText(Html.fromHtml(str));
            return adapter_layout_notification;
        }
    }
}