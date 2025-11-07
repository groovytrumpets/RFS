package com.groovy.rfs.model;

import java.util.List;

public class SerResReports {
    private int success;
    private List<Reports> reports;

    public List<Reports> getReports() {
        return reports;
    }

    public void setReports(List<Reports> reports) {
        this.reports = reports;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }
}
