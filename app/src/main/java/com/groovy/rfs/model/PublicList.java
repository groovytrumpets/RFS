package com.groovy.rfs.model;

public class PublicList {
    private int idMovie_collections;
    private String list_name;

    private String description;
    private String creator_name;

    public String getCreator_name() {
        return creator_name;
    }

    public void setCreator_name(String creator_name) {
        this.creator_name = creator_name;
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
}
