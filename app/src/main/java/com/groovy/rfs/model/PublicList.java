package com.groovy.rfs.model;

import java.util.List;

public class PublicList {
    private int idMovie_collections;
    private String list_name;
    private String avatar;
    private String description;
    private String creator_name;
    private List<MoviePreview> movies_preview;

    public List<MoviePreview> getMovies_preview() {
        return movies_preview;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setMovies_preview(List<MoviePreview> movies_preview) {
        this.movies_preview = movies_preview;
    }

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
