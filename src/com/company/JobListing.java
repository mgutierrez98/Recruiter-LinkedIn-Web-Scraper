package com.company;

import java.util.ArrayList;

public class JobListing {
    private String title, company, location, summary;
    private ArrayList<Recruiter> recruiters;

    public JobListing(String title, String company, String location, String summary, ArrayList<Recruiter> recruiters) {
        this.title = title;
        this.company = company;
        this.location = location;
        this.summary = summary;
        this.recruiters = recruiters;
    }
}
