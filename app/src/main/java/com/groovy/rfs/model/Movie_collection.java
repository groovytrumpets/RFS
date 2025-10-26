package com.groovy.rfs.model;

public class Movie_collection {

    private int idMovie_collection;
    private int Movie_idMovie;
    private int Movie_lists_idMovie_collections;
    private int Movie_lists_User_idUser;

    public Movie_collection() {
    }

    public Movie_collection(int idMovie_collection, int movie_idMovie, int movie_lists_idMovie_collections, int movie_lists_User_idUser) {
        this.idMovie_collection = idMovie_collection;
        Movie_idMovie = movie_idMovie;
        Movie_lists_idMovie_collections = movie_lists_idMovie_collections;
        Movie_lists_User_idUser = movie_lists_User_idUser;
    }

    public int getIdMovie_collection() {
        return idMovie_collection;
    }

    public void setIdMovie_collection(int idMovie_collection) {
        this.idMovie_collection = idMovie_collection;
    }

    public int getMovie_idMovie() {
        return Movie_idMovie;
    }

    public void setMovie_idMovie(int movie_idMovie) {
        Movie_idMovie = movie_idMovie;
    }

    public int getMovie_lists_idMovie_collections() {
        return Movie_lists_idMovie_collections;
    }

    public void setMovie_lists_idMovie_collections(int movie_lists_idMovie_collections) {
        Movie_lists_idMovie_collections = movie_lists_idMovie_collections;
    }

    public int getMovie_lists_User_idUser() {
        return Movie_lists_User_idUser;
    }

    public void setMovie_lists_User_idUser(int movie_lists_User_idUser) {
        Movie_lists_User_idUser = movie_lists_User_idUser;
    }
}
