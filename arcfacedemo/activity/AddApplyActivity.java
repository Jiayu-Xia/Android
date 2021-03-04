package com.arcsoft.arcfacedemo.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.entity.Apply;
import com.arcsoft.arcfacedemo.retrofit.GetRequest;
import com.arcsoft.arcfacedemo.session.Session;
import com.arcsoft.arcfacedemo.util.time.GetTime;

public class AddApplyActivity extends AppCompatActivity {
    private static final String[] a={"请假","加班","外出"};
    private static final String[] m={"高军","陈道铮","李万秀","Har"};
    private Spinner spinner;
    private Spinner spinner1;
    private TextView textView;
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private EditText editText1;
    private EditText editText2;
    private EditText editText3;
    private EditText editText4;
    private Button button;

    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> adapter1;
    private Apply apply;
    private GetRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_apply);

        //下拉框
        spinner=findViewById(R.id.ApplySpinner);
        spinner1=findViewById(R.id.ApplySpinner1);

        textView=findViewById(R.id.Spinner01);
        textView1=findViewById(R.id.Spinner1Text);

        editText1=findViewById(R.id.startDate);
        editText2=findViewById(R.id.endDate);
        editText3=findViewById(R.id.leaveHours);
        editText4=findViewById(R.id.leaveReason);
        button=findViewById(R.id.applyButton);

        textView2=findViewById(R.id.applyUser);
        textView3=findViewById(R.id.applyDepart);

        textView2.setText(Session.getInstance().getUserName());
        textView3.setText(Session.getInstance().getDepartment());

        apply=new Apply();
        request=new GetRequest();
        //将可选内容与ArrayAdapter连接起来
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,a);
        adapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,m);

        //设置下拉框风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //给spinner绑定数据源
        spinner.setAdapter(adapter);
        spinner1.setAdapter(adapter1);

        //添加时间Spinner事件监听
        spinner.setOnItemSelectedListener(new SpinnerSelectedListener());
        spinner1.setOnItemSelectedListener(new SpinnerSelectedListener1());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apply.setA_startDate(editText1.getText().toString());
                apply.setA_endDate(editText2.getText().toString());
                //时长
                //apply.setA_startDate(editText3.getText().toString());

                apply.setA_reason(editText4.getText().toString());
                apply.setUser_id(Session.getInstance().getUser_id());
                apply.setA_date(new GetTime().getCurrentDate()+":"+new GetTime().getCurrentTime());
                apply.setA_status(0);
                apply.setA_hours(Integer.valueOf(editText3.getText().toString()));
                apply.setName(Session.getInstance().getUserName());
                request.addApply(apply);
            }
        });

    }
    //使用数组形式操作
    class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
            textView.setText("您的申请是："+a[arg2]);
            apply.setA_type(a[arg2]);
        }

        public void onNothingSelected(AdapterView<?> arg0) {
            Toast.makeText(getApplicationContext(),"请选择申请类型" , Toast.LENGTH_SHORT).show();
        }
    }
    class SpinnerSelectedListener1 implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
            textView1.setText("审批人是："+m[arg2]);
            apply.setApprovedby(m[arg2]);
        }

        public void onNothingSelected(AdapterView<?> arg0) {
            Toast.makeText(getApplicationContext(),"请选择审批人" , Toast.LENGTH_SHORT).show();
        }
    }
}