package com.groovy.rfs.model;

import java.util.List;

public class SerResMovies {
    private int success;

    private List<Movie> movies;


    private String message;

    public SerResMovies() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public SerResMovies(String message, List<Movie> movies, int success) {
        this.message = message;
        this.movies = movies;
        this.success = success;
    }
}
