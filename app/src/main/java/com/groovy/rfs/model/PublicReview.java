package com.groovy.rfs.model;

public class PublicReview {
    private int idRating;
    private float score;
    private String comment;
    private int User_idUser; // ID người viết
    private String username;
    private String avatar; // Có thể null
    private int idMovie;
    private String movie_title;
    private Integer release_year; // Dùng Integer để nhận null
    private String movie_poster_url;

    public PublicReview(String avatar, String comment, int idMovie, int idRating, String movie_poster_url, String movie_title, Integer release_year, float score, int user_idUser, String username) {
        this.avatar = avatar;
        this.comment = comment;
        this.idMovie = idMovie;
        this.idRating = idRating;
        this.movie_poster_url = movie_poster_url;
        this.movie_title = movie_title;
        this.release_year = release_year;
        this.score = score;
        User_idUser = user_idUser;
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getIdMovie() {
        return idMovie;
    }

    public void setIdMovie(int idMovie) {
        this.idMovie = idMovie;
    }

    public int getIdRating() {
        return idRating;
    }

    public void setIdRating(int idRating) {
        this.idRating = idRating;
    }

    public String getMovie_poster_url() {
        return movie_poster_url;
    }

    public void setMovie_poster_url(String movie_poster_url) {
        this.movie_poster_url = movie_poster_url;
    }

    public String getMovie_title() {
        return movie_title;
    }

    public void setMovie_title(String movie_title) {
        this.movie_title = movie_title;
    }

    public Integer getRelease_year() {
        return release_year;
    }

    public void setRelease_year(Integer release_year) {
        this.release_year = release_year;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public int getUser_idUser() {
        return User_idUser;
    }

    public void setUser_idUser(int user_idUser) {
        User_idUser = user_idUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
