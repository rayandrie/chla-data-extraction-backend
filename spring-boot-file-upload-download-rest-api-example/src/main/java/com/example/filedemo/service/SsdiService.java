package com.example.filedemo.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.filedemo.response.jewishgen.SsdiObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SsdiService {
  // Pulls Data from jewishgen.org - Death Master File (1936 to February 28, 2014)

  // Example URL - https://jewishgen.org/databases/eidb/engine/ssdm.php?numberKind=starts&numberMax=&firstKind=exact&firstMax=Elvis&firstSoundex=&middleKind=starts&middleMax=&lastKind=exact&lastMax=Presley&lastSoundex=&suffixKind=exact&suffixMax=&bornyearKind=between&bornyearMin=&bornyearMax=&bornmonthKind=exact&bornmonthMax=&borndayKind=exact&borndayMax=&diedyearKind=between&diedyearMin=&diedyearMax=&diedmonthKind=exact&diedmonthMax=&dieddayKind=exact&dieddayMax=&ageKind=between&ageMin=&ageMax=&stateKind=exact&stateMax=&offset=1&pagesize=20&nototals=0&optimize=1&testing=0&engine=jg_myisam

  private final String URL_WITH_PARAMS = "https://jewishgen.org/databases/eidb/engine/ssdm.php?numberKind=starts&numberMax=&firstKind=exact&firstMax=&firstSoundex=&middleKind=starts&middleMax=&lastKind=exact&lastMax=&lastSoundex=&suffixKind=exact&suffixMax=&bornyearKind=between&bornyearMin=&bornyearMax=&bornmonthKind=exact&bornmonthMax=&borndayKind=exact&borndayMax=&diedyearKind=between&diedyearMin=&diedyearMax=&diedmonthKind=exact&diedmonthMax=&dieddayKind=exact&dieddayMax=&ageKind=between&ageMin=&ageMax=&stateKind=exact&stateMax=&offset=1&pagesize=20&nototals=0&optimize=1&testing=0&engine=jg_myisam";

  private Document doc;

  @Autowired
  public SsdiService() {}

  public List<SsdiObject> getRecordsByName(String firstName, String lastName) {
    try {
      doc = Jsoup.connect("https://jewishgen.org/databases/eidb/engine/ssdm.php?numberKind=starts&numberMax=&firstKind=exact&firstMax=" + firstName + "&firstSoundex=&middleKind=starts&middleMax=&lastKind=exact&lastMax=" + lastName + "&lastSoundex=&suffixKind=exact&suffixMax=&bornyearKind=between&bornyearMin=&bornyearMax=&bornmonthKind=exact&bornmonthMax=&borndayKind=exact&borndayMax=&diedyearKind=between&diedyearMin=&diedyearMax=&diedmonthKind=exact&diedmonthMax=&dieddayKind=exact&dieddayMax=&ageKind=between&ageMin=&ageMax=&stateKind=exact&stateMax=&offset=1&pagesize=20&nototals=0&optimize=1&testing=0&engine=jg_myisam").get();
    } catch (IOException e) {
      System.out.println("Error: " + e);
      System.out.println("Unable to connect to URL.");
      return null;
    }

    // System.out.println("HERE: " + doc.title());

    // Parse the Database Table
    List<SsdiObject> ssdiList = parseSsdiTable(doc);

    return ssdiList;
  }

  private List<SsdiObject> parseSsdiTable(Document doc) {
    List<SsdiObject> ssdiList = new ArrayList<SsdiObject>();
    Elements ssdiEntries = doc.select("tr");

    for (int i = 0; i < ssdiEntries.size(); i++) {
      if (i == 0) continue; // ignore table header

      Element entry = ssdiEntries.get(i);
      SsdiObject info = parseSsdiEntry(entry);
      ssdiList.add(info);
    }

    return ssdiList;
  }

  private SsdiObject parseSsdiEntry(Element entry) {
    // There are 13 tds in total
    Elements dataSet = entry.children();

    SsdiObject o = new SsdiObject();
    // Name
    o.setName(dataSet.get(2).text());
    // Date of Death
    o.setDateOfDeath(dataSet.get(4).text());
    // Date of Birth
    o.setDateOfBirth(dataSet.get(6).text());
    // Age
    o.setAge(dataSet.get(8).text());
    // SSN
    o.setSocialSecurityNumber(dataSet.get(10).text());
    // State Issued
    o.setStateIssued(dataSet.get(12).text());

    return o;
  }
}