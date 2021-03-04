package com.arcsoft.arcfacedemo.session;

public class Session {
    public  String token;
    public  Boolean state;
    public  int user_id;
    private String userName;
    private String department;


    private static Session session=null;
    public static Session getInstance(){
        if (session==null){
            session=new Session();
        }
        return session;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public static Session getSession() {
        return session;
    }

    public static void setSession(Session session) {
        Session.session = session;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
