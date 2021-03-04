package com.arcsoft.arcfacedemo.retrofit;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.arcsoft.arcfacedemo.activity.AddressBookActivity;
import com.arcsoft.arcfacedemo.activity.ApprovalActivity;
import com.arcsoft.arcfacedemo.activity.AttListActivity;
import com.arcsoft.arcfacedemo.activity.LocationActivity;
import com.arcsoft.arcfacedemo.activity.LoginActivity;
import com.arcsoft.arcfacedemo.activity.MyApplyActivity;
import com.arcsoft.arcfacedemo.activity.MyInfoActivity;
import com.arcsoft.arcfacedemo.activity.RegisterAndRecognizeActivity;
import com.arcsoft.arcfacedemo.entity.Apply;
import com.arcsoft.arcfacedemo.entity.Attendance;
import com.arcsoft.arcfacedemo.entity.User;
import com.arcsoft.arcfacedemo.session.Session;
import com.arcsoft.arcfacedemo.util.ImageUtil.ToByteConvertFactory;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GetRequest {
    User user=new User();

    //log
    public static final String TAG="Retrofit_request";
    //服务器地址
    public static final String url="http://10.64.44.213:8085/";

    //登录======================================================
    public void login(String userName,String password){
        //服务器地址
//        String url="http://192.168.101.14:8085/";
        //创建retrofit对象
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //创建网络请求实例
        GetRequest_Interface request=retrofit.create(GetRequest_Interface.class);
        //对请求进行封装
        Call<User> call=request.getLogin(userName,password);

        //异步发送网路请求，重写请求成功和请求失败的接口
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                //保存用户id；
                user=response.body();
                Session session=Session.getSession();
                session.setUser_id(user.getUser_id());

                session.setUserName(user.getName());
                session.setDepartment(user.getDepartment_name());
                System.out.println("用户id："+session.getUser_id());

                System.out.println("登录请求成功，登录的信息是"+response.toString());
                String userName;
                String password;
                userName=response.body().getUserName();
                password=response.body().getPassword();

                Bundle bundle=new Bundle();
                bundle.putString("userName", userName);
                bundle.putString("password", password);
                Message message=new Message();
                message.setData(bundle);
                //回调到主线程
                LoginActivity.handler.sendMessage(message);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                System.out.println("登录失败，API请求失败");
                Log.e(TAG, t.getMessage());
                Bundle bundle=new Bundle();
                bundle.putString("userName", null);
                bundle.putString("password", null);
                Message message=new Message();
                message.setData(bundle);
                LoginActivity.handler.sendMessage(message);
            }
        });
    }

    //人脸特征变量来注册
    public void register(String userName,String password,byte[] faceFeature) throws IOException {
       /* //byte[]与inputStream转换
        ByteArrayInputStream inputStream=new ByteArrayInputStream(faceFeature);

        //inputStream转为byte[]
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100]; //buff用于存放循环读取的临时数据
        int rc = 0;
        while ((rc = inputStream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in_b = swapStream.toByteArray(); //in_b为转换之后的结果*/

//        String url="http://192.168.101.14:8085/";
        //创建retrofit对象
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //创建网络请求实例
        GetRequest_Interface request=retrofit.create(GetRequest_Interface.class);
        //对请求进行封装
        Call<User> call=request.getRegister(userName,password,faceFeature);

        for (byte a:faceFeature){
            System.out.println(String.valueOf(a));
        }

        //异步发送网路请求，重写请求成功和请求失败的接口
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                System.out.println("注册请求成功，用户名是："+ response.isSuccessful());
                user=response.body();
                Session session=Session.getSession();
                session.setUser_id(user.getUser_id());
                System.out.println("用户id："+session.getUser_id());
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                System.out.println("注册失败，API请求失败");

            }
        });

    }

    //用人脸图来注册
    public void updateOneFile(String username,String password, File file){
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GetRequest_Interface request=retrofit.create(GetRequest_Interface.class);

        RequestBody fileRQ = RequestBody.create(MediaType.parse("image/*"), file);

        MultipartBody.Part part = MultipartBody.Part.createFormData("picture", file.getName(), fileRQ);



        Call<ResponseBody> call=request.uploadFile(part,username,password);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(TAG, "成功"+response.message());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG, "失败"+t.getMessage()+t.getLocalizedMessage());
            }
        });
    }

    //人脸比对
    public void faceCompare(byte[] faceFeatureDate,int user_id){
        if (faceFeatureDate!=null){
            Retrofit retrofit=new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            GetRequest_Interface getRequest=retrofit.create(GetRequest_Interface.class);
            Call<Boolean> call=getRequest.getCompare(faceFeatureDate, user_id);
            call.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Message message=new Message();
                Bundle bundle=new Bundle();
                bundle.putBoolean("flag", response.body().booleanValue());
                message.setData(bundle);
                RegisterAndRecognizeActivity.handler.sendMessage(message);
                    Log.i(TAG, "比对请求成功");
                }
                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {

                }
            });
        }else
            Log.i(TAG, "人脸特征为空");

    }



    //查用户名
    public void findName(int user_id){
        Retrofit retrofit=new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(url)
                .build();
        GetRequest_Interface request=retrofit.create(GetRequest_Interface.class);
        Call<String> call=request.getName(user_id);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.i(TAG, "查询name成功");

                String name=response.body();
                Bundle bundle=new Bundle();
                bundle.putString("name", name);
                Message message=new Message();
                message.setData(bundle);

                ApprovalActivity.handler1.sendMessage(message);

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.i(TAG, "查询name失败"+t.getMessage());
            }
        });
    }

    //查用户信息
    public void findInfo(int user_id){
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GetRequest_Interface request=retrofit.create(GetRequest_Interface.class);
        Call<User> call=request.getInfo(user_id);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.i(TAG,"查询用户信息成功");
                Bundle bundle=new Bundle();
                bundle.putSerializable("user", response.body());

                Message message=new Message();
                message.setData(bundle);
                MyInfoActivity.handler.sendMessage(message);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }
    //查用户头像
    public void getPhoto(int user_id){
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(new ToByteConvertFactory())
                .build();
        GetRequest_Interface request=retrofit.create(GetRequest_Interface.class);
        Call<byte[]> call=request.getPhoto(user_id);
        call.enqueue(new Callback<byte[]>() {
            @Override
            public void onResponse(Call<byte[]> call, Response<byte[]> response) {
                Log.i(TAG,"请求头像成功"+ String.valueOf(response.body().length));
                Bundle bundle= new Bundle();
                Message message=new Message();
                bundle.putByteArray("photo", response.body());
                message.setData(bundle);
                MyInfoActivity.handler1.sendMessage(message);
            }

            @Override
            public void onFailure(Call<byte[]> call, Throwable t) {
                Log.i(TAG,"请求头像failed"+t.getMessage());
            }
        });

    }
    //查手机号和邮箱
    public void addressBook(){
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GetRequest_Interface request=retrofit.create(GetRequest_Interface.class);
        Call<List<User>> call=request.getUserList();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                Bundle bundle=new Bundle();
                List<User> list=response.body();
                bundle.putSerializable("userList", (Serializable) list);
                Message message=new Message();
                message.setData(bundle);
                AddressBookActivity.handler.sendMessage(message);
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });
    }


    //插入考勤信息
    public void insertAtt(Attendance attendance){
//        String url="http://192.168.101.14:8085/";
        //获取retrofit对象
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //创建网络情节接口实例
        GetRequest_Interface request=retrofit.create(GetRequest_Interface.class);
        //对请求进行封装
        Call<Boolean> call=request.insertAtt(attendance.getId(),attendance.getUser_id(),
                attendance.getDate(),attendance.getTime(),attendance.getAddress(),
                attendance.getLongitude(),attendance.getLatitude(),attendance.getFlag(),attendance.getStatus());
        //发送异步请求
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.i("s", "考勤成功");
                Message message=new Message();

                Bundle bundle=new Bundle();
                bundle.putBoolean("success",response.body());
                message.setData(bundle);
                LocationActivity.handler.sendMessage(message);
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.i("s", "网络请求失败，请检查网络后重新打卡"+t.getMessage());

                Message message=new Message();
                Bundle bundle=new Bundle();
                bundle.putBoolean("success", false);
                message.setData(bundle);
                LocationActivity.handler.sendMessage(message);
            }
        });
    }
    public void allAttendance(){

        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())//采用Gson解析
                .build();
        GetRequest_Interface request=retrofit.create(GetRequest_Interface.class);
        Call<List<Attendance>> call=request.getAllAtt(Session.getInstance().getUser_id());
        call.enqueue(new Callback<List<Attendance>>() {
            @Override
            public void onResponse(Call<List<Attendance>> call, Response<List<Attendance>> response) {
                System.out.println("请求全部考勤成功");
                List<Attendance> Attlist=new ArrayList<>();
                Attlist=response.body();
                Bundle bundle=new Bundle();
                //bundle传递对象数组
                bundle.putSerializable("AttList", (Serializable) Attlist);
                Message message=new Message();
                message.setData(bundle);
                AttListActivity.handler.sendMessage(message);
            }

            @Override
            public void onFailure(Call<List<Attendance>> call, Throwable t) {
                System.out.println("请求全部考勤记录api失败");
            }
        });

    }
    public void updateUserInfo(User user){
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //创建网络请求接口实例
        GetRequest_Interface request=retrofit.create(GetRequest_Interface.class);

        //封装请求
        Call<User> call=request.updateUserInfo(user.getUser_id(), user.getName(), user.getTel(), user.getRole(), user.getPicture(),user.getStatus() ,
                user.getCreate_by(),user.getCreate_time() ,user.getDepartment_id() ,user.getDepartment_name(),user.getEmail() );

        //异步请求
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.i(TAG, "完善用户信息请求成功");
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.i(TAG, "完善用户信息请求失败，请检查网络"+t.getMessage());
            }
        });

    }

    //申请
    public void addApply(Apply apply){
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GetRequest_Interface request=retrofit.create(GetRequest_Interface.class);

        Call<Apply> call=request.insertApp(apply.getUser_id(),apply.getA_type(),apply.getA_reason(),apply.getA_date(),
                apply.getA_startDate(),apply.getA_endDate(),apply.getA_leaveType(), apply.getA_hours(),apply.getApprovedby(),apply.getName());

        call.enqueue(new Callback<Apply>() {
            @Override
            public void onResponse(Call<Apply> call, Response<Apply> response) {
                Log.i(TAG, "提交申请信息请求成功");
            }
            @Override
            public void onFailure(Call<Apply> call, Throwable t) {
                Log.i(TAG, "提交申请信息信息请求失败");
                Log.i(TAG,t.getMessage());
            }
        });
    }
    //根据user_id查询申请信息
    public void findApplyByUser_id(int user_id){
        Retrofit retrofit=new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(url)
                .build();
        GetRequest_Interface request=retrofit.create(GetRequest_Interface.class);
        Call<List<Apply>> call=request.getApplyById(user_id);
        call.enqueue(new Callback<List<Apply>>() {
            @Override
            public void onResponse(Call<List<Apply>> call, Response<List<Apply>> response) {
                Log.i(TAG, "查询个人申请信息成功");
                List<Apply> list=new ArrayList<>();
                list=response.body();
                Bundle bundle=new Bundle();
                bundle.putSerializable("apply", (Serializable) list);
                Message message=new Message();
                message.setData(bundle);

                MyApplyActivity.handler.sendMessage(message);

            }

            @Override
            public void onFailure(Call<List<Apply>> call, Throwable t) {

            }
        });

    }
    //根据approvedId and status查询申请信息
    public void findApplyByStatusAndApprovedId(int approvedById,int a_status){
        Retrofit retrofit=new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(url)
                .build();
        GetRequest_Interface request=retrofit.create(GetRequest_Interface.class);
        Call<List<Apply>> call=request.getApproveApply(approvedById, a_status);
        call.enqueue(new Callback<List<Apply>>() {
            @Override
            public void onResponse(Call<List<Apply>> call, Response<List<Apply>> response) {
                Log.i(TAG, "查询待审批信息成功");
                List<Apply> list=new ArrayList<>();
                list=response.body();
                Bundle bundle=new Bundle();
                bundle.putSerializable("apply", (Serializable) list);
                Message message=new Message();
                message.setData(bundle);

                ApprovalActivity.handler.sendMessage(message);

            }

            @Override
            public void onFailure(Call<List<Apply>> call, Throwable t) {

            }
        });
    }
    //审批
    public void approve(int app_id,int a_status){
        Retrofit retrofit=new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(url)
                .build();
        GetRequest_Interface request=retrofit.create(GetRequest_Interface.class);
        Call<List<Apply>> call=request.approve(app_id, a_status, Session.getInstance().getUser_id());
        call.enqueue(new Callback<List<Apply>>() {
            @Override
            public void onResponse(Call<List<Apply>> call, Response<List<Apply>> response) {
                Log.i(TAG, "审批信息成功,更新listView");
                List<Apply> list=new ArrayList<>();
                list=response.body();
                Bundle bundle=new Bundle();
                bundle.putSerializable("apply", (Serializable) list);
                Message message=new Message();
                message.setData(bundle);
                ApprovalActivity.handler.sendMessage(message);

            }

            @Override
            public void onFailure(Call<List<Apply>> call, Throwable t) {
                Log.i(TAG, "审批失败"+t.getMessage());
            }
        });
    }

}
