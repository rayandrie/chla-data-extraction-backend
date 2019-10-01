package com.example.filedemo.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.example.filedemo.response.acs.AcsInternalVariableObject;
import com.example.filedemo.response.acs.AcsStateObject;
import com.example.filedemo.response.acs.config.Variables;
import com.example.filedemo.response.acs.wrapper.AcsVariableObject;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AcsApiService {
  // API Key
  private final String ACS_API_KEY = "c28b7e5f2f3f178a55f8a17d91094ca44216fe39";

  // URL Paths
  private final String BASE_URL_ACS_DETAILED = "https://api.census.gov/data/2017/acs/acs5";

  private final String BASE_URL_ACS_SUBJECT = "https://api.census.gov/data/2017/acs/acs5/subject";

  private final String URL_GET_STATE_INFO = "https://api.census.gov/data/2014/pep/natstprc?get=STNAME,POP&DATE_=7&for=state:*";

  private final String URL_GET_SUBJECT_VARS_JSON = "https://api.census.gov/data/2017/acs/acs5/subject/variables.json";

  private final String URL_GET_DETAILED_VARS_JSON = "https://api.census.gov/data/2017/acs/acs5/variables.json";

  // Service Objects
  private static final Logger logger = LoggerFactory.getLogger(AcsApiService.class);
  private RestTemplate restService;
  private ObjectMapper mapper;

  // ACS Variables
  private Map<String, AcsStateObject> statesByName;
  private Map<String, AcsInternalVariableObject> subjectVariableByName;
  private Map<String, AcsInternalVariableObject> detailedVariableByName;

  @Autowired
  public AcsApiService() {
    restService = new RestTemplate();
    mapper = new ObjectMapper();
    statesByName = new HashMap<String, AcsStateObject>();
    subjectVariableByName = new HashMap<String, AcsInternalVariableObject>();
    detailedVariableByName = new HashMap<String, AcsInternalVariableObject>();
  }

  public void initAcsInfo() {
    // Get all city state codes
    getAllStates();
    // Might not need to do below since we already have final variable mapping 
    // // Set up Variable Map for ACS Subject Table
    // getSubjectTableVariables();
    // // Set up Variable Map for ACS Detailed Table
    // getDetailedTableVariables();
  }

  private void getDetailedTableVariables() {
    Map<String, Map<String, AcsVariableObject>> detailedMap = getVariableMapFromAcs(URL_GET_DETAILED_VARS_JSON);
    Map<String, AcsVariableObject> variableMap = detailedMap.get("variables");
    Map<String, String> detailedVarIdByName = Variables.DETAILED_VAR_ID_BY_NAME;
    for (Map.Entry<String, String> entry : detailedVarIdByName.entrySet()) {
      String varId = entry.getValue();
      AcsVariableObject acsVariable = variableMap.get(varId);
      detailedVariableByName.put(entry.getKey(), new AcsInternalVariableObject(acsVariable, varId));
    }
    // Print Internal Detailed Table Variable Map
    System.out.println("Printing Internal Detailed Table Variable Map...");
    for (Map.Entry<String, AcsInternalVariableObject> entry : detailedVariableByName.entrySet()) {
      System.out.println("Key = " + entry.getKey() 
        + ", Value = " + entry.getValue().toString());
    }
  }

  private void getSubjectTableVariables() {
    Map<String, Map<String, AcsVariableObject>> subjectMap = getVariableMapFromAcs(URL_GET_SUBJECT_VARS_JSON);
    Map<String, AcsVariableObject> variableMap = subjectMap.get("variables");
    Map<String, String> subjectVarIdByName = Variables.SUBJECT_VAR_ID_BY_NAME;
    for (Map.Entry<String, String> entry : subjectVarIdByName.entrySet()) {
      String varId = entry.getValue();
      AcsVariableObject acsVariable = variableMap.get(varId);
      subjectVariableByName.put(entry.getKey(), new AcsInternalVariableObject(acsVariable, varId));
    }
    // Print Internal Subject Table Variable Map
    System.out.println("Printing Internal Subject Table Variable Map...");
    for (Map.Entry<String, AcsInternalVariableObject> entry : subjectVariableByName.entrySet()) {
      System.out.println("Key = " + entry.getKey() 
        + ", Value = " + entry.getValue().toString());
    }
  }

  private Map<String, Map<String, AcsVariableObject>> getVariableMapFromAcs(String url) {
    // Make API Call to ACS
    final String jsonResp = restService.getForObject(url, String.class);

    // Convert JSON String to Map
    Map<String, Map<String, AcsVariableObject>> map = new HashMap<String, Map<String, AcsVariableObject>>();
    try {
      map = mapper.readValue(jsonResp, new TypeReference<Map<String, Map<String, AcsVariableObject>>>(){});
    } catch (JsonGenerationException e) {
      e.printStackTrace();
    } catch (JsonMappingException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }
    // Check output response
    // System.out.println(map.get("variables"));

    return map;
  }

  private void getAllStates() {
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