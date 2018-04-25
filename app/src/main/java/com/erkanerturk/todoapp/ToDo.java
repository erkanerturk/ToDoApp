package com.erkanerturk.todoapp;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by erkanerturk on 21.04.2018.
 */

@IgnoreExtraProperties
public class ToDo {

    private String title;
    private String info;
    private boolean status;
    private String timestamp;

    public ToDo() {

    }

    public ToDo(String title, String info, boolean status, String timestamp) {
        this.title = title;
        this.info = info;
        this.status = status;
        this.timestamp = timestamp;
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
}
