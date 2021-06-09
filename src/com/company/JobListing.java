package com.company;

import java.util.ArrayList;

public class JobListing {
    private String title, company, location, summary, recruiter;

    public JobListing(String title, String company, String location, String summary) {
        this.title = title;
        this.company = company;
        this.location = location;
        this.summary = summary;
        this.recruiter = null;
    }

    public String getCompany() {
        return company;
    }

    public void setRecruiter(String recruiter) {
        this.recruiter = recruiter;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public String getSummary() {
        return summary;
    }

    public String getRecruiter() {
        return recruiter;
    }
}
