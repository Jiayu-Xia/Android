package com.arcsoft.arcfacedemo.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.dialog.DialogUtil;
import com.arcsoft.arcfacedemo.entity.Apply;
import com.arcsoft.arcfacedemo.entity.Attendance;
import com.arcsoft.arcfacedemo.retrofit.GetRequest;
import com.arcsoft.arcfacedemo.session.Session;

import java.util.ArrayList;

public class ApprovalActivity extends AppCompatActivity {

    //接收retrofit的回调
    public static Handler handler;
    public static Handler handler1;

    private Context context;
    private ArrayList<Apply> applyArrayList;
    GetRequest request=new GetRequest();
    TextView textView;
    static View root;


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval);

        context=this.getApplicationContext();
        ListView listView=findViewById(R.id.ApprovalList);

        root =findViewById(R.layout.popupwindows);

        request.findApplyByStatusAndApprovedId(Session.getInstance().getUser_id(), 0);

        //接收retrofit回调
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle=msg.getData();
                applyArrayList= (ArrayList<Apply>) bundle.getSerializable("apply");
                MyAdapter myAdapter=new MyAdapter();
                listView.setAdapter(myAdapter);
            }
        };




    }
    class MyAdapter extends BaseAdapter {

        //获取元素个数
        @Override
        public int getCount() {
            return applyArrayList.size();
        }
        @Override
        public Object getItem(int i) {
            return i;
        }
        @Override
        public long getItemId(int i) {
            return i;
        }
        //由系统调用，返回一个view对象座位listView的条目
        //position：本次getView方法调用所返回的view对象在listView中处于第几条目,i的值就是多少
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View adapter_layout = View.inflate(context, R.layout.adapter_layout_attendance, null);
            textView=adapter_layout.findViewById(R.id.textView2);
            //设置文本大小
            textView.setTextSize(15);
            //这里是怎么做遍历printerList的
            //获取集合中第i个元素，遍历printerList
            Apply apply = applyArrayList.get(i);
            //解析申请表，给申请表各字段赋值
            //setValue(apply);
            //String name;
            // request.findName(apply.getUser_id());
            textView.setText("申请人: "+apply.getName()+ "\n"+
                    "申请时间: " + apply.getA_date()+"\n"+
                    "申请类型： "+apply.getA_type()+ "        "+"申请时长:"+apply.getA_hours()+"小时"+"\n" +
                    "时间区间： " + apply.getA_startDate()+"--"+apply.getA_endDate()+ "\n"
                    );
            //设置listView的背景色，点击后更改颜色
//            textView.setTextColor(Color.RED);
            //textView.setBackgroundResource(R.drawable.selector_listview);
            //每个条目添加点击事件监听器
            System.out.println("主线程跑完了");

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //启动弹窗的dialog，并传入所选对象
                    DialogUtil.onItemclick(root, apply, ApprovalActivity.this);
                }
            });
            return adapter_layout;
        }
    }

}