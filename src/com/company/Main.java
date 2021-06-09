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

        JButton start = new JButton("Start Grabbing");
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String jobLink = indeedLink.getText();
                IndeedScrapper(jobLink);
                String email = JOptionPane.showInputDialog("Input Linkedin email");
                String password = JOptionPane.showInputDialog("Input Linkedin password");
                LinkedinScrapper(email, password);
                JFrame jobInfo = new JFrame("Job Listings & Recruiters");
                String[] columnNames = {"Job Title", "Company", "Location", "Summary", "Recruiters"};

                Object[][] data = new Object[jobsList.size()][5];
                int x = 0;
                for(int  i = 0; i < jobsList.size(); i++) {
                    data[i][0] = jobsList.get(i).getTitle();
                    data[i][1] = jobsList.get(i).getCompany();
                    data[i][2] = jobsList.get(i).getLocation();
                    data[i][3] = jobsList.get(i).getSummary();
                    if(jobsList.get(i).getRecruiter() == null)  data[i][4] = "";
                    else data[i][4] = jobsList.get(i).getRecruiter();
                }

                JTable table = new JTable(data, columnNames);
                JScrollPane scrollPane = new JScrollPane(table);
                table.setFillsViewportHeight(true);
                jobInfo.add(scrollPane);
                jobInfo.setSize(600,400);
                jobInfo.setLocationRelativeTo(null);
                jobInfo.setVisible(true);

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
                try {
                    WebElement companyRecruiter = driver.findElement(By.className("reusable-search__result-container"));
                    job.setRecruiter(companyRecruiter.findElement(By.className("app-aware-link")).getAttribute("href"));
                }catch (Exception e) {
                }
            }
            driver.close();
            driver.quit();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}