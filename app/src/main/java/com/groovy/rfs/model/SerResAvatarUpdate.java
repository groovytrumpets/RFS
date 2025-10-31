package com.groovy.rfs.model;

public class SerResAvatarUpdate {
    int success;
    String message;
    String new_avatar_url;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNew_avatar_url() {
        return new_avatar_url;
    }

    public void setNew_avatar_url(String new_avatar_url) {
        this.new_avatar_url = new_avatar_url;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }
}
