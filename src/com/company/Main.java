package com.company;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

public class Main {
    static ArrayList<JobListing> jobsList = new ArrayList<>();

    public static void main(String[] args) {
        GUI();
    }

    public static void GUI() {
        JFrame frame = new JFrame("Recruiter Grabber");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(2,2));

        JLabel indeedLinkLabel = new JLabel("Indeed Job Listing Link:", SwingConstants.CENTER);
        JTextField indeedLink = new JTextField();
        String jobLink = String.valueOf(indeedLink);
        IndeedScrapper(jobLink);

        JButton start = new JButton("Start Scraping");
        start.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String email = JOptionPane.showInputDialog("Input your Linkedin email");
                String password = JOptionPane.showInputDialog("Input Linkedin password");
                LinkedinScrapper(email, password);

                JFrame jobInfo = new JFrame("Job Listings & Recruiters");
            }
        });

        JButton quit = new JButton("Quit");
        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        frame.add(indeedLinkLabel);
        frame.add(indeedLink);
        frame.add(start);
        frame.add(quit);
        frame.setSize(600,100);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void IndeedScrapper(String jobLink) {
        try {
            final Document indeedWebsite = Jsoup.connect(jobLink).get();

            for (Element indeedRow: indeedWebsite.select("div.jobsearch-SerpJobCard")) {
                String jobTitle = indeedRow.select("h2.title a.jobtitle").text();
                String company = "";
                String location = "";
                String summary = "";
                for (Element companyPoint: indeedRow.select("div.sjcl")) {
                    if (companyPoint.select("div span.company").text() != null) company += companyPoint.select("div span.company").text();
                    else company += companyPoint.select("div span.company a.turnstileLink").text();
                }
                if (indeedRow.select("div.sjcl span.location").text() != null) location += indeedRow.select("div.sjcl span.location").text();
                else location += indeedRow.select("div.sjcl div.location").text();
                for(Element summaryPoint: indeedRow.select("div.summary ul")) summary += summaryPoint.select("li").text() + "\n";
                jobsList.add(new JobListing(jobTitle,company,location,summary));
            }
        }catch (Exception e) {
            System.out.println("Website could not be scanned.");
        }
    }

    public static void LinkedinScrapper(String email, String pass) {
        try {
            System.setProperty("webdriver.chrome.driver", "lib\\chromedriver.exe");
            WebDriver driver = new ChromeDriver();
            driver.manage();

            driver.get("https://www.linkedin.com/login");

            WebElement username = driver.findElement(By.id("username"));
            WebElement password = driver.findElement(By.id("password"));
            WebElement login = driver.findElement(By.xpath("//button[text()='Sign in']"));

            username.sendKeys(email);
            password.sendKeys(pass);
            login.click();

            for(JobListing job: jobsList) {
                String company = job.getCompany().replace(" ", "%20");
                String url = "https://www.linkedin.com/search/results/people/?keywords=" + company + "%20recruiter&origin=SWITCH_SEARCH_VERTICAL";
                driver.get(url);
                List<WebElement> people = driver.findElements(By.xpath("//*[@id=\"main\"]/div/div/div[2]/ul/li"));
                ArrayList<String> linkedinProfiles = new ArrayList<String>();
                for (WebElement i : people) {
                    linkedinProfiles.add(i.findElement(By.className("app-aware-link")).getAttribute("href"));
                }
                job.setRecruiters(linkedinProfiles);
            }
            driver.close();
            driver.quit();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}