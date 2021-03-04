package com.arcsoft.arcfacedemo.entity;

import java.io.Serializable;
import java.util.Arrays;

public class User implements Serializable {
    /**
     * id : 1
     * userName : tjm
     * password : tjm
     * age : 23
     */
    private int user_id;
    private String userName;
    private String password;
    private byte[] faceFeatureData;
    private String name;
    private String tel;
    private String role;
    private String picture;
    private int status;
    private String create_by;
    private String create_time;
    private int department_id;
    private String department_name;
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreate_by() {
        return create_by;
    }

    public void setCreate_by(String create_by) {
        this.create_by = create_by;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public int getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(int department_id) {
        this.department_id = department_id;
    }

    public String getDepartment_name() {
        return department_name;
    }

    public void setDepartment_name(String department_name) {
        this.department_name = department_name;
    }

    public byte[] getFaceFeatureData() {
        return faceFeatureData;
    }

    public void setFaceFeatureData(byte[] faceFeatureData) {
        this.faceFeatureData = faceFeatureData;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "user_id=" + user_id +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", faceFeatureData=" + Arrays.toString(faceFeatureData) +
                ", name='" + name + '\'' +
                ", tel='" + tel + '\'' +
                ", role='" + role + '\'' +
                ", picture='" + picture + '\'' +
                ", status=" + status +
                ", create_by='" + create_by + '\'' +
                ", create_time='" + create_time + '\'' +
                ", department_id=" + department_id +
                ", department_name='" + department_name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
