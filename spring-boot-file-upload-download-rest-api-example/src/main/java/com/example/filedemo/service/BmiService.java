package com.example.filedemo.service;

import java.io.IOException;

import com.example.filedemo.request.BmiInfoRequest;
import com.example.filedemo.response.zscore.BmiInfoResponse;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BmiService {

  private final String URL_ZSCORE = "https://zscore.research.chop.edu/result.php";

  @Autowired
  public BmiService() {}

  public BmiInfoResponse getBmiInfo(BmiInfoRequest req) {
    // Process the request at https://zscore.research.chop.edu/result.php with form params
    Document document;

    try {
      Response res = Jsoup.connect(URL_ZSCORE)
        .userAgent("Mozilla/5.0")
        .timeout(10 * 1000)
        .method(Method.POST)
        .data("weight", req.getWeight())
        .data("lborkg", req.getWeightMetric())
        .data("height", req.getHeight())
        .data("cmorin", req.getHeightMetric())
        .data("sex", req.getGender())
        .data("dob", req.getDob())
        .data("dov", req.getOfficeVisitDate())
        .followRedirects(true)
        .execute();
      
      document = res.parse();
    } catch (IOException io) {
      System.out.println("Error: " + io.getMessage());
      return null;
    }

    // Parse the information
    return parseBmiInfo(document);
  }

  public BmiInfoResponse parseBmiInfo(Document doc) {
    Elements headline = doc.select("table#form"); // <table>
    Element table = headline.get(0); // <table>
    Elements tbody = table.children(); // <tbody>
    Elements info = tbody.get(0).children(); // List of <tr>s
    
    // System.out.println("BMI Info: " + info);

    BmiInfoResponse res = new BmiInfoResponse();
    // Weight
    res.setWeight(info.get(0).child(1).text());
    // Height
    res.setHeight(info.get(1).child(1).text());
    // Gender
    res.setGender(info.get(2).child(1).text());
    // Date of Birth
    res.setDob(info.get(3).child(1).text());
    // Date of Office Visit
    res.setOfficeVisitDate(info.get(4).child(1).text());
    // Age in Months of Visit
    res.setAgeInMonthsAtTimeOfVisit(info.get(5).child(1).text());
    // BMI
    res.setBmi(info.get(6).child(1).text());
    // Z-Score
    res.setZScore(info.get(7).child(1).text());
    // Percentile
    res.setPercentile(info.get(8).child(1).text());

    return res;
  }
}