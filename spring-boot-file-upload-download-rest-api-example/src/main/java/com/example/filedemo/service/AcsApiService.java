package com.example.filedemo.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.filedemo.request.AcsVariablesRequest;
import com.example.filedemo.response.acs.AcsStateObject;
import com.example.filedemo.response.acs.config.Variables;
import com.example.filedemo.response.acs.wrapper.AcsVariableObject;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AcsApiService {
  // API Key
  private final String ACS_API_KEY = "c28b7e5f2f3f178a55f8a17d91094ca44216fe39";

  // ACS Tables
  private final String SUBJECT_TABLE = "SUBJECT";
  private final String DETAILED_TABLE = "DETAILED";

  // URL Paths
  private final String BASE_URL_ACS_DETAILED = "https://api.census.gov/data/2017/acs/acs5";
  private final String BASE_URL_ACS_SUBJECT = "https://api.census.gov/data/2017/acs/acs5/subject";

  // Getting the State Codes URL
  private final String URL_GET_STATE_INFO = "https://api.census.gov/data/2014/pep/natstprc?get=STNAME,POP&DATE_=7&for=state:*";

  // Getting the Subject Table Information URL
  private final String URL_GET_SUBJECT_VARS_JSON = "https://api.census.gov/data/2017/acs/acs5/subject/variables.json";

  // Getting the Detailed Table Info URL
  private final String URL_GET_DETAILED_VARS_JSON = "https://api.census.gov/data/2017/acs/acs5/variables.json";

  // Service Objects
  private static final Logger logger = LoggerFactory.getLogger(AcsApiService.class);
  private RestTemplate restService;
  private ObjectMapper mapper;

  // ACS Variables
  private Map<String, AcsStateObject> statesByName;

  @Autowired
  public AcsApiService() {
    restService = new RestTemplate();
    mapper = new ObjectMapper();
    statesByName = new HashMap<String, AcsStateObject>();
  }

  // This function should be called on page load on the client-side
  public void initAcsInfo() {
    // Get all city state codes
    // getAllStates();
  }

  // Makes a GET Request to the ACS API to get variable info
  public void makeAcsGetRequest(AcsVariablesRequest req) {
    String subjectQuery = generateAcsQuery(BASE_URL_ACS_SUBJECT, req.getListOfSubjectVariables());
    System.out.println("Subject Query: " + subjectQuery);
    String detailedQuery = generateAcsQuery(BASE_URL_ACS_DETAILED, req.getListOfDetailedVariables());
    System.out.println("Detailed Query: " + detailedQuery);

    Map<String, List<ImmutablePair<String, String> > > allVariablesByState = new HashMap<String, List<ImmutablePair<String, String> > >();

    allVariablesByState = parseAcsResponse(subjectQuery, allVariablesByState);
    allVariablesByState = parseAcsResponse(detailedQuery, allVariablesByState);

    // Now, allVariablesByState has the information the user asked for
    if (allVariablesByState != null) {
      System.out.println(allVariablesByState);
    }
  }

  // Generate the ACS Query
  private String generateAcsQuery(String baseUrl, String[] listOfVariables) {
    if (listOfVariables.length == 0) {
      return "";
    }
    
    // Query Variables
    String getParam = "get=NAME,";
    for (int i = 0; i < listOfVariables.length; i++) {
      String varId = Variables.VAR_ID_BY_NAME.get(listOfVariables[i]);
      if (i == listOfVariables.length - 1) {
        getParam += varId;
      } else {
        getParam += varId + ",";
      }
    }
    // Query State - For now, default is to get info for all states of the US
    String stateParam = "for=state:*";
    
    // Create full query and return
    String fullQuery = baseUrl + "?" + getParam + "&" + stateParam + "&key=" + ACS_API_KEY;

    return fullQuery;
  }

  // Parse the response from the ACS API
  private Map<String, List<ImmutablePair<String, String> > > parseAcsResponse(String query, Map<String, List<ImmutablePair<String, String> > > allVariablesByState) {
    if (query.equals("")) {
      return allVariablesByState;
    }

    String[][] queryResp;
    queryResp = restService.getForObject(
      query,
      String[][].class
    );

    for (int i = 1; i < queryResp.length; i++) {
      // Create a new List of Name-Value Pairs for each State
      List<ImmutablePair<String, String> > varList = new ArrayList<ImmutablePair<String, String> >();

      String[] info = queryResp[0];
      String[] curr = queryResp[i];
      // Add variables corresponding to that State - no need to account for last index, which is just the state code
      for (int j = 1; j < info.length - 1; j++) {
        // Add initial values of empty String for each of the variables
        String varId = info[j];
        String varVal = curr[j];
        String varName = "";
        // Find varName from the Map using varId, and add the var name-value mapping
        for (Map.Entry<String, String> entry : Variables.VAR_ID_BY_NAME.entrySet()) {
          if (entry.getValue().equals(varId)) {
            varName = entry.getKey();
            varList.add(new ImmutablePair<>(varName, varVal));
            break;
          }
        }
      }

      // Add new State if it doesn't exist already
      if (allVariablesByState.containsKey(curr[0])) {
        List<ImmutablePair<String, String> > existingVarList = allVariablesByState.get(curr[0]);
        existingVarList.addAll(varList);
      } else {
        // No State Key present, so just add it
        allVariablesByState.put(curr[0], varList);
      }
    }

    return allVariablesByState;
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

  // Makes an API Call to get the City State Info
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

  // Helper function to get City State Codes
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