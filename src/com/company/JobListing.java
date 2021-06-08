package com.company;

import java.util.ArrayList;

public class JobListing {
    private String title, company, location, summary;
    private ArrayList<String> recruiters;

    public JobListing(String title, String company, String location, String summary) {
        this.title = title;
        this.company = company;
        this.location = location;
        this.summary = summary;
        this.recruiters = null;
    }

    public String getCompany() {
        return company;
    }

    public void setRecruiters(ArrayList<String> recruiters) {
        this.recruiters = recruiters;
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

    public ArrayList<String> getRecruiters() {
        return recruiters;
    }
}
