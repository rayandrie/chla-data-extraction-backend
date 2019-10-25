package com.example.filedemo.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.filedemo.helpers.Utilities;
import com.example.filedemo.internal.PatientInfo;
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

  // Example URL - https://jewishgen.org/databases/eidb/engine/ssdm.php?numberKind=starts&numberMax=&firstKind=exact&firstMax=Elvis&firstSoundex=&middleKind=starts&middleMax=A&lastKind=exact&lastMax=Presley&lastSoundex=&suffixKind=exact&suffixMax=&bornyearKind=between&bornyearMin=1957&bornyearMax=1957&bornmonthKind=exact&bornmonthMax=05&borndayKind=exact&borndayMax=2&diedyearKind=between&diedyearMin=&diedyearMax=&diedmonthKind=exact&diedmonthMax=&dieddayKind=exact&dieddayMax=&ageKind=between&ageMin=&ageMax=&stateKind=exact&stateMax=&offset=1&pagesize=20&nototals=0&optimize=1&testing=0&engine=jg_myisam

  private Document doc;

  @Autowired
  public SsdiService() {}

  public List<PatientInfo> getSsdiRecords(List<PatientInfo> listOfPatients) {
    // Need to find SSDI info for each patient using First Name, Middle Initial, Last Name and DOB
    for (int i = 0; i < listOfPatients.size(); i++) {
      PatientInfo patient = listOfPatients.get(i);
      Document currDoc = getRecordsWithInfo(patient.getFirstName(), 
        patient.getMiddleInitial(), patient.getLastName(), 
        patient.getDob()
      );

      List<SsdiObject> results = parseSsdiTable(currDoc);
      
      // Put the information into PatientInfo Object
      if (results == null) {
        // Means Patient is not dead. Vital Information should show that patient is 'Alive', and date of death should be null
        patient.setVitalInformation("Alive");
        patient.setDateOfDeath("");
      } else {
        // By default, just choose the first results that matches. Vital Information should show that patient is 'Dead', and date of death should be what we got back
        SsdiObject r = results.get(0);
        patient.setVitalInformation("Dead");
        patient.setDateOfDeath(r.getDateOfDeath());
      }
    }

    return listOfPatients;
  }

  private Document getRecordsWithInfo(String firstName, String middleInitial, String lastName, String dob) {
    
    // Parse DOB
    Map<String, String> dobMap = Utilities.parseDOB(dob);

    try {
      doc = Jsoup.connect("https://jewishgen.org/databases/eidb/engine/ssdm.php?numberKind=starts&numberMax=&firstKind=exact&firstMax=" + firstName + "&firstSoundex=&middleKind=starts&middleMax=" + middleInitial + "&lastKind=exact&lastMax=" + lastName + "&lastSoundex=&suffixKind=exact&suffixMax=&bornyearKind=between&bornyearMin=" + dobMap.get("Year") + "&bornyearMax=" + dobMap.get("Year") + "&bornmonthKind=exact&bornmonthMax=" + dobMap.get("Month") + "&borndayKind=exact&borndayMax=" + dobMap.get("Day") + "&diedyearKind=between&diedyearMin=&diedyearMax=&diedmonthKind=exact&diedmonthMax=&dieddayKind=exact&dieddayMax=&ageKind=between&ageMin=&ageMax=&stateKind=exact&stateMax=&offset=1&pagesize=20&nototals=0&optimize=1&testing=0&engine=jg_myisam").get();
    } catch (IOException e) {
      System.out.println("Error: " + e);
      System.out.println("Unable to connect to URL.");
      return null;
    }

    // System.out.println("Title: " + doc.title());

    return doc;
  }

  private List<SsdiObject> parseSsdiTable(Document doc) {
    List<SsdiObject> ssdiList = new ArrayList<SsdiObject>();
    Elements ssdiEntries = doc.select("tr");

    if (ssdiEntries.size() <= 1) {
      return null;
    }

    for (int i = 1; i < ssdiEntries.size(); i++) {

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