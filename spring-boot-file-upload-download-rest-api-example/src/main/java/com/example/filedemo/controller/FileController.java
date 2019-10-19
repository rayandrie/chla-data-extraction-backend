package com.example.filedemo.controller;

import com.example.filedemo.payload.BasicResponse;
import com.example.filedemo.payload.UploadFileResponse;
import com.example.filedemo.request.AcsVariablesRequest;
import com.example.filedemo.request.SsdiRequest;
import com.example.filedemo.response.jewishgen.SsdiObject;
import com.example.filedemo.service.AcsApiService;
import com.example.filedemo.service.FileStorageService;
import com.example.filedemo.service.SsdiService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.apache.commons.lang3.tuple.ImmutablePair;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Paths;;

@RestController
public class FileController {

  private static final Logger logger = LoggerFactory.getLogger(FileController.class);
  private String filename;
  MultipartFile file;

  @Autowired
  private FileStorageService fileStorageService;

  @Autowired
  private AcsApiService acsApiService;

  @Autowired
  private SsdiService ssdiService;

  @GetMapping("/testing")
  public BasicResponse testing() {
    return new BasicResponse(201, "Success", "Hit /testing endpoint");
  }

  @PostMapping("/getSsdiInfo")
  public List<SsdiObject> getSsdiInfo(@RequestBody SsdiRequest req) {
    List<SsdiObject> response = ssdiService.getRecordsByName(req.getFirstName(), req.getLastName());

    return response;
  }

  @GetMapping("/getAcsCityStateInfo")
  public BasicResponse getAcsCityStateInfo() {
    acsApiService.initAcsInfo();

    return new BasicResponse(201, "Success", "Received all City State Information");
  }

  @PostMapping("/appendACSVariables")
  public BasicResponse appendACSVariables(@RequestBody AcsVariablesRequest req) {
    // Make a request to the ACS API if there are variables to be searched for
    if (req.detailedVariablesIsEmpty() && req.subjectVariablesIsEmpty()) {
      return new BasicResponse(404, "Failure", "Variable Lists empty, no Request can be made.");
    }

    return new BasicResponse(201, "Success", "Successfully made ACS Request.");
  }

  @PostMapping("/uploadFile")
  public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
    String fileName = fileStorageService.storeFile(file);
    this.file = file;
    filename = fileName;

    String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/").path(fileName)
        .toUriString();

    return new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
  }

  @PostMapping("/returnDownloadFile")
  public UploadFileResponse returnDownloadFile() {

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
      for (String parameter : parameters) {
        writer.append(parameter);
        writer.append(",");
      }

      writer.append("\n");

      // combine user inputted content with API results
      ArrayList<ArrayList<String>> csv = new ArrayList<>();

      for (Map.Entry<String, List<ImmutablePair<String, String>>> entry : results.entrySet()) {
        ArrayList<String> line = new ArrayList<>();

        if (content.contains(entry.getKey())) {
          line.add(entry.getKey());
          line.add(entry.getValue().get(0).right); // this only gets one parameter?
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

    // TOOD for each parameter check if in detailed or subject

    AcsVariablesRequest req = new AcsVariablesRequest();
    Object[] parametersArray = parameters.toArray();
    String[] listOfDetailedVariables = Arrays.copyOf(parametersArray, parametersArray.length, String[].class);
    req.setListOfDetailedVariables(listOfDetailedVariables);

    // set subject list to empty bc it is a detailed query - will need to
    // differentiate later
    String[] listOfSubjectVariables = {};
    req.setListOfSubjectVariables(listOfSubjectVariables);

    return req;
  }

  // process acs database queries
  public void acs(Resource resource, ArrayList<String> parameters, ArrayList<String> content) {

    AcsVariablesRequest req = splitACSVars(parameters);

    Map<String, List<ImmutablePair<String, String>>> results = acsApiService.makeAcsGetRequest(req);

    populateCSV(resource, parameters, results, content);
  }

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

    acs(resource, parameters, content);

    return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
        .body(resource);
  }

}
