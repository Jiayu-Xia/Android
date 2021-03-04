package com.arcsoft.arcfacedemo.database;

import android.app.Notification;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.arcsoft.arcfacedemo.entity.NotificationBean;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.util.HashMap;
import java.util.Map;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    public final String CREATE_TABLE="Create table";
    //数据库名
    private static final String DATABASE_NAME = "notification.db";
    private Map<String, Dao> daos = new HashMap<String, Dao>();
    //创建databaseHelper实例
    private static DatabaseHelper instance;
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }
    //创建数据库sql语句 并 执行,相当于初始化数据库，
    // 这里是新建了一张表这个方法继承自SQLiteOpenHelper,会自动调用，
    // 也就是，当新建了一个DatabaseHelper对象时，就会默认新建一张表user，表里存着名为name项
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
            try {
                TableUtils.createTable(connectionSource, NotificationBean.class);
                Log.i(CREATE_TABLE, "Notification表建立成功-------------------------------------------");
//                TableUtils.createTable(connectionSource,FileUri.class);
//                Log.i(CREATE_TABLE, "建FileUri表成功-------------------------------------------");
            } catch (SQLException | java.sql.SQLException e) {
                e.printStackTrace();
            }
    }
    //database update
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        try {
            TableUtils.dropTable(connectionSource,  Notification.class,true);
            Log.i(CREATE_TABLE, "删除Notification表成功-------------------------------------------");
//            TableUtils.dropTable(connectionSource, FileUri.class,true);
//            Log.i(CREATE_TABLE, "删除FileUri表成功-------------------------------------------");
            //更新，先删除原来的表，在重新创建表
            onCreate(sqLiteDatabase, connectionSource);
        } catch (SQLException | java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 单例获取该Helper
     * 保证线程安全
     * @param context
     * @return
     */
    public static synchronized DatabaseHelper getHelper(Context context) {
        //获得context
        //context = context.getApplicationContext();
        if (instance == null) {
            synchronized (DatabaseHelper.class) {
                if (instance == null)  {
                    instance = new DatabaseHelper(context);
                    System.out.println("获得单例Helper---------------------------------");
                }
            }
        }
        return instance;
    }

    //获取数据库操作工具
    public synchronized Dao getDao(Class clazz) throws SQLException, java.sql.SQLException {
        Dao dao = null;
        String className = clazz.getSimpleName();
        if (daos.containsKey(className)) {
            dao = daos.get(className);
        }
        if (dao == null) {
            dao = super.getDao(clazz);
            daos.put(className, dao);
        }
        System.out.println("获得单例dao---------------------------------");
        return dao;
    }
    /**
     * 释放资源
     */
    @Override
    public void close() {
        super.close();
        for (String key : daos.keySet()) {
            Dao dao = daos.get(key);
            dao = null;
            System.out.println("资源清理====================================");
        }
    }
}
