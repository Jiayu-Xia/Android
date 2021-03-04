package com.arcsoft.arcfacedemo.database;

import android.content.Context;

import com.arcsoft.arcfacedemo.entity.NotificationBean;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//数据库操作类（增删改查）
public class SqliteDaoOrm {
    public final String DaoOperate="DaoOperate";
    //获得context
    private Context context;
    //DeviceDescriptionBean的dao
    private Dao<NotificationBean, Integer> notificationBeansDao;
    private DatabaseHelper helper;
    public SqliteDaoOrm(Context context){
        this.context=context;
        helper=DatabaseHelper.getHelper(context);
        try {
            //DeviceDescriptionBean的dao
            notificationBeansDao=helper.getDao(NotificationBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /*
    *
    * 这里是NotificationBean表的数据库操作的方法定义
    * ======================================================================================================================================
    *
    * */
    //添加
    public void addNotification(NotificationBean notification){
        try {
            notificationBeansDao.createOrUpdate(notification);
            System.out.println("添加了一条fileUri记录"+"\n");
            System.out.println(notification.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //查询全部
    public List<NotificationBean> selectAllNotification(){
        try {
            return notificationBeansDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    //删除一条
    public void deleteOne(NotificationBean notificationBean){
        try {
            notificationBeansDao.delete(notificationBean);
            System.out.println("删除一条文件信息"+notificationBean.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //删除全部
    public void deleteAll(){
        List<NotificationBean> list=new ArrayList<>();
        try {
            list=notificationBeansDao.queryForAll();
            notificationBeansDao.delete(list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
