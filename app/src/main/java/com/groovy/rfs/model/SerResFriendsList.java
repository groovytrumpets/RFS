package com.groovy.rfs.model;

import java.util.List;

public class SerResFriendsList {
    private int success;
    private List<User> friends;

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }
}
