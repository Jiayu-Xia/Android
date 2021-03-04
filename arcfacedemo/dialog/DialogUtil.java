package com.arcsoft.arcfacedemo.dialog;

import android.content.Context;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.widget.Button;

import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.entity.Apply;
import com.arcsoft.arcfacedemo.retrofit.GetRequest;

public class DialogUtil {
    public static GetRequest request=new GetRequest();

    public static void onItemclick(final View root, Apply apply, Context context){
        Button bt_pass;
        Button bt_un_pass;
        Button bt_cancle;
        BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.popupwindows);
        bottomSheetDialog.show();


        bt_pass= (Button) bottomSheetDialog.findViewById(R.id.pass);
        bt_un_pass= (Button) bottomSheetDialog.findViewById(R.id.un_pass);
        bt_cancle= (Button) bottomSheetDialog.findViewById(R.id.cancel);

        //通过
        bt_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                request.approve(apply.getApp_id(),1);
                bottomSheetDialog.cancel();
            }
        });
        //不通过
        bt_un_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                request.approve(apply.getApp_id(),2);
                bottomSheetDialog.cancel();
            }
        });
        //取消
        bt_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.cancel();
            }
        });

}
}
