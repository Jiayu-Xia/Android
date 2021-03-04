package com.arcsoft.arcfacedemo.entity;

public class Apply {
        int app_id;
        int user_id;
        String a_type;
        String a_reason;
        String a_date;
        String a_startDate;
        String a_endDate;
        String a_leaveType;
        String a_leaveAddress;
        String a_endAddress;
        int a_status;
        int a_hours;
        String Approvedby;
        int ApprovedbyId;
        String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApprovedby() {
        return Approvedby;
    }

    public void setApprovedby(String approvedby) {
        Approvedby = approvedby;
    }

    public int getApprovedbyId() {
        return ApprovedbyId;
    }

    public void setApprovedbyId(int approvedbyId) {
        ApprovedbyId = approvedbyId;
    }

    public int getA_hours() {
            return a_hours;
        }

        public void setA_hours(int a_hours) {
            this.a_hours = a_hours;
        }

        public int getApp_id() {
            return app_id;
        }

        public void setApp_id(int app_id) {
            this.app_id = app_id;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public String getA_type() {
            return a_type;
        }

        public void setA_type(String a_type) {
            this.a_type = a_type;
        }

        public String getA_reason() {
            return a_reason;
        }

        public void setA_reason(String a_reason) {
            this.a_reason = a_reason;
        }

        public String getA_date() {
            return a_date;
        }

        public void setA_date(String a_date) {
            this.a_date = a_date;
        }

        public String getA_startDate() {
            return a_startDate;
        }

        public void setA_startDate(String a_startDate) {
            this.a_startDate = a_startDate;
        }

        public String getA_endDate() {
            return a_endDate;
        }

        public void setA_endDate(String a_endDate) {
            this.a_endDate = a_endDate;
        }

        public String getA_leaveType() {
            return a_leaveType;
        }

        public void setA_leaveType(String a_leaveType) {
            this.a_leaveType = a_leaveType;
        }

        public String getA_leaveAddress() {
            return a_leaveAddress;
        }

        public void setA_leaveAddress(String a_leaveAddress) {
            this.a_leaveAddress = a_leaveAddress;
        }

        public String getA_endAddress() {
            return a_endAddress;
        }

        public void setA_endAddress(String a_endAddress) {
            this.a_endAddress = a_endAddress;
        }

        public int getA_status() {
            return a_status;
        }

        public void setA_status(int a_status) {
            this.a_status = a_status;
        }

    @Override
    public String toString() {
        return "Apply{" +
                "app_id=" + app_id +
                ", user_id=" + user_id +
                ", a_type='" + a_type + '\'' +
                ", a_reason='" + a_reason + '\'' +
                ", a_date='" + a_date + '\'' +
                ", a_startDate='" + a_startDate + '\'' +
                ", a_endDate='" + a_endDate + '\'' +
                ", a_leaveType='" + a_leaveType + '\'' +
                ", a_leaveAddress='" + a_leaveAddress + '\'' +
                ", a_endAddress='" + a_endAddress + '\'' +
                ", a_status=" + a_status +
                ", a_hours=" + a_hours +
                ", Approvedby='" + Approvedby + '\'' +
                ", ApprovedbyId=" + ApprovedbyId +
                ", name='" + name + '\'' +
                '}';
    }
}
