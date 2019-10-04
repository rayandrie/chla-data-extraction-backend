package com.example.filedemo.controller;

import com.example.filedemo.payload.BasicResponse;
import com.example.filedemo.payload.UploadFileResponse;
import com.example.filedemo.request.AcsVariablesRequest;
import com.example.filedemo.service.AcsApiService;
import com.example.filedemo.service.FileStorageService;
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

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileStorageService fileStorageService;
    
    @Autowired
    private AcsApiService acsApiService;

    @GetMapping("/testing")
    public BasicResponse testing() {
      return new BasicResponse(201, "Success", "Hit /testing endpoint");
    }

    @GetMapping("/getAcsCityStateInfo")
    public BasicResponse getAcsCityStateInfo() {
      acsApiService.initAcsInfo();

      return new BasicResponse(201, "Success", "Received all City State Information");
    }

    @PostMapping("/appendACSVariables")
    public BasicResponse appendACSVariables(@RequestBody AcsVariablesRequest req) {
      // Make a request to the ACS API
      System.out.println("HEHROHOURHRURR");
      if (req.detailedVariablesIsEmpty() && req.subjectVariablesIsEmpty()) {
        return new BasicResponse(404, "Failure", "Variable Lists empty, no Request can be made.");
      } else {
        acsApiService.makeAcsGetRequest(req);
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

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

}
