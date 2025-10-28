package com.groovy.rfs.model;

public class Movie {
    private int idMovie;
    private String title;
    private int release_year; // Đổi từ release_year
    private String duration;
    private String director;
    private String country;
    private String language;
    private String poster_url; // Đổi từ poster_url
    private String wallpaper_url; // Đổi từ wallpaper_url
    private String trailer_url; // Đổi từ trailer_url
    private String description;
    private float rating_avg; // Đổi từ rating_avg
    private String productionCompany; // Đổi từ production_company
    private String ageRating; // Đổi từ age_rating
    private String slug;

    private String genres;
    private String visibility;

    public Movie() {
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public String getAgeRating() {
        return ageRating;
    }

    public void setAgeRating(String ageRating) {
        this.ageRating = ageRating;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getIdMovie() {
        return idMovie;
    }

    public void setIdMovie(int idMovie) {
        this.idMovie = idMovie;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getProductionCompany() {
        return productionCompany;
    }

    public void setProductionCompany(String productionCompany) {
        this.productionCompany = productionCompany;
    }

    public float getRating_avg() {
        return rating_avg;
    }

    public void setRating_avg(float rating_avg) {
        this.rating_avg = rating_avg;
    }

    public int getRelease_year() {
        return release_year;
    }

    public void setRelease_year(int release_year) {
        this.release_year = release_year;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTrailer_url() {
        return trailer_url;
    }

    public void setTrailer_url(String trailer_url) {
        this.trailer_url = trailer_url;
    }

    public String getWallpaper_url() {
        return wallpaper_url;
    }

    public void setWallpaper_url(String wallpaper_url) {
        this.wallpaper_url = wallpaper_url;
    }

    public Movie(String ageRating, String country, String description, String director, String duration, int idMovie, String language, String poster_url, String productionCompany, float rating_avg, int release_year, String slug, String title, String trailer_url, String wallpaper_url) {
        this.ageRating = ageRating;
        this.country = country;
        this.description = description;
        this.director = director;
        this.duration = duration;
        this.idMovie = idMovie;
        this.language = language;
        this.poster_url = poster_url;
        this.productionCompany = productionCompany;
        this.rating_avg = rating_avg;
        this.release_year = release_year;
        this.slug = slug;
        this.title = title;
        this.trailer_url = trailer_url;
        this.wallpaper_url = wallpaper_url;
    }

    public Movie(String poster_url, String title) {
        this.poster_url = poster_url;
        this.title = title;
    }

    public String getPoster_url() {
        return poster_url;
    }

    public void setPoster_url(String poster_url) {
        this.poster_url = poster_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
