package com.groovy.rfs.model;

import java.util.List;

public class SerResPubLists {
    private int success;
    private List<PublicList> lists;

    public List<PublicList> getLists() {
        return lists;
    }

    public void setLists(List<PublicList> lists) {
        this.lists = lists;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }
}
