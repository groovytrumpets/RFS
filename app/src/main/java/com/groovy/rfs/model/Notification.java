package com.groovy.rfs.model;

public class Notification {
    private int idNotifications;
    private int User_idSender;
    private int User_idReceiver;
    private String type;
    private int is_read;
    private String create_date;
    private int reference_id;
    private String sender_username;
    private String sender_avatar;

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public int getIdNotifications() {
        return idNotifications;
    }

    public void setIdNotifications(int idNotifications) {
        this.idNotifications = idNotifications;
    }

    public int getIs_read() {
        return is_read;
    }

    public void setIs_read(int is_read) {
        this.is_read = is_read;
    }

    public int getReference_id() {
        return reference_id;
    }

    public void setReference_id(int reference_id) {
        this.reference_id = reference_id;
    }

    public String getSender_avatar() {
        return sender_avatar;
    }

    public void setSender_avatar(String sender_avatar) {
        this.sender_avatar = sender_avatar;
    }

    public String getSender_username() {
        return sender_username;
    }

    public void setSender_username(String sender_username) {
        this.sender_username = sender_username;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getUser_idReceiver() {
        return User_idReceiver;
    }

    public void setUser_idReceiver(int user_idReceiver) {
        User_idReceiver = user_idReceiver;
    }

    public int getUser_idSender() {
        return User_idSender;
    }

    public void setUser_idSender(int user_idSender) {
        User_idSender = user_idSender;
    }
}
