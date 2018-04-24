package com.erkanerturk.todoapp;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by erkanerturk on 21.04.2018.
 */

@IgnoreExtraProperties
public class ToDo {
    private String name;
    private String date;
    private String time;
    private String timestamp;

    public ToDo() {

    }

    public ToDo(String name, String time) {
        this.name = name;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
