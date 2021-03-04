package com.arcsoft.arcfacedemo.util.time;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GetTime {
    public  String getCurrentDate(){
        String[] timeList=new String[2];
        timeList=getTime().split(" ");
        return timeList[0];
    }
    public  String getCurrentTime(){
        String[] timeList=new String[2];
        timeList=getTime().split(" ");
        return timeList[1];
    }
    public String getTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//获取当前时间戳
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
//        time1.setText("Date当前日期时间"+simpleDateFormat.format(date));
    }
}
