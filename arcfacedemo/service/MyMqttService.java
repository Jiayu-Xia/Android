package com.arcsoft.arcfacedemo.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.activity.AnnouncementActivity;
import com.arcsoft.arcfacedemo.activity.SuccessActivity;
import com.arcsoft.arcfacedemo.database.SqliteDaoOrm;
import com.arcsoft.arcfacedemo.entity.NotificationBean;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.lang.reflect.Method;

public class MyMqttService extends Service {
    public static final String TAG=MyMqttService.class.getName();

    private static MqttAndroidClient mqttAndroidClient;
    private MqttConnectOptions mqttConnectOptions;
    //服务器地址(协议、地址、端口号)
    public static final String HOST="tcp://10.64.44.213:61613";
    //用户名
    public static final String USERNAME="admin";
    //密码
    public static final String PASSWORD="password";
    //发布主题
    public static final String PUBLISH_TOPIC="message_notification";
    //响应主题
    public static final String RESPONSE_TOPIC="message_arrived";
    //客户端ID，一般一客户端唯一标识符表示，这里用设备序列号表示
    public String CLIENTID = getSerialNumber();

    /**
     * 获取手机序列号
     *
     * @return 手机序列号
     */
    @SuppressLint({"NewApi", "MissingPermission"})
    public static String getSerialNumber() {
        String serial = "";
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {//9.0+
                serial = Build.getSerial();
            } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {//8.0+
                serial = Build.SERIAL;
            } else {//8.0-
                Class<?> c = Class.forName("android.os.SystemProperties");
                Method get = c.getMethod("get", String.class);
                serial = (String) get.invoke(c, "ro.serialno");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("e", "读取设备序列号异常：" + e.toString());
        }
        return serial;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
    /**
     * 开启服务
     */
    public static void startService(Context mContext){
        mContext.startService(new Intent(mContext,MyMqttService.class));
        Log.i(" " , "服务启动");
    }
    /**
     * 发布消息（模拟其他客户端发布消息）
     */
    public static void publish(String message){
        String topic=PUBLISH_TOPIC;
        Integer qos=2;
        Boolean retained=false;
        try {
            //参数分别为：主题、消息的字节数组、服务质量、是否在服务器保留断开连接后的最后一条消息
            mqttAndroidClient.publish(topic, message.getBytes(),qos.intValue(),retained.booleanValue());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    /**
     * 响应（收到其他客户端的消息后，响应给对方告知消息已到达或消息有问题等）
     */
    public void response(String message){
        String topic=RESPONSE_TOPIC;
        Integer qos=2;
        Boolean retained=false;
        try {
            mqttAndroidClient.publish(topic, message.getBytes(),qos.intValue(),retained.booleanValue());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    /**
     * 初始化
     */
    private void init(){
        String serverURI=HOST;//服务器地址（协议+地址+端口号）
        mqttAndroidClient=new MqttAndroidClient(this, serverURI, CLIENTID);
        mqttAndroidClient.setCallback(mqttCallback);//设置监听订阅消息回调
        mqttConnectOptions=new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(true);//设置是否清除缓存
        mqttConnectOptions.setConnectionTimeout(10);//设置超时
        mqttConnectOptions.setKeepAliveInterval(20);//设置发送心跳包的时间间隔
        mqttConnectOptions.setUserName(USERNAME);//设置用户名
        mqttConnectOptions.setPassword(PASSWORD.toCharArray());//设置密码

        boolean doConnect=true;
        String message="{\"terminal_uid\":\"" + CLIENTID + "\"}";
        String topic=PUBLISH_TOPIC;
        Integer qos=2;
        Boolean retained=false;
        if ((!message.equals("")) || (!topic.equals(""))) {
            // 最后的遗嘱
            try {
                mqttConnectOptions.setWill(topic, message.getBytes(), qos.intValue(), retained.booleanValue());
            } catch (Exception e) {
                Log.i(TAG, "Exception Occured", e);
                doConnect = false;
                iMqttActionListener.onFailure(null, e);
            }
        }
        if (doConnect){
            doClientConnection();
        }
    }

    /**
     * 判断网络是否连接
     */
    private boolean isConnectIsNormal(){
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            String name = info.getTypeName();
            Log.i(TAG, "当前网络名称：" + name);
            return true;
        } else {
            Log.i(TAG, "没有可用网络");
            /*没有可用网络的时候，延迟3秒再尝试重连*/
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doClientConnection();
                }
            }, 3000);
            return false;
        }
    }

    /**
     * 连接MQTT服务器
     */
    private void doClientConnection(){
        if (!mqttAndroidClient.isConnected()&&isConnectIsNormal()){
            try {
                mqttAndroidClient.connect(mqttConnectOptions,null,iMqttActionListener);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * MQTT是否连接成功的监听
     */
    private IMqttActionListener iMqttActionListener = new IMqttActionListener() {

        @Override
        public void onSuccess(IMqttToken arg0) {
            Log.i(TAG, "连接成功 ");
            try {
                mqttAndroidClient.subscribe(PUBLISH_TOPIC, 2);//订阅主题，参数：主题、服务质量
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable arg1) {
            arg1.printStackTrace();
            Log.i(TAG, "连接失败 ");
            doClientConnection();//连接失败，重连（可关闭服务器进行模拟）
        }
    };

    //订阅主题的回调
    private MqttCallback mqttCallback = new MqttCallback() {
        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            NotificationBean notificationBean=new NotificationBean();

            //接收到的字符串重新编码
            String MyMessage=new String(message.getPayload(),"GB2312");
            Log.i(TAG, "收到消息： " + new String(message.getPayload(),"GB2312"));
            String [] split=MyMessage.split(";");
            String tile=split[0];
            String contentText=split[1];
            //系统弹窗
            notification(tile,contentText);

            //将notification写入sqLite
            SqliteDaoOrm sqliteDaoOrm=new SqliteDaoOrm(MyMqttService.this);
            notificationBean.setTitle(tile);
            notificationBean.setContent(contentText);
            sqliteDaoOrm.addNotification(notificationBean);

            //收到消息，这里弹出Toast表示。如果需要更新UI，可以使用广播或者EventBus进行发送
            //Toast.makeText(getApplicationContext(), "messageArrived: " + new String(message.getPayload()), Toast.LENGTH_LONG).show();
            //收到其他客户端的消息后，响应给对方告知消息已到达或者消息有问题等
            response("message arrived");
        }
        @Override
        public void deliveryComplete(IMqttDeliveryToken arg0) {

        }

        @Override
        public void connectionLost(Throwable arg0) {
            Log.i(TAG, "连接断开 ");
            doClientConnection();//连接断开，重连
        }
    };

    @Override
    public void onDestroy(){
        try {
            Log.e(TAG, "onDestroy");
            //断开MQTT连接
            mqttAndroidClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
    /**
     * 状态栏弹窗
     * @param tile
     * @param contentText
     */
    public String notification(String tile, String contentText){
        Context context=MyMqttService.this;

        NotificationManager notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder=new Notification.Builder(context)
                .setOngoing(true)//正在运行
                .setWhen(System.currentTimeMillis())//什么时候弹出通知栏
                .setContentTitle(tile)//标题
                .setContentText(contentText)//内容
                .setContentIntent(getContentClickIntent(context))
                .setAutoCancel(true)//是否自动消失
                .setSmallIcon(R.mipmap.ic_launcher);//设置小图标
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            //android 8.0 以上弹出状态栏
            String channelID="0";
            String channelName="channel_name";
            NotificationChannel channel=new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
            if (notificationManager!=null){
                Log.w(TAG, "弹窗通知");
                notificationManager.createNotificationChannel(channel);
                builder.setChannelId(channelID);
                builder.build();
                notificationManager.notify(0, builder.build());
            }
        }else{
            startForeground(1,new Notification());
            Log.i(TAG, "Android版本低于8.0");
            //            //弹出通知栏 8.0以下系统弹出方式
            if (notificationManager != null) {
                notificationManager.notify(0, builder.build());
            }
        }
        return "弹窗成功";
    }
    private PendingIntent getContentClickIntent(Context context) {
        Intent intent=new Intent(context, AnnouncementActivity.class);
        PendingIntent contentIntent=PendingIntent.getActivity(context, 0, intent, 0);
        return contentIntent;
    }

}

