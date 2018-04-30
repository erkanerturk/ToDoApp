package com.erkanerturk.todoapp;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;

/**
 * Created by erkanerturk on 21.04.2018.
 */

@IgnoreExtraProperties
public class ToDo {

    private String title;
    private String info;
    private boolean status;
    private String timestamp;
    private Date timeMillis;

    public ToDo() {

    }

    public ToDo(String title, String info, boolean status, String timestamp, Date timeMillis) {
        this.title = title;
        this.info = info;
        this.status = status;
        this.timestamp = timestamp;
        this.timeMillis = timeMillis;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Date getTimeMillis() {
        return timeMillis;
    }

    public void setTimeMillis(Date timeMillis) {
        this.timeMillis = timeMillis;
    }
}
