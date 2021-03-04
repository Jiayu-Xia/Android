package com.arcsoft.arcfacedemo.activity;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.entity.Apply;
import com.arcsoft.arcfacedemo.entity.Attendance;
import com.arcsoft.arcfacedemo.retrofit.GetRequest;
import com.arcsoft.arcfacedemo.session.Session;

import java.util.ArrayList;

public class MyApplyActivity extends AppCompatActivity {
    //接收retrofit的回调
    public static Handler handler;

    private Context context;
    private ArrayList<Apply> applyArrayList;
    GetRequest request=new GetRequest();
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_apply);

        context=this.getApplicationContext();
        ListView listView=findViewById(R.id.MyAppList);

        //发送retrofit请求
        request.findApplyByUser_id(Session.getInstance().getUser_id());
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
        private String s;
        //获取元素个数
        @Override
        public int getCount() {
            return applyArrayList.size();
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
            //这里是怎么做遍历printerList的
            //获取集合中第i个元素，遍历printerList
           Apply apply = applyArrayList.get(i);
           //解析申请表，给申请表各字段赋值
           setValue(apply);

            textView.setText("申请时间: " + apply.getA_date()+"\n"+
                    "申请类型： "+apply.getA_type()+ "        "+"申请时长:"+apply.getA_hours()+"小时"+"\n" +
                    "时间区间： " + apply.getA_startDate()+"--"+apply.getA_endDate()+ "\n" +
                    "审核状态: "+s+ "\n");
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

        private void setValue(Apply apply) {
            if (apply.getA_status()==0){
                 s="未审批";
            }else if (apply.getA_status()==1){
                 s="审批通过";
            }else{
                 s="审批未通过,请联系审核人";
            }
        }
    }
}