package com.arcsoft.arcfacedemo.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.entity.User;
import com.arcsoft.arcfacedemo.retrofit.GetRequest;
import com.arcsoft.arcfacedemo.session.Session;

public class AddInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_info);

        GetRequest request=new GetRequest();

        EditText name=findViewById(R.id.editName);
        EditText phone=findViewById(R.id.editPhone);
        EditText department=findViewById(R.id.editDepartment);
        EditText role=findViewById(R.id.editRole);
        EditText email=findViewById(R.id.editEmail);
        Button button=findViewById(R.id.infoButton);
        User user=new User();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LocationActivity locationActivity=new LocationActivity();

                user.setUser_id(Session.getInstance().getUser_id());
                user.setName(name.getText().toString());
                user.setTel(phone.getText().toString());
                user.setDepartment_name(department.getText().toString());
                user.setRole(role.getText().toString());
                String emailString=email.getText().toString();
                user.setEmail(emailString);

                String[] timeList=new String[2];
                timeList=locationActivity.getTime().split(" ");

                user.setCreate_time(timeList[0]);

                Log.e(" ", user.toString());
                request.updateUserInfo(user);
//                Intent intent=new Intent(AddInfoActivity.this,ChooseFunctionActivity.class);
//                startActivity(intent);
            }
        });


    }
}