package com.arcsoft.arcfacedemo.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.entity.Attendance;
import com.arcsoft.arcfacedemo.retrofit.GetRequest;

import java.util.ArrayList;

public class AttListActivity extends AppCompatActivity {
    static Context context;
    static ArrayList<Attendance> attendancesList;

    GetRequest getRequest = new GetRequest();

    TextView textView;

    //定义全局的handler
    public static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_att_list);

        context=this.getApplicationContext();
        ListView listView=(ListView) findViewById(R.id.AttList);

        getRequest.allAttendance();
        //接收request信息回调
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                MyAdapter myAdapter = new MyAdapter();
                Bundle bundle=msg.getData();
                attendancesList= (ArrayList<Attendance>) bundle.getSerializable("AttList");
                for (Attendance attendance:attendancesList){
                    System.out.println( attendance.toString());

                }
                listView.setAdapter(myAdapter);

            }
        };
    }
    class MyAdapter extends BaseAdapter {
        //获取元素个数
        @Override
        public int getCount() {
            return attendancesList.size();
        }
        @Override
        public Object getItem(int i) {
            return null;
        }
        @Override
        public long getItemId(int i) {
            return 0;
        }
        //由系统调用，返回一个view对象座位listView的条目
        //position：本次getView方法调用所返回的view对象在listView中处于第几条目,i的值就是多少
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View adapter_layout = View.inflate(context, R.layout.adapter_layout_attendance, null);
            textView=adapter_layout.findViewById(R.id.textView2);
            //设置文本大小
            textView.setTextSize(15);
            //获取集合中第i个元素，遍历printerList
            Attendance attendance = attendancesList.get(i);
            String s;
            if (attendance.getStatus()==1){
                 s="正常";
            }else{
                 s="异常";
            }
            textView.setText("时间: " + attendance.getDate()+" "+attendance.getTime()+ "\n" +
                    "地址: " + attendance.getAddress() + "\n" +
                    "审核状态: "+s+ "    "+
                    "审核结果: "+attendance.getDescribed());
            //设置listView的背景色，点击后更改颜色
//            textView.setTextColor(Color.RED);
            //textView.setBackgroundResource(R.drawable.selector_listview);
            //每个条目添加点击事件监听器
/*            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //启动弹窗的dialog，并传入所选对象
                    showBottomSheetDialog(context, deviceDescriptionBean, root);
                }
            });*/
            System.out.println("主线程跑完了");
            return adapter_layout;
        }
    }
}