package com.company;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Evaluator;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    static Scanner keyboard = new Scanner(System.in);
    static ArrayList<JobListing> jobsList = new ArrayList<>();
    public static void main(String[] args){

        System.out.print("Paste indeed url: ");
        String indeedURL = keyboard.next();
        readLinkedin(indeedURL);
        readLinkedin(indeedURL + "=10");
        readLinkedin(indeedURL + "=20");
        readLinkedin(indeedURL + "=30");

        int x = 0;
    }

    public static void readLinkedin(String url) {
        String indeedurl = url;
        try {

            final Document indeedWebsite = Jsoup.connect(url).get();

            for(Element indeedRow: indeedWebsite.select("div.jobsearch-SerpJobCard")) {

                ArrayList<Recruiter> recruiters = new ArrayList<>();

                String jobTitle = indeedRow.select("h2.title a.jobtitle").text();

                String company = "";

                for(Element companyPoint: indeedRow.select("div.sjcl")) {
                    if(companyPoint.select("div span.company").text() != null){
                        company += companyPoint.select("div span.company").text();
                    }else {
                        company += companyPoint.select("div span.company a.turnstileLink").text();
                    }
                }

                String location = "";

                if(indeedRow.select("div.sjcl span.location").text() != null) {
                    location += indeedRow.select("div.sjcl span.location").text();
                }else {
                    location += indeedRow.select("div.sjcl div.location").text();
                }

                String summary = "";

                for(Element summaryPoint: indeedRow.select("div.summary ul")) {
                    summary += summaryPoint.select("li").text() + "\n";
                }

                String LinkedinURL = "";
                if(company.contains(" ")) {
                    String companyNOSPACE = company.replace(" ", "%20");
                    LinkedinURL += "linkedin.com/search/results/people/?keywords=" + companyNOSPACE + "%20recruiter&origin=GLOBAL_SEARCH_HEADER ";
                }else {
                    LinkedinURL += "linkedin.com/search/results/people/?keywords=" + company +  "%20recruiter&origin=GLOBAL_SEARCH_HEADER ";
                }
                //final Document LinkedInWebsite = Jsoup.connect(LinkedinURL).get();


                jobsList.add(new JobListing(jobTitle,company,location,summary, recruiters));
            }
            System.out.println(indeedWebsite.outerHtml());
        }catch (Exception e) {
            System.out.println("Website could not be scanned.");
        }
    }
}