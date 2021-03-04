package com.arcsoft.arcfacedemo.entity;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

public class NotificationBean implements Serializable {
    @DatabaseField(id = true)//主键
    private String title;
    @DatabaseField
    private String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "NotificationBean{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
