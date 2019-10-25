'use strict';

function downloadSingleFile() {
    
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "/returnDownloadFile");

    xhr.onload = function() {
        console.log(xhr.responseText);
        var response = JSON.parse(xhr.responseText);

        if(xhr.status == 200) {
            console.log("DOWNLAOD");

            downloadLink.href = response.fileDownloadUri;
            downloadLink.download = response.fileDownloadUri;

        } //add error checking here
    }
    xhr.send();
}

var download = document.querySelector('#download');
var downloadLink = document.querySelector('#downloadLink');

download.addEventListener('click', function(event) {

    console.log("ONSUBMIT CLICKED");
    downloadSingleFile();
   
})