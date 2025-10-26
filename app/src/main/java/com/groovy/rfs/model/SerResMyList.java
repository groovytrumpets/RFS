package com.groovy.rfs.model;

import java.util.List;

public class SerResMyList {
    private int success;
    private String message;
    private List<MovieList> lists;

    public SerResMyList() {
    }

    public SerResMyList(List<MovieList> lists, String message, int success) {
        this.lists = lists;
        this.message = message;
        this.success = success;
    }

    public List<MovieList> getLists() {
        return lists;
    }

    public void setLists(List<MovieList> lists) {
        this.lists = lists;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }
}
