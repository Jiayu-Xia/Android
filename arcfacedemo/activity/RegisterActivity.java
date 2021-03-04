package com.arcsoft.arcfacedemo.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.entity.User;
import com.arcsoft.arcfacedemo.faceserver.FaceServer;
import com.arcsoft.arcfacedemo.retrofit.GetRequest;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.imageutil.ArcSoftImageFormat;
import com.arcsoft.imageutil.ArcSoftImageUtil;
import com.arcsoft.imageutil.ArcSoftImageUtilError;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class RegisterActivity extends AppCompatActivity {
    /**
     * 请求选择本地图片文件的请求码
     */
    private static final int ACTION_CHOOSE_IMAGE = 0x201;
    private Uri uri;
    //注册图所在目录
    private static final String ROOT_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "arcfacedemo";
    private static final String REGISTER_DIR = ROOT_DIR + File.separator + "register";

    //预览选择的图片
    private ImageView ivShow;
    private Bitmap mBitmap = null;

    private String username;
    private String password;
    GetRequest getRequest=new GetRequest();

    private User user=new User();
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        EditText UserName=findViewById(R.id.UserName);
        EditText PassWord=findViewById(R.id.Password);
        ImageButton imageButton=findViewById(R.id.imageButton);

        FaceServer.getInstance().init(this);

        ivShow = findViewById(R.id.register_show);
        ivShow.setImageResource(R.drawable.faces);

        //创建注册图的文件夹
//        File registerFile=new File(REGISTER_DIR);
//        if (!registerFile.exists()){
//            registerFile.mkdirs();
//        }else{
//            Log.i(" ", "文件夹已经存在了，删除文件已经注册的人脸");
//            for (File file:registerFile.listFiles()){
//                file.delete();//删除文件夹中的所有文件
//            }
//        }

        Button registerButton=findViewById(R.id.registerBty);
        //特征提取子线程没完成，button不可点击

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.setUserName(UserName.getText().toString());
                user.setPassword(PassWord.getText().toString());

                if (user.getFaceFeatureData()!=null){
                    System.out.println("发送时有数据");
                }
/*                byte[] faceFeatureData=user.getFaceFeatureData();
                for (byte a:faceFeatureData){
                    System.out.println(String.valueOf(a));
                }*/
                //注册请求
                //getRequest.register(user.getUserName(), user.getPassword(), user.getFaceFeatureData());

                //新接口====================================================
                getRequest.updateOneFile(user.getUserName(), user.getPassword(),file );

            }
        });
    }
    /**
     * 从本地选择文件,从layout文件中设置按钮监听器
     *相应按钮
     * @param view
     */
    public void chooseImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, ACTION_CHOOSE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_CHOOSE_IMAGE) {
            try {
                mBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
//                uri=data.getData();
/*
                int permission= ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if(permission!= PackageManager.PERMISSION_GRANTED){
                // We don't have permission so prompt the user
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
                }
*/

                //file=new File(uri.getPath());

            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            if (mBitmap!=null){
                saveBitmapToFile(REGISTER_DIR,mBitmap,"registerPicture.jpg");

                //Android图片加载框架,注册图预览
                Glide.with(ivShow.getContext())
                        .load(mBitmap)
                        .into(ivShow);
                //耗时的操作放在子线程中操作

/*
                        saveBitmapToFile(REGISTER_DIR,mBitmap,"registerPicture.jpg");

                        //注册操作，注册之前先把注册过的信息删除
                        int deleteCount = FaceServer.getInstance().clearAllFaces(RegisterActivity.this);
                        Log.i("注册之前先删除之前注册的文件 ", String.valueOf(deleteCount));

                        //图片文件文件夹
                        File file=new File(REGISTER_DIR);





                        File jpgFile[]=file.listFiles();
                        mBitmap= ArcSoftImageUtil.getAlignedBitmap(mBitmap, true);
                        if (mBitmap==null){
                            Log.e(" ", "mbitmap为空");
                            return;
                        }
                        //人脸图片注册到特征文件
                        byte[] bgr24=ArcSoftImageUtil.createImageData(mBitmap.getWidth(), mBitmap.getHeight(), ArcSoftImageFormat.BGR24);

                        int transformtCode=ArcSoftImageUtil.bitmapToImageData(mBitmap, bgr24, ArcSoftImageFormat.BGR24);

                        if (transformtCode== ArcSoftImageUtilError.CODE_SUCCESS){

                            boolean success= FaceServer.getInstance().registerBgr24(RegisterActivity.this, bgr24,
                                    mBitmap.getWidth(),mBitmap.getHeight() ,  jpgFile[0].getName().substring(0, jpgFile[0].getName().lastIndexOf(".")));

                            //获取人脸特征字节数组faceFeatureData[]
                              byte[] faceFeatureData=FaceServer.getInstance().registerFaceFeature(RegisterActivity.this, bgr24,
                                    mBitmap.getWidth(),mBitmap.getHeight() ,  jpgFile[0].getName().substring(0, jpgFile[0].getName().lastIndexOf(".")));

                            user.setFaceFeatureData(faceFeatureData);
                        }*/
                    }
        }
    }

    //往注册文件夹中写注册人图片
    public void saveBitmapToFile(String path, Bitmap bm, String picName) {
        file= new File(path, picName);
        FileOutputStream out = null;

        try {
            out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}