package com.example.filedemo.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.example.filedemo.payload.BasicResponse;
import com.example.filedemo.payload.RequiredVariablesResponse;
import com.example.filedemo.payload.UploadFileResponse;
import com.example.filedemo.request.AcsVariablesRequest;
import com.example.filedemo.request.ChosenVariablesRequest;
import com.example.filedemo.response.acs.config.Variables;
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

  /* Testing Endpoint */
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

  // @PostMapping("/testBmiInfo")
  // public BmiInfoResponse getBmiInfo(@RequestBody BmiInfoRequest req) {
  //   BmiInfoResponse res = bmiService.getBmiInfo(req);

  //   return res;
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
    requiredVars.add("Unique ID");

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

  public static File convertToFile(MultipartFile file) throws IOException {
    File convFile = new File(file.getOriginalFilename());
    convFile.createNewFile();
    FileOutputStream fos = new FileOutputStream(convFile);
    fos.write(file.getBytes());
    fos.close();
    return convFile;
  }

  //get the first row (titles) and check if it has all the required variables
  public boolean inputFileValidation(@RequestParam("file") File file, String[] requiredVariables) {
       
      try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        String first;
        if ((first = br.readLine()) != null) {
          List<String> titles = Arrays.asList(first.split(","));
          for (int i = 0; i < titles.size(); i++) {
            titles.set(i, titles.get(i).trim());
          }

          for (String s : requiredVariables) {
            if (!titles.contains(s)) {
              return false;
            }
          }
        }
      
    } catch (IOException ioe) {
        ioe.printStackTrace();
        System.out.println("uploadFile error");

    }
    return true;
  }

  @PostMapping("/uploadFile")
  public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {

     if (file.isEmpty()) {
      return new UploadFileResponse("Internal Server Error", "empty file", "text/csv", 0);
     }

     File f = null; 
     try {
        f = convertToFile(file);
     } catch (IOException e) {
        System.out.println(e);
     }

   // if (!chosenVariablesRequest.isDetailedVariablesEmpty() || !chosenVariablesRequest.isSubjectVariablesEmpty()) {
      String[] vars = {"Unique ID", "Address", "Zip Code", "State (Abbreviation Format)", "City"};
      if (!inputFileValidation(f, vars)) {
        return new UploadFileResponse("Internal Server Error", "missing an ACS variable", "text/csv", 0);
      }
    //}

    /*if (chosenVariablesRequest.isRequestedSsdiInfo()) {
      String[] vars = {"First Name", "Middle Initial", "Last Name", "Date of Birth (MM/DD/YYYY)"};
      if (!inputFileValidation(file, vars)) {
        return new UploadFileResponse("Internal Server Error", "missing a SSDI variable", "text/csv", 0);
      }
    }

    if (chosenVariablesRequest.isRequestedBmiInfo()) {
      String[] vars = {"Height (cm)", "Weight (kg)", "Gender ('male' or 'female')", "Date of Birth (MM/DD/YYYY)", "Date of Measurement"};
      if (!inputFileValidation(file, vars)) {
        return new UploadFileResponse("Internal Server Error", "missing a BMI variable", "text/csv", 0);
      }
    } */

    String fileName = fileStorageService.storeFile(file);
    this.file = file;
    filename = fileName;

    String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/").path(fileName)
        .toUriString();

    // TODO: Need to create the temp CSV file that only has address, city, state and zipcode so that we can get census tract from user. 
    //Can store in fileStorageService as well I guess? 
    //After doing so, can comment out below (Pass this temp CSV File as a File object and call it 'addressFile' so it can be used below).
    
    try {
      Path path = Paths.get("..\\temp.csv");
      File tempFile = null;   
      try   {  
        Path p = Files.createFile(path);   
        tempFile = p.toFile(); 
      }   
      catch (IOException e)   {  
        e.printStackTrace();  
      }  

      //if ACS, create temp file with only the inputted address, city, state and zipcode
      //if (!chosenVariablesRequest.isDetailedVariablesEmpty() || !chosenVariablesRequest.isSubjectVariablesEmpty()) {
        //String[] vars = {"Unique ID", "Address", "Zip Code", "State (Abbreviation Format)", "City"};
     
          populateTempCSV(tempFile, f, Arrays.asList(vars));
     
        //}

      //updatedAddressFile array has the census track info 
     byte[] updatedAddressFile = acsApiService.getCensusTracts(tempFile);

      Path censusPath = Paths.get("..\\censusTract.csv");
      File censusFile = null;   
      try  {  
        Path p = Files.createFile(censusPath);   
        censusFile = p.toFile(); 
      }   
      catch (IOException e)   {  
        e.printStackTrace();  
      }  

      // convert to file
      try (FileOutputStream fos = new FileOutputStream(censusFile)) {
          fos.write(updatedAddressFile);
      } catch (IOException ioe) {
          ioe.printStackTrace();
      }

      Resource censusResource = fileStorageService.loadFileAsResource(censusFile.getAbsolutePath());
      Resource resource = fileStorageService.loadFileAsResource(f.getAbsolutePath());

      //parse censusTract.csv then combine/append that file to input file
      ArrayList<String> censusFileList = csvtoList(censusResource); // **strings delinated by "" like: "1","4600 Silver Hill Rd, Suitland, MD, 20746","Match","Exact","4600 Silver Hill Rd, SUITLAND, MD, 20746","-76.92691,38.846542","613199520","L","24","033","802405","1084"
      ArrayList<String> inputFileList = csvtoList(resource);

      // for (String s : censusFileList) {
      //   System.out.println(s);
      // }
      //   System.out.println("-------------");

      // for (String s : inputFileList) {
      //   System.out.println(s);
      // }


      // "1","4600 Silver Hill Rd, Suitland, MD, 20746","Match","Exact","4600 Silver Hill Rd, SUITLAND, MD, 20746","-76.92691,38.846542","613199520","L","24","033","802405","1084"
      // -------------
      // Unique ID, Address, City, State (Abbreviation Format), Zip Code
      // 1, 4600 Silver Hill Rd, Suitland, MD, 20746

      for (int i = 0; i < inputFileList.size(); i++) {
        String s1 = censusFileList.get(i) + inputFileList.get(i);
        inputFileList.set(i, s1);
      }

      // FileWriter writer = new FileWriter(resource.getFile(), false);
      // for (String line : inputFileList) {
      //   writer.append(line);
      //   writer.write(System.getProperty("line.separator"));
      // }
      
      // writer.flush();
      // writer.close();

    } catch(Exception e) {
      e.printStackTrace();
      return new UploadFileResponse("Internal Server Error", "Error in loading CSV file needed to get census tract info", "text/csv", 0);

    }

    // // Get Census Tract Information (Max 10,000 Entries)
    // Resource resource = fileStorageService.loadFileAsResource(addressFileName);
    // File addressFile;
    // try {
    //   addressFile = resource.getFile();
    // } catch (IOException e) {
    //   e.printStackTrace();
    //   return new UploadFileResponse("Internal Server Error", "Error in loading CSV file needed to get census tract info", "text/csv", 0);
    // }


    // TODO: We will receive the Census Tract Info as []byte. 
    //Need to convert it to a CSV File (File or MultipartFile?), 
    //then parse and append the Census Tract Information (State, County and Tract) to the user-uploaded CSV File. 

    return new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
  }

  @PostMapping("/returnDownloadFile")
  public UploadFileResponse returnDownloadFile() {

    // Now the user wants to download the file with the variables he/she has chosen previously. Given that user has uploaded the (correct) file, we need to make the corresponding requests to get the data.

    // TODO: From the CSV File (With Updated Census Information), need to parse it and get a List<PatientInfo> listOfPatients that will be used to store the data we want from all 
    //data sources (See PatientInfo.java). Basically, PatientInfo object will have all the initial information (E.g. First Name, Last Name, etc.) provided by the user, and then appended information 
    //(E.g. ACS Variables, SSDI Info, etc.) after getting them from the respective services below.

    // ACS Request (NEED TO TEST)
    // listOfPatients = acsApiService.makeAcsGetRequest(chosenVariablesRequest.getListOfDetailedVariables(), chosenVariablesRequest.getListOfSubjectVariables(), listOfPatients);

    // SSDI Request (NEED TO TEST)
    // if (chosenVariablesRequest.isRequestedSsdiInfo()) {
    //   results = ssdiService.getSsdiRecords(listOfPatients);
    // }

    // BMI Request (NEED TO TEST)
    // if (chosenVariablesRequest.isRequestedBmiInfo()) {
    //   results = bmiService.getBmiInfo(listOfPatients);
    // }

    // TODO: After this, listOfPatients variable should have the information it needs to make the CSV File the user wants. Can convert listOfPatients to a CSV File, and then pass it back to the user

    String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/").path(filename)
        .toUriString();

    return new UploadFileResponse(filename, fileDownloadUri, file.getContentType(), file.getSize());
  }

  public Map<String, List<String>> grabCertainColumns(File temp, File input, List<String> vars) {
    Map<String, List<String>> map = new LinkedHashMap<>();
    try (BufferedReader br = new BufferedReader(new FileReader(input))) {
     List<List<String>> lines = new ArrayList<>();
     String thisLine;
     while ((thisLine = br.readLine()) != null) {
       if (!thisLine.isEmpty()) {
         lines.add(Arrays.asList(thisLine.split(", ")));
       }
     }

     //have to remove empty spaces at end
    // List<List<String>> lines1 = new ArrayList<>();
    //  for (List<String> l : lines) {
    //   for (int i = 0; i < l.size(); i++) {
    //     List<String> li = new ArrayList<>();
    //     if (!l.get(i).isEmpty()) {
    //       li.add(l.get(i));
    //     } 
    //     lines1.add(li);
    //   }
    //  }

      //2d array to map - parsing out the vars we don't want
     for (int i = 0; i < lines.get(0).size(); i++) {
        if (vars.contains(lines.get(0).get(i))) {
           List<String> list = new ArrayList<>();
           for (int j = 1; j < lines.size(); j++) {
              list.add(lines.get(j).get(i));
           }
           map.put(lines.get(0).get(i), list);
         }
     }

     System.out.println(map);
      
    } catch (FileNotFoundException fne) {
      System.out.println(fne);

    } catch (IOException e) {
      System.out.println(e);
    }

    return map;
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

  //populates csv with just the necessary columns
  public void populateTempCSV(File temp, File input, List<String> vars) {
    Map<String, List<String>> map = grabCertainColumns(temp, input, vars);
    int numRows = map.get(vars.get(0)).size();

    try {
      FileWriter writer = new FileWriter(temp, false);
     
      //writes the titles (parameters like address, zip code, etc.)
      // for (String s : map.keySet()) {
      //   writer.append(s);
      //   writer.append(",");
      // }
      // writer.write(System.getProperty("line.separator"));
      
      //writes the value/ element i of the list to the current line
      for (int i = 0; i < numRows; i++) {
        for (Map.Entry<String, List<String>> e : map.entrySet()) {
          List<String> values = e.getValue();
          writer.append(values.get(i));
          writer.append(",");
        }
        writer.write(System.getProperty("line.separator"));
      }

      writer.flush();
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void populateCSV(Resource resource, ArrayList<String> parameters,
      Map<String, List<ImmutablePair<String, String>>> results, ArrayList<String> content) {
    try {
      FileWriter writer = new FileWriter(resource.getFile(), false);

      // add new parameters - define column headers here

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
