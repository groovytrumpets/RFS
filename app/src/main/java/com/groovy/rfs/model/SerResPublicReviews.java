package com.groovy.rfs.model;

import java.util.List;

public class SerResPublicReviews {

    private int success;

    public List<PublicReview> getReviews() {
        return reviews;
    }

    public void setReviews(List<PublicReview> reviews) {
        this.reviews = reviews;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    private List<PublicReview> reviews; // Danh sách PublicReview
}
