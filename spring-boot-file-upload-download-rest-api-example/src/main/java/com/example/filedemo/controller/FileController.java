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

import com.example.filedemo.internal.PatientInfo;
import com.example.filedemo.payload.BasicResponse;
import com.example.filedemo.payload.RequiredVariablesResponse;
import com.example.filedemo.payload.UploadFileResponse;
import com.example.filedemo.request.ChosenVariablesRequest;
import com.example.filedemo.service.AcsApiService;
import com.example.filedemo.service.BmiService;
import com.example.filedemo.service.FileStorageService;
import com.example.filedemo.service.SsdiService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
  Resource resource;

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
    this.chosenVariablesRequest = req;

    Set<String> requiredVars = new HashSet<String>();
    requiredVars.add("Unique ID");

    // Analyze the variables that the user requested, and return a response of corresponding information that the user needs to put in his/her CSV file
    // Check whether User wants ACS Params
    if (!chosenVariablesRequest.isDetailedVariablesEmpty() || !chosenVariablesRequest.isSubjectVariablesEmpty()) {
      requiredVars.add("Address");
      requiredVars.add("City");
      requiredVars.add("State");
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
      requiredVars.add("Height(cm)");
      requiredVars.add("Weight(kg)");
      requiredVars.add("Gender ('male' or 'female')");
      requiredVars.add("Date of Birth (MM/DD/YYYY)");
      requiredVars.add("Date of Measurement");
    }

    // System.out.println(chosenVariablesRequest.isRequestedBmiInfo());
    // System.out.println(chosenVariablesRequest.isRequestedSsdiInfo());
    // System.out.println(chosenVariablesRequest.isDetailedVariablesEmpty());
    // System.out.println(chosenVariablesRequest.isSubjectVariablesEmpty());

    Object[] requiredVarsArr = requiredVars.toArray();
    String[] stringArray = Arrays.copyOf(requiredVarsArr, requiredVarsArr.length, String[].class);

    RequiredVariablesResponse requiredVariablesResponse = new RequiredVariablesResponse(201, "Success", "User will need to have the following variables in the CSV File that they want to upload, in the format mentioned in the parentheses (if applicable) for each variable.", stringArray);

    return requiredVariablesResponse;
  }

  //converts user inputed MultipartFile into a File type
  public static File convertToFile(MultipartFile file) throws IOException {
    File convFile = new File(file.getOriginalFilename());
    convFile.createNewFile();
    FileOutputStream fos = new FileOutputStream(convFile);
    fos.write(file.getBytes());
    fos.close();
    return convFile;
  }

  //get the first row (titles) and check if it has all the required variables
  public String inputFileValidation(@RequestParam("file") File file, String[] requiredVariables) {
      try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        String first;
        if ((first = br.readLine()) != null) {
          List<String> titles = Arrays.asList(first.split(","));
          for (int i = 0; i < titles.size(); i++) {
            titles.set(i, titles.get(i).trim());
          }

          for (String s : requiredVariables) {
            if (!titles.contains(s)) {
              return s;
            }
          }
        }
      
    } catch (IOException ioe) {
        ioe.printStackTrace();
        System.out.println("uploadFile error");

    }
    return null;
  }
 

  @PostMapping("/uploadFile")
  public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
     if (file.isEmpty()) {
      return new UploadFileResponse("Internal Server Error", "Empty file - Please provide us with a properly-formatted, populated CSV File for us to work with.", "text/csv", 0);
     }

     File f = null; 
     try {
        f = convertToFile(file);
     } catch (IOException e) {
        System.out.println(e);
     }

     // User must make Headers EXACTLY the same as below
     String[] acsVars = {"Unique ID", "Address", "Zip Code", "State", "City"};
     String[] ssdiVars = {"Unique ID", "First Name", "Last Name", "Date of Birth"};
     String[] bmiVars = {"Unique ID", "Height(cm)", "Weight(kg)", "Gender", "Date of Birth", "Date of Measurement"};

    // String[] vars = null;
    if (!chosenVariablesRequest.isDetailedVariablesEmpty() || !chosenVariablesRequest.isSubjectVariablesEmpty()) {
      // vars = vars1;
      String missingHeader = inputFileValidation(f, acsVars);
      if (missingHeader != null) {
        return new UploadFileResponse("Internal Server Error", "You are missing an ACS variable: " + missingHeader + ". Make sure you include all the necessary columns in your file.", "text/csv", 0);
      }
    }

    if (chosenVariablesRequest.isRequestedSsdiInfo()) {
      // vars = vars1;
      String missingHeader = inputFileValidation(f, ssdiVars);
      if (missingHeader != null) {
        return new UploadFileResponse("Internal Server Error", "You are missing an SSDI variable: " + missingHeader + ". Make sure you include all the necessary columns in your file.", "text/csv", 0);
      }
    }

    if (chosenVariablesRequest.isRequestedBmiInfo()) {
      // vars = vars1;
      String missingHeader = inputFileValidation(f, bmiVars);
      if (missingHeader != null) {
        return new UploadFileResponse("Internal Server Error", "You are missing a BMI variable: " + missingHeader + ". Make sure you include all the necessary columns in your file.", "text/csv", 0);
      }
    }

    String fileName = fileStorageService.storeFile(file);
    this.file = file;
    filename = fileName;

    String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/").path(fileName)
        .toUriString();
    
    if (!chosenVariablesRequest.isDetailedVariablesEmpty() || !chosenVariablesRequest.isSubjectVariablesEmpty()) {
      // Procedure for Getting Census Tract Info
      try {
        Path path = Paths.get("..\\temp.csv");
        // Delete file if it exists
        if (path.toFile().exists()) {
          Files.delete(path);
        }
        
        //if ACS, create temp file with only the inputted address, city, state and zipcode
        File tempFile = null;   
        try {  
          Path p = Files.createFile(path);   
          tempFile = p.toFile(); 
        }   
        catch (IOException e)   {  
          e.printStackTrace();  
        }  
        
        populateTempCSV(tempFile, f, Arrays.asList(acsVars));

        //updatedAddressFile array has the census track info 
        byte[] updatedAddressFile = acsApiService.getCensusTracts(tempFile);

        Path censusPath = Paths.get("..\\censusTract.csv");
        if (censusPath.toFile().exists()) {
          Files.delete(censusPath);
        }
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
        resource = fileStorageService.loadFileAsResource(f.getAbsolutePath());

        //parse censusTract.csv then combine/append that file to input file
        ArrayList<String> censusFileList = csvtoList(censusResource); // **strings seperated by "" like: "1","4600 Silver Hill Rd, Suitland, MD, 20746","Match","Exact","4600 Silver Hill Rd, SUITLAND, MD, 20746","-76.92691,38.846542","613199520","L","24","033","802405","1084"
        ArrayList<String> inputFileList = csvtoList(resource);

        // census: "1","4600 Silver Hill Rd, Suitland, MD, 20746","Match","Exact","4600 Silver Hill Rd, SUITLAND, MD, 20746","-76.92691,38.846542","613199520","L","24","033","802405","1084"
        // -------------
        // input: Unique ID, Address, City, State, Zip Code
        // 1, 4600 Silver Hill Rd, Suitland, MD, 20746

        inputFileList.set(0, inputFileList.get(0)+", tract");

        //combines input file with census tract file
        for (int i = 1; i < inputFileList.size(); i++) {
          String census = censusFileList.get(i-1);
          String[] sarr = census.split(","); 
          //if no match for census tract, make tract null
          String tract = "";
          boolean contains = !census.contains("No_Match");
          if (contains) {
            //grab the census tract - last 3 like "06","067","001101"
            for (int j = sarr.length - 4; j < sarr.length-1; j++) {
              String trim = sarr[j].substring(1,sarr[j].length()-1); //removes quotes
              tract += trim;
            }
          
          } else {
            tract = "no match";
          }
            String s1 = inputFileList.get(i) + ", " + tract;
            //System.out.println("s1 " + s1);
            inputFileList.set(i, s1);
        }

        FileWriter writer = new FileWriter(resource.getFile(), false);
        for (String line : inputFileList) {
          writer.append(line);
          writer.write(System.getProperty("line.separator"));
        }
        
        writer.flush();
        writer.close();


      } catch(Exception e) {
        e.printStackTrace();
        return new UploadFileResponse("Internal Server Error", "Error in loading CSV file needed to get Census Tract Info - check your address formatting.", "text/csv", 0);

      }
    } else {
      Path censusPath = Paths.get("..\\censusTract.csv");
      try {
        if (censusPath.toFile().exists()) {
          Files.delete(censusPath);
        }
        File censusFile = null;   
        Path p = Files.createFile(censusPath);   
        censusFile = p.toFile(); 
        Resource censusResource = fileStorageService.loadFileAsResource(censusFile.getAbsolutePath());
        resource = fileStorageService.loadFileAsResource(f.getAbsolutePath());
      } catch (IOException e)   {  
        e.printStackTrace();  
      }
    }

    return new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
  }

  @PostMapping("/returnDownloadFile")
  public UploadFileResponse returnDownloadFile() {

    String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/").path(filename)
        .toUriString();

    return new UploadFileResponse(filename, fileDownloadUri, file.getContentType(), file.getSize());
  }

  //uses the file as a map form and make it into a list of patient info
  public List<PatientInfo> makeListOfPatients (Map<String, List<String>> map, int num) {
    
    List<PatientInfo> patients = new ArrayList<>();
    
    for (int i = 0; i < num; i++) {
      PatientInfo p = new PatientInfo();
      if (map.containsKey("tract")) {
        String tract = map.get("tract").get(i); //census tract is 11 digits: first 2 digits = state, next 3 = county, next 6 is tract
        if (!tract.equals("no match")) {
          p.setState(tract.substring(0,2));
          p.setCounty(tract.substring(2,5));
          p.setTract(tract.substring(5,11));
        } else {
          p.setState(null);
          p.setCounty(null);
          p.setTract(null);
        }
      }
      //ssdi
      if (map.containsKey("first name")) {
        p.setFirstName(map.get("first name").get(i));
      }
      if (map.containsKey("last name")) {
        p.setLastName(map.get("last name").get(i));
      }
     
      //bmi
      if (map.containsKey("weight(kg)")) {
        p.setWeight(map.get("weight(kg)").get(i));
      }
      if (map.containsKey("height(cm)")) {
        p.setHeight(map.get("height(cm)").get(i));
      }
      if (map.containsKey("gender")) {
        p.setGender(map.get("gender").get(i));
      }
      if (map.containsKey("date of measurement")) {
        p.setDateOfMeasurement(map.get("date of measurement").get(i));
      }
      if (map.containsKey("date of birth")) {
        p.setDob(map.get("date of birth").get(i));
      }
      patients.add(p);
    }
    return patients;
  }

  //converts given file into a map of <column header, list of matching value entries>
  public Map<String, List<String>> csvToMap (File input) {
    Map<String, List<String>> map = new LinkedHashMap<>();
    try (BufferedReader br = new BufferedReader(new FileReader(input))) {
      List<List<String>> lines = new ArrayList<>();
      String thisLine;
      while ((thisLine = br.readLine()) != null) {
        if (!thisLine.isEmpty()) {
          lines.add(Arrays.asList(thisLine.toLowerCase().split(", ")));
        }
      }

      //for number of columns (lines.get(0) size) parse into map
      for (int i = 0; i < lines.get(0).size(); i++) {
        List<String> list = new ArrayList<>();
        for (int j = 1; j < lines.size(); j++) {
          list.add(lines.get(j).get(i));
        }
        map.put(lines.get(0).get(i), list);
      }

    } catch (FileNotFoundException fne) {
      System.out.println(fne);

    } catch (IOException e) {
      System.out.println(e);
    }
    return map;
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

  public void populateCSV(Resource resource, ArrayList<String> content, List<PatientInfo> listOfPatients) {
    try {
      FileWriter writer = new FileWriter(resource.getFile(), false);

      // add new parameters - define column headers here
      ArrayList<String> parameters = new ArrayList<>();
      if (listOfPatients != null) {
        if (!listOfPatients.isEmpty()) {
          if (listOfPatients.get(0) != null) {
            if (listOfPatients.get(0).getVarValByVarName() != null) {
              for (String s : listOfPatients.get(0).getVarValByVarName().keySet()) {
                parameters.add(s);
              }
            }
          }
        }
      }
      if (listOfPatients.get(0).getBmi()!= null && listOfPatients.get(0).getZScore()!= null && listOfPatients.get(0).getPercentile()!= null) {      
        parameters.add("bmi");
        parameters.add("zscore");
        parameters.add("percentile");
      }
      if (listOfPatients.get(0).getVitalInformation() != null && listOfPatients.get(0).getDateOfDeath() != null) {
        parameters.add("vital information");
        parameters.add("date of death");
      }
      String headers = content.get(0) + "," + String.join(",", parameters);
      writer.append(headers);
      writer.write(System.getProperty("line.separator")); //write new line

      //acs gives: ca varValByVarName = {Age Dependency Ratio=-888888888, Median Gross Rent as a % of Household Income - Renter-Occupied Households paying cash rent=37.6, GINI Index of Income Inequality Households=0.6135}

      // combine user inputted content with API results
      ArrayList<ArrayList<String>> csv = new ArrayList<>();
      for (int i = 0; i < listOfPatients.size(); i++) {
        ArrayList<String> line = new ArrayList<>();
        //acs
        if (listOfPatients.get(i).getVarValByVarName() != null) {
          for (Map.Entry<String, String> entry : listOfPatients.get(i).getVarValByVarName().entrySet()) {
            line.add(entry.getValue());
          }
        }
        //bmi
        if (listOfPatients.get(i).getBmi()!= null && listOfPatients.get(i).getZScore()!= null && listOfPatients.get(i).getPercentile()!= null) {
          line.add(listOfPatients.get(i).getBmi());
          line.add(listOfPatients.get(i).getZScore());
          line.add(listOfPatients.get(i).getPercentile());   
        }
        //ssdi
        if (listOfPatients.get(i).getVitalInformation() != null && listOfPatients.get(i).getDateOfDeath() != null) {
          line.add(listOfPatients.get(i).getVitalInformation());
          line.add(listOfPatients.get(i).getDateOfDeath());          
        }

        csv.add(line);
      }

      //combines generated data with previous input content
      int i = 1; //iterator at location i on original csv list
      for (ArrayList<String> al : csv) {
        writer.append(content.get(i) + "," +String.join(",", al));
        writer.write(System.getProperty("line.separator")); //new line
        i++;
      }

      writer.flush();
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @GetMapping("/downloadFile/{fileName:.+}")
  public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
    //converts user inputted file to an arraylist of strings for each line in file
    ArrayList<String> content = csvtoList(resource);

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

    // Now the user wants to download the file with the variables he/she has chosen previously. 
    //Given that user has uploaded the (correct) file, we need to make the corresponding requests to get the data.

    // PatientInfo object will have all the initial information (E.g. First Name, Last Name, etc.) provided by the user, and then appended information 
    //(E.g. ACS Variables, SSDI Info, etc.) after getting them from the respective services below.

    List<PatientInfo> listOfPatients = new ArrayList<>();

    try {
      Map<String, List<String>> inputMap = csvToMap(resource.getFile());
      listOfPatients = makeListOfPatients(inputMap, content.size()-1);

    } catch (FileNotFoundException fne) {
      System.out.println(fne.getMessage());
    } catch (IOException io) {
      System.out.println(io.getMessage());
    } catch (Exception e) {
      System.out.println("Error: " + e.getLocalizedMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    String[] detailed = this.chosenVariablesRequest.getListOfDetailedVariables();
    String[] subject = this.chosenVariablesRequest.getListOfSubjectVariables();

    // ACS Request 
    if (!chosenVariablesRequest.isDetailedVariablesEmpty() || !chosenVariablesRequest.isSubjectVariablesEmpty()) {
      listOfPatients = acsApiService.makeAcsGetRequest(detailed, subject, listOfPatients);
    }    
    // SSDI Request
    if (chosenVariablesRequest.isRequestedSsdiInfo()) {
      listOfPatients = ssdiService.getSsdiRecords(listOfPatients);
    }

    // BMI Request
    if (chosenVariablesRequest.isRequestedBmiInfo()) {
      listOfPatients = bmiService.getBmiInfo(listOfPatients);
    }

    if (resource == null) {
      System.out.println("WTFFFFFFF");
    }
    if (content == null) {
      System.out.println("FUCK YOU");
    }

    populateCSV(resource, content, listOfPatients); 

    return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
        .body(resource);
  }
}