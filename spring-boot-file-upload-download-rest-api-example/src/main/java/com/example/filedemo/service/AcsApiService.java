package com.example.filedemo.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.filedemo.response.acs.wrapper.AcsStateObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AcsApiService {
  // API Key
  private final String ACS_API_KEY = "c28b7e5f2f3f178a55f8a17d91094ca44216fe39";

  // URI Paths
  private final String URL_GET_STATE_INFO = "https://api.census.gov/data/2014/pep/natstprc?get=STNAME,POP&DATE_=7&for=state:*";

  private static final Logger logger = LoggerFactory.getLogger(AcsApiService.class);
  private RestTemplate restService;
  private Map<String, AcsStateObject> statesByName;

  @Autowired
  public AcsApiService() {
    restService = new RestTemplate();
    statesByName = new HashMap<String, AcsStateObject>();
  }

  public void getAllStates() {
    String[][] cityStatesResp = restService.getForObject(
      URL_GET_STATE_INFO,
      String[][].class
    );
    getCityStatesByStateName(cityStatesResp);
    
    // Print out States Map
    for (Map.Entry<String, AcsStateObject> entry : statesByName.entrySet()) {
      System.out.println("Key = " + entry.getKey() 
        + ", Value = " + entry.getValue().toString());
    }
  }

  private Map<String, AcsStateObject> getCityStatesByStateName(String[][] listStates) {
    if (listStates.length == 0) {
      return null;
    }

    // Parse Response to Map
    for (int i = 0; i < listStates.length; i++) {
      if (i == 0) continue;

      String stateName = listStates[i][0];
      String population = listStates[i][1];
      String date = listStates[i][2];
      String stateCode = listStates[i][3];
      AcsStateObject stateInfo = new AcsStateObject(stateName, population, date, stateCode);
      statesByName.put(stateName, stateInfo);
    }

    return statesByName;
  }

}