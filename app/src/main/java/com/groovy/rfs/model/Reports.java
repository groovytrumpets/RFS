package com.groovy.rfs.model;

public class Reports {
    private String reason;
    private String status;
    private String create_date;
    private String reported_comment;
    private String movie_title;

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getMovie_title() {
        return movie_title;
    }

    public void setMovie_title(String movie_title) {
        this.movie_title = movie_title;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReported_comment() {
        return reported_comment;
    }

    public void setReported_comment(String reported_comment) {
        this.reported_comment = reported_comment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
