package com.arcsoft.arcfacedemo.retrofit;

import android.database.Observable;

import com.arcsoft.arcfacedemo.entity.Apply;
import com.arcsoft.arcfacedemo.entity.Attendance;
import com.arcsoft.arcfacedemo.entity.User;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface GetRequest_Interface {
    /*
    * @GET、@POST、@PUT、@DELETE、@HEAD
    * retrofit将url分为两个部分，前半部分在创建retrofit实例的时候通过.buildUrl()设置，后半部分在接口中‘
    * @HTTP作用：替换@GET、@POST、@PUT、@DELETE、@HEAD注解的作用 及 更多功能拓展
    * 具体使用：通过属性method、path、hasBody进行设置
    * */

    /**
     *  Body注解:传递的是json数据
     *
     * 作用于方法的参数
     * 使用该注解定义的参数不可为null
     * 当你发送一个post或put请求,但是又不想作为请求参数或表单的方式发送请求时,使用该注解定义的参数可以直接传入一个实体类,retrofit会通过convert把该实体序列化并将序列化后的结果直接作为请求体发送出去.
     *
     * @param userName
     * @param password
     * @param faceFeatureData
     * @return
     */

    @GET("register")//和方法中的url拼接
    Call<User> getRegister(@Query("userName") String userName,
                          @Query("password") String password,
                          @Query("faceFeatureData") byte[] faceFeatureData);//其中返回类型为Call<*>，*是接收数据的类
    //图片文件和参数一起上传
    @Multipart
    @POST("register1")
    Call<ResponseBody> uploadFile (@Part() MultipartBody.Part files ,@Query("userName")String userName,@Query("password")String password);

    //人臉比对接口
    @GET("faceCompare")
    Call<Boolean> getCompare(@Query("faceFeatureData")byte[] faceFeatureDate,@Query("user_id")int user_id);


    @GET("login")
    Call<User> getLogin(@Query("userName") String userName,
                        @Query("password") String password);

    @GET("update")
    Call<User> updateUserInfo(@Query("user_id") int user_id,@Query("name") String name,@Query("tel") String tel,
                              @Query("role") String role,@Query("picture") String picture,@Query("status") int status,
                              @Query("create_by") String create_by,@Query("create_time") String create_time,
                              @Query("department_id") int department_id,@Query("department_name")String department,
                              @Query("email")String email);

    @GET("findName")
    Call<String> getName(@Query("user_id")int user_id);


    @GET("findInfo")
    Call<User> getInfo(@Query("user_id")int user_id);

    @GET("findPhoto")
    Call<byte[]> getPhoto(@Query("user_id")int user_id);



    @GET("selectAll")
    Call<List<User>> getUserList();

    //考勤------------------------------------------@Query("endaddress")String endaddress,----===================================---------
    @GET("insertAtt")
    Call<Boolean> insertAtt(@Query("id") int id, @Query("user_id") int user_id, @Query("date") String date,
                            @Query("time") String time, @Query("address") String address, @Query("longitude") String longitude,
                            @Query("latitude") String latitude, @Query("flag") int flag, @Query("status") int status);
    @GET("selectAllAtt")
    Call<List<Attendance>> getAllAtt(@Query("user_id") int user_id);




    //提交申请----------------------------------------==============================----
    // get请求不能带@body注解
    //@GET("insertApply")
//    @HTTP(method = "GET", path = "insertApply", hasBody = true)

    @GET("insertApply")
    Call<Apply> insertApp(@Query("user_id")int user_id,@Query("a_type") String a_type,@Query("a_reason") String a_reason,
                          @Query("a_date") String a_date,@Query("a_startDate") String a_startDate,@Query("a_endDate")String a_endDate,@Query("a_leaveType")String a_leaveType,
                          @Query("a_hours")int a_hours,@Query("Approvedby")String Approvedby,@Query("name")String name);

    //根据user_id查询申请记录
    @GET("findByUser_id")
    Call<List<Apply>> getApplyById(@Query("user_id") int user_id);
    //根据status查询考情记录
    @GET("findApplyByApprovedbyId")
    Call<List<Apply>> getApproveApply(@Query("approvedById")int approvedById,@Query("a_status")int a_status);
    //审批----更新申请记录
    @GET("approve")
    Call<List<Apply>> approve(@Query("app_id") int app_id,@Query("a_status")int a_status,@Query("user_id")int user_id);

}
