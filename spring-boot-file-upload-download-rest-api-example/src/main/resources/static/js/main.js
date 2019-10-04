'use strict';

function onPageLoad() {
  // Get all the State Code Information from ACS
  $.ajax({
    url: '/getAcsCityStateInfo',
    method: 'GET',
    dataType: 'json',
    contentType: "application/json;charset=utf-8",
    success: function(data) {
      console.log("Status: ", data.status);
      console.log("Message: ", data.message);
    },
    error: function(request, error) {
      console.log(error);
    }
  });

  // Sample POST Call
  let data = {
    "listOfDetailedVariables": [
      'Median Gross Rent as a % of Household Income - Renter-Occupied Households paying cash rent',
      'GINI Index of Income Inequality Households'
    ],
    "listOfSubjectVariables": []
  };
  data = JSON.stringify(data);

  $.ajax({
    url: '/appendACSVariables',
    method: 'POST',
    data: data,
    contentType: "application/json;charset=utf-8",
    success: function(data) {
      console.log("Status: ", data.status);
      console.log("Message: ", data.message);
    },
    error: function(request, error) {
      console.log(error);
    }
  });
}

function uploadSingleFile(file) {
    var formData = new FormData();
    formData.append("file", file);

    var xhr = new XMLHttpRequest();
    xhr.open("POST", "/uploadFile");

    xhr.onload = function() {
        console.log(xhr.responseText);
        var response = JSON.parse(xhr.responseText);
        if(xhr.status == 200) {
            singleFileUploadError.style.display = "none";
            singleFileUploadSuccess.innerHTML = "<p>File Uploaded Successfully.</p><p>DownloadUrl : <a href='" + response.fileDownloadUri + "' target='_blank'>" + response.fileDownloadUri + "</a></p>";
            singleFileUploadSuccess.style.display = "block";
        } else {
            singleFileUploadSuccess.style.display = "none";
            singleFileUploadError.innerHTML = (response && response.message) || "Some Error Occurred";
        }
    }

    xhr.send(formData);
}

// function uploadMultipleFiles(files) {
//     var formData = new FormData();
//     for(var index = 0; index < files.length; index++) {
//         formData.append("files", files[index]);
//     }

//     var xhr = new XMLHttpRequest();
//     xhr.open("POST", "/uploadMultipleFiles");

//     xhr.onload = function() {
//         console.log(xhr.responseText);
//         var response = JSON.parse(xhr.responseText);
//         if(xhr.status == 200) {
//             multipleFileUploadError.style.display = "none";
//             var content = "<p>All Files Uploaded Successfully</p>";
//             for(var i = 0; i < response.length; i++) {
//                 content += "<p>DownloadUrl : <a href='" + response[i].fileDownloadUri + "' target='_blank'>" + response[i].fileDownloadUri + "</a></p>";
//             }
//             multipleFileUploadSuccess.innerHTML = content;
//             multipleFileUploadSuccess.style.display = "block";
//         } else {
//             multipleFileUploadSuccess.style.display = "none";
//             multipleFileUploadError.innerHTML = (response && response.message) || "Some Error Occurred";
//         }
//     }

//     xhr.send(formData);
// }

var singleUploadForm = document.querySelector('#singleUploadForm');
var singleFileUploadInput = document.querySelector('#singleFileUploadInput');
var singleFileUploadError = document.querySelector('#singleFileUploadError');
var singleFileUploadSuccess = document.querySelector('#singleFileUploadSuccess');

var multipleUploadForm = document.querySelector('#multipleUploadForm');
var multipleFileUploadInput = document.querySelector('#multipleFileUploadInput');
var multipleFileUploadError = document.querySelector('#multipleFileUploadError');
var multipleFileUploadSuccess = document.querySelector('#multipleFileUploadSuccess');

var testButton = document.querySelector('#tester');

onPageLoad();

testButton.addEventListener('click', function(event) {
  console.log("Clicked Test Button!");

  event.preventDefault();
});

singleUploadForm.addEventListener('submit', function(event){
    var files = singleFileUploadInput.files;
    if(files.length === 0) {
        singleFileUploadError.innerHTML = "Please select a file";
        singleFileUploadError.style.display = "block";
    }
    uploadSingleFile(files[0]);
    event.preventDefault();
}, true);


// multipleUploadForm.addEventListener('submit', function(event){
//     var files = multipleFileUploadInput.files;
//     if(files.length === 0) {
//         multipleFileUploadError.innerHTML = "Please select at least one file";
//         multipleFileUploadError.style.display = "block";
//     }
//     uploadMultipleFiles(files);
//     event.preventDefault();
// }, true);

