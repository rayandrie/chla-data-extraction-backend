package com.example.filedemo.controller;

import com.example.filedemo.payload.BasicResponse;
import com.example.filedemo.payload.UploadFileResponse;
import com.example.filedemo.request.AcsVariablesRequest;
import com.example.filedemo.request.BmiInfoRequest;
import com.example.filedemo.request.SsdiRequest;
import com.example.filedemo.response.jewishgen.SsdiObject;
import com.example.filedemo.response.zscore.BmiInfoResponse;
import com.example.filedemo.service.AcsApiService;
import com.example.filedemo.service.BmiService;
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

import java.io.FileWriter;
import java.io.IOException;
import java.util.*; 
import java.util.List;

@RestController
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

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
      return new BasicResponse(201, "Success", "Hit /testing endpoint");
    }

    @PostMapping("/getBmiInfo")
    public BmiInfoResponse getBmiInfo(@RequestBody BmiInfoRequest req) {
      BmiInfoResponse res = bmiService.getBmiInfo(req);

      return res;
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

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();

        return new UploadFileResponse(fileName, fileDownloadUri,
                file.getContentType(), file.getSize());
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
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        //append acs data to user's file - using example query first
         String parameter = "GINI Index of Income Inequality Households";  //get from front end later
         AcsVariablesRequest req = new AcsVariablesRequest();
         String[] listOfDetailedVariables = {parameter};
         req.setListOfDetailedVariables(listOfDetailedVariables);

         //set subject list to empty bc it is a detailed query - will need to differentiate later
         String[] listOfSubjectVariables = {};
         req.setListOfSubjectVariables(listOfSubjectVariables);

         Map<String, List<ImmutablePair<String, String> > > results = acsApiService.makeAcsGetRequest(req); 
         if (results != null) {
          System.out.println(results);
         }

         //write the results data to csv - just write it to file for now
         try {
          FileWriter writer = new FileWriter(resource.getFile(), false); 

          //TODO: need to rewrite all the user data back into file
         
          //define column headers here 
          writer.append("State");     //user will already have this inputted
          writer.append(","); 
          writer.append(parameter);
          writer.append(","); 
          writer.append("\n"); 

          //add API info to this column
          for (Map.Entry<String, List<ImmutablePair<String, String>>> entry : results.entrySet()) {
            writer.append(String.join(",", entry.getKey(), entry.getValue().get(0).right));
            writer.write(System.getProperty("line.separator"));
          }

        	writer.flush();
        	writer.close();
         }catch (IOException e) { 
             e.printStackTrace(); 
         } 

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

}
