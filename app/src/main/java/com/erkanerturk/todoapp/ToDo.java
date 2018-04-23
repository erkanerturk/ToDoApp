package com.erkanerturk.todoapp;

/**
 * Created by erkanerturk on 21.04.2018.
 */

public class ToDo {
    private String name;
    private String date;
    private String time;

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
}
