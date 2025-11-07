package com.groovy.rfs.model;

import java.util.List;

public class SerResUserSearch {
    private int success;
    private List<User> users;

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
