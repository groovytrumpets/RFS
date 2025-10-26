package com.groovy.rfs.model;

public class MovieList {
    private int idMovie_collections;
    private String list_name;
    private String description;
    private String visibility; // Kiểu ENUM trong DB thường là String trong Java
    private String status;
    private String create_date; // Dùng String cho đơn giản, hoặc dùng Date nếu bạn có TypeAdapter
    private String slug;
    private int User_idUser;

    public MovieList(String create_date, String description, int idMovie_collections, String list_name, String slug, String status, int user_idUser, String visibility) {
        this.create_date = create_date;
        this.description = description;
        this.idMovie_collections = idMovie_collections;
        this.list_name = list_name;
        this.slug = slug;
        this.status = status;
        User_idUser = user_idUser;
        this.visibility = visibility;
    }

    public MovieList() {
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIdMovie_collections() {
        return idMovie_collections;
    }

    public void setIdMovie_collections(int idMovie_collections) {
        this.idMovie_collections = idMovie_collections;
    }

    public String getList_name() {
        return list_name;
    }

    public void setList_name(String list_name) {
        this.list_name = list_name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getUser_idUser() {
        return User_idUser;
    }

    public void setUser_idUser(int user_idUser) {
        User_idUser = user_idUser;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }
}
