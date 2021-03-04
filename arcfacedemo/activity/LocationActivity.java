package com.arcsoft.arcfacedemo.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.entity.Attendance;
import com.arcsoft.arcfacedemo.retrofit.GetRequest;
import com.arcsoft.arcfacedemo.session.Session;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * 考勤页面分为内勤、外勤、补打卡
 */
public class LocationActivity extends AppCompatActivity {
    public static Handler handler;
    public static Handler handler1;

    private boolean isFirstLocate = true;

    MyLocationConfiguration.LocationMode locationMode;
    public static final String TAG="location";
    public static String myLocation;
    public static double Longitude;
    //纬度
    public static double Latitude;

    MapView mMapView = null;
    BaiduMap mBaiduMap=null;
    LocationClient mLocationClient=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化地图sdk
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_location);

        TextView locationText=findViewById(R.id.locationText);
        Button button=findViewById(R.id.button);
        Button button1=findViewById(R.id.button2);
        Button button2=findViewById(R.id.button3);
        Attendance attendance=new Attendance();

        //百度地图显示
        //地图控件
        mMapView =findViewById(R.id.bmapView);
        // 得到地图
        mBaiduMap = mMapView.getMap();
        //定位图层
        mBaiduMap.setMyLocationEnabled(true);

        mMapView.removeViewAt(1);// 删除百度地图Logo
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(20));
        //定位初始化
        mLocationClient= new LocationClient(this);
        //通过LocationClientOption设置LocationClient相关参数
        LocationClientOption option = new LocationClientOption();

        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        // 可选，设置地址信息
        option.setIsNeedAddress(true);
        //可选，设置是否需要地址描述
        option.setIsNeedLocationDescribe(true);

        //设置locationClientOption
        mLocationClient.setLocOption(option);

        //注册LocationListener监听器
        MyLocationListener myLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myLocationListener);
        //开启地图定位图层
        mLocationClient.start();


        //获取位置信息
        Location location=getLocation(this);
        Geocoder gc =  new Geocoder(this, Locale.getDefault());

        List<Address> locationList = null;
        if (location!=null){
            //经度
            Longitude=location.getLongitude();
            //纬度
            Latitude=location.getLatitude();
            try {
                //此处有耗时操作，最好异步处理
                /**
                 * locationlist中包含了所有的位置信息
                 */
                locationList = gc.getFromLocation(location.getLatitude(),location.getLongitude(),10);
                Address address=locationList.get(0);
                myLocation= address.getAddressLine(0);
                String locationString=locationList.toString();
                System.out.println(locationString);
                Toast.makeText(this, "经度："+Longitude+"\n" +
                        "纬度："+Latitude+"\n", Toast.LENGTH_SHORT).show();
                locationText.setText(
                        "您当前位置为："+myLocation+"\n"
                +"如位置无误请点击完成考勤，若位置有问题，请点击重新定位");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //重新获取位置
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LocationActivity.this,LocationActivity.class);
                startActivity(intent);
            }
        });
        //完成内勤考勤，retrofit网络请求
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Toast.makeText(LocationActivity.this, getTime(), Toast.LENGTH_SHORT).show();
                String[] timeList=new String[2];
                timeList=getTime().split(" ");

                attendance.setUser_id(Session.getInstance().getUser_id());
                attendance.setDate(timeList[0]);
                attendance.setTime(timeList[1]);
                attendance.setAddress(myLocation);
                attendance.setLongitude(String.valueOf(Longitude));
                attendance.setLatitude(String.valueOf(Latitude));
                attendance.setFlag(0);//外勤1，内勤0
                attendance.setStatus(1);
                //获取所有考勤表的字段，然后ritrofit上传到服务器；
                GetRequest getRequest=new GetRequest();

                //网络请求
                getRequest.insertAtt(attendance);
                //Toast.makeText(LocationActivity.class, "")
                handler=new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);

                        boolean success=msg.getData().getBoolean("success");
                        if (success==true){
                            AlertDialog alertDialog1 = new AlertDialog.Builder(LocationActivity.this)
                                    .setTitle("考勤成功")//标题
                                    .setMessage("")//内容
                                    .setIcon(R.mipmap.ic_launcher)//图标
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener(){

                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            startActivity(new Intent(LocationActivity.this,ChooseFunctionActivity.class));
                                        }
                                    })
                                    .create();
                            alertDialog1.show();
                        }else{
                            AlertDialog alertDialog1 = new AlertDialog.Builder(LocationActivity.this)
                                    .setTitle("考勤失败")//标题
                                    .setMessage("请检查网络和位置信息后重新打卡")//内容
                                    .setIcon(R.mipmap.ic_launcher)//图标
                                    .setPositiveButton("重新打卡", new DialogInterface.OnClickListener(){

                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            startActivity(new Intent(LocationActivity.this,RegisterAndRecognizeActivity.class));
                                        }
                                    })
                                    .create();
                            alertDialog1.show();
                        }

                    }
                };
            }
        });

        //完成外勤打卡
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Toast.makeText(LocationActivity.this, getTime(), Toast.LENGTH_SHORT).show();
                String[] timeList=new String[2];
                timeList=getTime().split(" ");
                attendance.setUser_id(Session.getInstance().getUser_id());
                attendance.setDate(timeList[0]);
                attendance.setTime(timeList[1]);
                attendance.setAddress(myLocation);
                attendance.setLongitude(String.valueOf(Longitude));
                attendance.setLatitude(String.valueOf(Latitude));
                attendance.setFlag(1);//外勤1，内勤0
                attendance.setStatus(1);
                //获取所有考勤表的字段，然后ritrofit上传到服务器；
                GetRequest getRequest=new GetRequest();

                //网络请求
                getRequest.insertAtt(attendance);
                //Toast.makeText(LocationActivity.class, "")
                handler1=new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);

                        boolean success=msg.getData().getBoolean("success");
                        if (success==true){
                            AlertDialog alertDialog1 = new AlertDialog.Builder(LocationActivity.this)
                                    .setTitle("考勤成功")//标题
                                    .setMessage("")//内容
                                    .setIcon(R.mipmap.ic_launcher)//图标
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener(){

                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            startActivity(new Intent(LocationActivity.this,ChooseFunctionActivity.class));
                                        }
                                    })
                                    .create();
                            alertDialog1.show();
                        }else{
                            AlertDialog alertDialog1 = new AlertDialog.Builder(LocationActivity.this)
                                    .setTitle("考勤失败")//标题
                                    .setMessage("请检查网络和位置信息后重新打卡")//内容
                                    .setIcon(R.mipmap.ic_launcher)//图标
                                    .setPositiveButton("重新打卡", new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            startActivity(new Intent(LocationActivity.this,RegisterAndRecognizeActivity.class));
                                        }
                                    })
                                    .create();
                            alertDialog1.show();
                        }

                    }
                };

            }
        });



    }
    /**
     * 获取位置信息的提供者
     */
    private static String getProvider(@NotNull LocationManager locationManager){
        //获取位置信息提供者列表
        List<String> providerList=locationManager.getProviders(true);

        if (providerList.contains(LocationManager.NETWORK_PROVIDER)){
            //获取network定位
            return LocationManager.NETWORK_PROVIDER;
        }else if(providerList.contains(LocationManager.GPS_PROVIDER)){
            //获取GPS定位
            return  LocationManager.GPS_PROVIDER;
        }
        Log.e(TAG, "获取位置信息提供者失败");
        return null;
    }

    public static Location getLocation(@NotNull Context context) {

        /*获取LocationManager对象*/
        LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        //位置信息提供者
        String provider = getProvider(locationManager);
        Log.i("Provider", provider);
        if (provider == null) {
            Toast.makeText(context, "定位失败1,provider为空", Toast.LENGTH_SHORT).show();
            return null;
        }
        //系统权限检查警告，需要做权限判断
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling ActivityCompat#requestPermissions
            return null;
        }
        Location myLocation=locationManager.getLastKnownLocation(provider);
        if(myLocation!=null){
            return myLocation;
        }
        Toast.makeText(context, "定位失败2,location为空", Toast.LENGTH_SHORT).show();
        return null;
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //mapView 销毁后不在处理新接收的位置
            if (location == null || mMapView == null){
                return;
            }

            // 如果是第一次定位
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            if (isFirstLocate) {
                isFirstLocate = false;
                //给地图设置状态
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(ll));

            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);

            // ------------------  以下是可选部分 ------------------
            // 自定义地图样式，可选
            //自定义精度圈填充颜色
//            int accuracyCircleFillColor = 0xAAFFFF88;
//            //自定义精度圈边框颜色
//            int accuracyCircleStrokeColor = 0xAA00FF00;
//            // 更换定位图标，这里的图片是放在 drawble 文件下的
//            BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);
//            // 定位模式 地图SDK支持三种定位模式：NORMAL（普通态）, FOLLOWING（跟随态）, COMPASS（罗盘态）
//            locationMode = MyLocationConfiguration.LocationMode.NORMAL;
//            // 定位模式、是否开启方向、设置自定义定位图标、精度圈填充颜色以及精度圈边框颜色5个属性（此处只设置了前三个）。
//            MyLocationConfiguration mLocationConfiguration = new MyLocationConfiguration(locationMode,true,mCurrentMarker,accuracyCircleFillColor,accuracyCircleStrokeColor);
//            // 使自定义的配置生效
//            mBaiduMap.setMyLocationConfiguration(mLocationConfiguration);
            // ------------------  可选部分结束 ------------------

        }
    }

    //获取系统时间
    public String getTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//获取当前时间戳
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
//        time1.setText("Date当前日期时间"+simpleDateFormat.format(date));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
    }
}