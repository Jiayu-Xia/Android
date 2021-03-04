package com.arcsoft.arcfacedemo.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.entity.Apply;
import com.arcsoft.arcfacedemo.entity.User;
import com.arcsoft.arcfacedemo.retrofit.GetRequest;
import com.arcsoft.arcfacedemo.session.Session;
import com.arcsoft.arcfacedemo.util.ImageUtil.ImageDispose;

import java.util.ArrayList;

public class MyInfoActivity extends AppCompatActivity {
    //接收retrofit的回调
    public static Handler handler;
    public static Handler handler1;

    private Context context;
    GetRequest request=new GetRequest();

    TextView textName;
    TextView textNumber;
    TextView textDepart;
    TextView textRole;
    TextView textStatus;
    TextView textEmail;
    TextView textPhone;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);
        initView();
        //call用户信息
        request.findInfo(Session.getInstance().getUser_id());
        //call人脸图片
        request.getPhoto(Session.getInstance().getUser_id());

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle=msg.getData();
                User user=new User();
                user= (User) bundle.getSerializable("user");

                textName.setText(user.getName());
                textNumber.setText( String.valueOf(user.getUser_id()));
                textRole.setText(user.getRole());

                textDepart.setText(user.getDepartment_name());

                textPhone.setText(user.getTel());
                textEmail.setText(user.getEmail());


                if(user.getStatus()==0){
                    textStatus.setText("未审批，请等候审批");
                }else if(user.getStatus()==1){
                    textStatus.setText("正常");
                }else{
                    textStatus.setText("异常");
                }
            }
        };

        handler1=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.e("", "头像头像");
                Bundle bundle=msg.getData();
                byte[] imageByte=bundle.getByteArray("photo");
                Bitmap bitmap=ImageDispose.getPicFromBytes(imageByte, new BitmapFactory.Options());
                imageView.setImageBitmap(bitmap);
                Log.i(" ", "显示头像");
            }
        };

    }
    private void initView(){
        textName=findViewById(R.id.textName);
        textNumber=findViewById(R.id.textNumber);
        textDepart=findViewById(R.id.textDepart);
        textRole=findViewById(R.id.textRole);
        textStatus=findViewById(R.id.textStatus);
        textEmail=findViewById(R.id.textEmail);
        textPhone=findViewById(R.id.textPhone);
        imageView=findViewById(R.id.imageView2);
    }
}