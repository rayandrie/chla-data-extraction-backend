package com.example.filedemo.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.filedemo.internal.PatientInfo;
import com.example.filedemo.response.acs.config.Variables;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Service
public class AcsApiService {
  // API Key
  private final String ACS_API_KEY = "c28b7e5f2f3f178a55f8a17d91094ca44216fe39";

  // URL Paths
  private final String BASE_URL_ACS_DETAILED = "https://api.census.gov/data/2017/acs/acs5";
  private final String BASE_URL_ACS_SUBJECT = "https://api.census.gov/data/2017/acs/acs5/subject";
  private final String CENSUS_TRACT_POST_URL = "https://geocoding.geo.census.gov/geocoder/geographies/addressbatch";

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

  @Autowired
  public AcsApiService() {
    restService = new RestTemplate();
    mapper = new ObjectMapper();
  }

  // Using the temporary CSV File with only address information, get a new file with the Batch Geocoding Information which include Census Tracts
  public byte[] getCensusTracts(File file) {
    // Make POST Call to get Census Tracts of each address in the CSV File we will send

    List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
    messageConverters.add(new ByteArrayHttpMessageConverter());

    RestTemplate restTemplate = new RestTemplate();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("benchmark", "9");
    body.add("vintage", "910");
    body.add("addressFile", new FileSystemResource(file));

    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String,Object>>(body, headers);

    ResponseEntity<byte[]> response = restTemplate.exchange(CENSUS_TRACT_POST_URL, HttpMethod.POST, requestEntity, byte[].class);

    // // Output Response
    // String bodyResp = new String(response.getBody());
    // System.out.println("File Response:");
    // System.out.println(bodyResp);

    return response.getBody();
  }

  // Makes a GET Request to the ACS API to get variable info
  public List<PatientInfo> makeAcsGetRequest(String[] listOfDetailedVariables, String[] listOfSubjectVariables, List<PatientInfo> listOfPatients) {

    // For all patients from the CSV File, we want to query ACS to get their information
    for (int i = 0; i < listOfPatients.size(); i++) {
      PatientInfo currPatient = listOfPatients.get(i);
      String patientStateParam = currPatient.getState();
      String patientCountyParam = currPatient.getCounty();
      String patientTractParam = currPatient.getTract();

      // We need State, County and Tract to execute the request. If we don't have it, then just skip the patient, meaning we can't get any info for that particular patient
      if (patientStateParam == null || patientCountyParam == null || patientTractParam == null) {
        continue;
      }

      // Generate the query for the particular patient
      String subjectQuery = generateAcsQueryForSinglePatient(BASE_URL_ACS_SUBJECT, listOfSubjectVariables, patientStateParam, patientCountyParam, patientTractParam);
      // System.out.println("Subject Query: " + subjectQuery);
      String detailedQuery = generateAcsQueryForSinglePatient(BASE_URL_ACS_DETAILED, listOfDetailedVariables, patientStateParam, patientCountyParam, patientTractParam);
      // System.out.println("Detailed Query: " + detailedQuery);

      // Create the Map that will be appended to the PatientInfo Variable
      Map<String, String> varValByVarName = new HashMap<String, String>();

      // Parse the Response for both subject and detailed tables, and put them in our Map
      varValByVarName = parseAcsResponseForSinglePatient(subjectQuery, listOfSubjectVariables, varValByVarName);
      varValByVarName = parseAcsResponseForSinglePatient(detailedQuery, listOfDetailedVariables, varValByVarName);

      // Put the map into our PatientInfo Object so that they can be appended to the CSV later
      currPatient.setVarValByVarName(varValByVarName);
    }

    return listOfPatients;
  }

  // Generate the ACS Query for a single patient
  private String generateAcsQueryForSinglePatient(String baseUrl, String[] listOfVariables, String state, String county, String tract) {
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

    // Query by Census Tract
    String tractParam = "for=tract:" + tract + "&" + "in=state:" + state + "+county:" + county;
    
    // Create full query and return
    String fullQuery = baseUrl + "?" + getParam + "&" + tractParam;
    // + "&key=" + ACS_API_KEY;

    //sample query for subject: 
    // https://api.census.gov/data/2017/acs/acs5?get=NAME,B019083_001E&for=tract:001000&in=state:01+county:103
    
    return fullQuery;
  }

  // Parse the response from the ACS API
  private Map<String, String> parseAcsResponseForSinglePatient(String query, String[] listOfVariables, Map<String, String> varValByVarName) {
    // Empty query, do nothing
    if (query.equals("")) {
      return varValByVarName;
    }

    String[][] queryResp;
    try {
      queryResp = restService.getForObject(
        query,
        String[][].class
      );
    } catch (HttpStatusCodeException exception) {
      System.out.println("Status Code Error: " + exception.getRawStatusCode());
      System.out.println("Error Msg: " + exception.getStatusText());
      return null;
    }

    // In each response, we get back data corresponding to that single patient (in total, an array that holds 2 arrays; 1st is array content info, 2nd is actual content). Need to package it in a map so that it can be appended into the CSV file in the future.
    String[] info = queryResp[1];

    // Skip 1st var because it's just NAME info
    // Skip last 3 var because that is just state, county and tract info
    int len = info.length - 3;

    // Initialize the listOfValues array with the values we got back from the response
    String[] listOfValues = new String[listOfVariables.length];
    int j = 0;
    for (int i = 1; i < len; i++) {
      listOfValues[j] = info[i];
      j++;
    }

    // Now, listOfVariables should have the same length and ordering as listOfValues; they should map each other.
    for (int i = 0; i < listOfVariables.length; i++) {
      varValByVarName.put(listOfVariables[i], listOfValues[i]);
    }

    return varValByVarName;
  }

}