package com.example.filedemo.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.example.filedemo.helpers.Utilities;
import com.example.filedemo.internal.PatientInfo;
import com.example.filedemo.payload.BasicResponse;
import com.example.filedemo.payload.RequiredVariablesResponse;
import com.example.filedemo.payload.UploadFileResponse;
import com.example.filedemo.request.AcsVariablesRequest;
import com.example.filedemo.request.BmiInfoRequest;
import com.example.filedemo.request.ChosenVariablesRequest;
import com.example.filedemo.request.SsdiRequest;
import com.example.filedemo.response.acs.config.Variables;
import com.example.filedemo.response.jewishgen.SsdiObject;
import com.example.filedemo.response.zscore.BmiInfoResponse;
import com.example.filedemo.service.AcsApiService;
import com.example.filedemo.service.BmiService;
import com.example.filedemo.service.FileStorageService;
import com.example.filedemo.service.SsdiService;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@RestController
public class FileController {

  private static final Logger logger = LoggerFactory.getLogger(FileController.class);
  private String filename;
  MultipartFile file;

  private ChosenVariablesRequest chosenVariablesRequest = null;

  @Autowired
  private FileStorageService fileStorageService;

  @Autowired
  private AcsApiService acsApiService;

  @Autowired
  private SsdiService ssdiService;

  @Autowired
  private BmiService bmiService;

  @GetMapping("/testing")
  public BasicResponse testing() {
    // MM/DD/YYYY
    // String dob = "03/09/1990";

    // Map<String, String> x = Utilities.parseDOB(dob);
    // System.out.println("Year: " + x.get("Year"));
    // System.out.println("Month: " + x.get("Month"));
    // System.out.println("Day: " + x.get("Day"));

    // String dob2 = "11/21/2001";
    // x = Utilities.parseDOB(dob2);
    // System.out.println("Year: " + x.get("Year"));
    // System.out.println("Month: " + x.get("Month"));
    // System.out.println("Day: " + x.get("Day"));

    return new BasicResponse(201, "Success", "Hit /testing endpoint");
  }

  @PostMapping("/getBmiInfo")
  public BmiInfoResponse getBmiInfo(@RequestBody BmiInfoRequest req) {
    BmiInfoResponse res = bmiService.getBmiInfo(req);

    return res;
  }

  // @PostMapping("/getSsdiInfo")
  // public List<SsdiObject> getSsdiInfo(@RequestBody SsdiRequest req) {
  //   List<SsdiObject> response = ssdiService.getRecordsByName(req.getFirstName(), req.getLastName());

  //   return response;
  // }

  /* 
    POST Endpoint for User to choose variables he/she wants for his/her CSV File. 
    Response will be a list of variables user will need to include in his/her initial 
    CSV File that he/she uploads. (TEST THIS)
  */
  @PostMapping("/chooseVariables")
  public RequiredVariablesResponse chooseVariables(@RequestBody ChosenVariablesRequest req) {
    if (req == null) {
      return new RequiredVariablesResponse(501, "Internal Server Error", "Request is null", null);
    }

    // Save the variables that user requested, so that we can use it later when the user uploads file
    this.chosenVariablesRequest = req; // Need deep copy?

    Set<String> requiredVars = new HashSet<String>();
    requiredVars.add("Unique ID (0, 1, 2, 3...)");

    // Analyze the variables that the user requested, and return a response of corresponding information that the user needs to put in his/her CSV file

    // Check whether User wants ACS Params
    if (!chosenVariablesRequest.isDetailedVariablesEmpty() || !chosenVariablesRequest.isSubjectVariablesEmpty()) {
      requiredVars.add("Address");
      requiredVars.add("City");
      requiredVars.add("State (Abbreviation Format)");
      requiredVars.add("Zip Code");
    }
    // Check whether User wants SSDI Params
    if (chosenVariablesRequest.isRequestedSsdiInfo()) {
      requiredVars.add("First Name");
      requiredVars.add("Middle Initial");
      requiredVars.add("Last Name");
      requiredVars.add("Date of Birth (MM/DD/YYYY)");
    }
    // Check whether User wants BMI Params
    if (chosenVariablesRequest.isRequestedBmiInfo()) {
      requiredVars.add("Height (cm)");
      requiredVars.add("Weight (kg)");
      requiredVars.add("Gender ('male' or 'female')");
      requiredVars.add("Date of Birth (MM/DD/YYYY)");
      requiredVars.add("Date of Measurement");
    }

    Object[] requiredVarsArr = requiredVars.toArray();
    String[] stringArray = Arrays.copyOf(requiredVarsArr, requiredVarsArr.length, String[].class);

    RequiredVariablesResponse requiredVariablesResponse = new RequiredVariablesResponse(201, "Success", "User will need to have the following variables in the CSV File that they want to upload, in the format mentioned in the parentheses (if applicable) for each variable.", stringArray);

    return requiredVariablesResponse;
  }

  @PostMapping("/uploadFile")
  public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
    
    // TODO: Possibly should validate File

    String fileName = fileStorageService.storeFile(file);
    this.file = file;
    filename = fileName;

    String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/").path(fileName)
        .toUriString();

    // TODO: Need to create the 2nd CSV file that only has address, city, state and zipcode so that we can get census tract from user. Store in fileStorageService as well I guess? After doing so, you can comment out below.

    // // Get Census Tract Information (Max 10,000 Entries)
    // Resource resource = fileStorageService.loadFileAsResource(addressFileName);
    // File addressFile;
    // try {
    //   addressFile = resource.getFile();
    // } catch (IOException e) {
    //   e.printStackTrace();
    //   return new UploadFileResponse("Internal Server Error", "Error in loading CSV file needed to get census tract info", "text/csv", 0);
    // }

    // byte[] updatedAddressFile = acsApiService.getCensusTracts(addressFile);

    Resource resource = fileStorageService.loadFileAsResource(fileName);
    File addressFile;
    try {
      addressFile = resource.getFile();
    } catch (IOException e) {
      e.printStackTrace();
      return new UploadFileResponse("Internal Server Error", "Error in loading CSV file needed to get census tract info", "text/csv", 0);
    }
    acsApiService.getCensusTracts(addressFile);

    // TODO: We will receive the Census Tract Info as []byte. Need to convert it to a CSV File, then parse and append the Census Tract Information (State, County and Tract) to the user-uploaded CSV File. 

    return new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
  }

  @PostMapping("/returnDownloadFile")
  public UploadFileResponse returnDownloadFile() {

    // Now the user wants to download the file with the variables he/she has chosen previously. Given that user has uploaded the (correct) file, we need to make the corresponding requests to get the data.

    // TODO: From the CSV File (With Updated Census Information), need to parse it and get a List<PatientInfo> listOfPatients that will be used to process the requests from all data sources (See PatientInfo.java). Basically, PatientInfo object will have all the initial information (E.g. First Name, Last Name, etc.) provided by the user, and then appended information (E.g. ACS Variables, SSDI Info, etc.) after getting them from the respective services.

    // ACS Request (NEED TO TEST)
    // List<PatientInfo> results = acsApiService.makeAcsGetRequest(chosenVariablesRequest.getListOfDetailedVariables(), chosenVariablesRequest.getListOfSubjectVariables(), listOfPatients);

    // SSDI Request (NEED TO TEST)
    // results = ssdiService.getSsdiRecords(listOfPatients);

    String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/").path(filename)
        .toUriString();

    return new UploadFileResponse(filename, fileDownloadUri, file.getContentType(), file.getSize());
  }

  // take current user inputted csv and converts it into an ArrayList<String>
  public ArrayList<String> csvtoList(Resource resource) {
    ArrayList<String> records = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(resource.getFile()))) {
      String line;
      while ((line = br.readLine()) != null) {
        records.add(line);
      }
      
    } catch (FileNotFoundException fne) {
      System.out.println(fne);

    } catch (IOException e) {
      System.out.println(e);
    }

    return records;
  }

  public void populateCSV(Resource resource, ArrayList<String> parameters,
      Map<String, List<ImmutablePair<String, String>>> results, ArrayList<String> content) {
    // write the results data to csv - just write it to file for now
    try {
      FileWriter writer = new FileWriter(resource.getFile(), false);

      // add new parameters - define column headers here
      writer.append("State");
      writer.append(",");

      // for all parameters, add a column for each
      if (!results.isEmpty()) {
        List<ImmutablePair<String, String>> column = (List<ImmutablePair<String, String>>) results.values().toArray()[0];
        for (ImmutablePair<String, String> p : column ) {
          writer.append(p.left);
          writer.append(",");
        }
      }

      writer.append("\n");

      // combine user inputted content with API results
      ArrayList<ArrayList<String>> csv = new ArrayList<>();

      for (Map.Entry<String, List<ImmutablePair<String, String>>> entry : results.entrySet()) {
        ArrayList<String> line = new ArrayList<>();

        if (content.contains(entry.getKey())) {
          line.add(entry.getKey());  //adds the state 
          for (ImmutablePair<String,String> pair : entry.getValue()) { //adds all the parameter values
              line.add(pair.right); 
          }
          csv.add(line);
        }
      }

      for (ArrayList<String> al : csv) {
        writer.append(String.join(",", al));
        writer.write(System.getProperty("line.separator"));
      }

      writer.flush();
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // ACS specific - split parameters into detailed and subject
  public AcsVariablesRequest splitACSVars(ArrayList<String> parameters) {
    AcsVariablesRequest req = new AcsVariablesRequest();
    ArrayList<String> listOfDetailedVariables = new ArrayList<>();
    ArrayList<String> listOfSubjectVariables = new ArrayList<>();

    //if parameter starts with S it is a Subject query
    for (String parameter : parameters) {
      String varId = Variables.VAR_ID_BY_NAME.get(parameter);
      if (varId.charAt(0) == 'S') {
        listOfSubjectVariables.add(parameter);
      } else {
        listOfDetailedVariables.add(parameter);
      }
    }

    req.setListOfDetailedVariables(listOfDetailedVariables.toArray(new String[listOfDetailedVariables.size()]));
    req.setListOfSubjectVariables(listOfSubjectVariables.toArray(new String[listOfSubjectVariables.size()]));

    return req;
  }

  // process acs database queries
  // public void acs(Resource resource, ArrayList<String> parameters, ArrayList<String> content) {

  //   AcsVariablesRequest req = splitACSVars(parameters);

  //   Map<String, List<ImmutablePair<String, String>>> results = acsApiService.makeAcsGetRequest(req);

  //   populateCSV(resource, parameters, results, content);
  // }

  @GetMapping("/downloadFile/{fileName:.+}")
  public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
    // Load file as Resource
    Resource resource = fileStorageService.loadFileAsResource(fileName);

    // Try to determine file's content type
    String contentType = null;
    try {
      contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
    } catch (IOException ex) {
      logger.info("Could not determine file type.");
    }

    // Fallback to the default content type if type could not be determined
    if (contentType == null) {
      contentType = "application/octet-stream";
    }

    ArrayList<String> content = csvtoList(resource);

    // append acs data to user's file - using example query first
    ArrayList<String> parameters = new ArrayList<>();
    parameters.add("GINI Index of Income Inequality Households");
    parameters.add("Median Gross Rent as a % of Household Income - Renter-Occupied Households paying cash rent");
    parameters.add("Age Dependency Ratio");

    // acs(resource, parameters, content);

    return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
        .body(resource);
  }

}
