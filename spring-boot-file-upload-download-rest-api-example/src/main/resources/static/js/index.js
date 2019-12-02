//jQuery time
var current_fs, next_fs, previous_fs; //fieldsets
var left, opacity, scale; //fieldset properties which we will animate
var animating; //flag to prevent quick multi-click glitches

/* When the user clicks on the button, 
toggle between hiding and showing the dropdown content */
$(document).ready(function () {
	$('#trigger').click(function (event) {
		event.stopPropagation();
		$('#drop').toggle();
	});

	$(document).click(function () {
		$('#drop').hide();
	});

	//$('#category').multiselect();

	//$('select[multiple]').multiselect();

	// $('.selections').dropdown({
	// 	// options here
	//   });
});

// $('#variables').multiselect({
//     columns: 1,
//     placeholder: 'Select Languages',
//     search: true,
//     selectAll: true
// });

var varsByCategory = {
	A: ["ACS 1", "ACS 2", "ACS 3", "ACS 4"],
	B: ["BMI 1", "BMI 2", "BMI 3", "BMI 4"],
	C: ["SSDI 1", "SSDI 2", "SSDI 3", "SSDI 4"]
}

function changecat(value) {
	if (value.length == 0) document.getElementById("category").innerHTML = "<option></option>";
	else {
		var catOptions = "";
		for (categoryId in varsByCategory[value]) {
			catOptions += "<option>" + varsByCategory[value][categoryId] + "</option>";
		}
		document.getElementById("category").innerHTML = catOptions;
	}
}

$(".next").click(function (event) {

	//the user chose the variables
	if ((event.target.id) == "chosedb") {
		var choseNone = true;
		if(document.getElementById("BMI_switch").checked){
			choseNone = false;
		  }
		  if(document.getElementById("SSDI_switch").checked){
			choseNone = false;
		  }
		var selectedValues = $('#vars').val();
		if(selectedValues){
			choseNone = false;
		}
		console.log("selected values:" + selectedValues + "\nBMI selected: " + "\nSSDI selected: ");
		if(!choseNone){
			console.log("choseNone is false");
			sendVars();
		}
		else{
			return;
		}
	}

	//if the user tries to click next without uploading a file
	if ((event.target.id) == "uploadFile") {
		console.log("ONSUBMIT CLICKED");
		var files = singleFileUploadInput.files;
		if (files.length === 0) {
			singleFileUploadError.innerHTML = "Please select a file";
			console.log("DIDN'T SELECT FILE");
			singleFileUploadError.style.display = "block";
			return;
		}
		uploadSingleFile(files[0]);
		// downloadSingleFile();
		event.preventDefault();
	}

	if (animating) return false;
	animating = true;

	current_fs = $(this).parent();
	next_fs = $(this).parent().next();

	//activate next step on progressbar using the index of next_fs
	$("#progressbar li").eq($("fieldset").index(next_fs)).addClass("active");

	//show the next fieldset
	next_fs.show();
	//hide the current fieldset with style
	current_fs.animate({ opacity: 0 }, {
		step: function (now, mx) {
			//as the opacity of current_fs reduces to 0 - stored in "now"
			//1. scale current_fs down to 80%
			scale = 1 - (1 - now) * 0.2;
			//2. bring next_fs from the right(50%)
			left = (now * 50) + "%";
			//3. increase opacity of next_fs to 1 as it moves in
			opacity = 1 - now;
			current_fs.css({
				'transform': 'scale(' + scale + ')',
				'position': 'absolute'
			});
			next_fs.css({ 'left': left, 'opacity': opacity });
		},
		duration: 800,
		complete: function () {
			current_fs.hide();
			animating = false;
		},
		//this comes from the custom easing plugin
		easing: 'easeInOutBack'
	});
});

$(".previous").click(function () {
	if (animating) return false;
	animating = true;

	current_fs = $(this).parent();
	previous_fs = $(this).parent().prev();

	//de-activate current step on progressbar
	$("#progressbar li").eq($("fieldset").index(current_fs)).removeClass("active");

	//show the previous fieldset
	previous_fs.show();
	//hide the current fieldset with style
	current_fs.animate({ opacity: 0 }, {
		step: function (now, mx) {
			//as the opacity of current_fs reduces to 0 - stored in "now"
			//1. scale previous_fs from 80% to 100%
			scale = 0.8 + (1 - now) * 0.2;
			//2. take current_fs to the right(50%) - from 0%
			left = ((1 - now) * 50) + "%";
			//3. increase opacity of previous_fs to 1 as it moves in
			opacity = 1 - now;
			current_fs.css({ 'left': left });
			previous_fs.css({ 'transform': 'scale(' + scale + ')', 'opacity': opacity });
		},
		duration: 800,
		complete: function () {
			current_fs.hide();
			animating = false;
		},
		//this comes from the custom easing plugin
		easing: 'easeInOutBack'
	});
});

$(".submit").click(function () {
	return false;
})

///my stuff

function uploadSingleFile(file) {
	var formData = new FormData();
	formData.append("file", file);

	var xhr = new XMLHttpRequest();
	xhr.open("POST", "/uploadFile");

	xhr.onload = function () {
		console.log(xhr.responseText);
		var response = JSON.parse(xhr.responseText);
		if (xhr.status == 200) {
			//error from backend getting census tract info - addresses not formatted correctly, dont show download btn
			var csvError = xhr.responseText.includes("Error in loading CSV file needed to get Census Tract Info");
			var missing = xhr.responseText.includes("missing");
			
			if (csvError || missing) {
				var json = JSON.parse(xhr.responseText);
				document.querySelector('#success').innerHTML = "<p>" + json.fileDownloadUri + "Please try again." + "</p>";
				document.querySelector('#success').style.display = "block";
				document.getElementById('download').style.visibility= 'hidden';
			} else { // no errors, show success msg and download btn when ready
				singleFileUploadError.style.display = "none";
				document.querySelector('#success').innerHTML = "<p>Success! You can re-download your file, which has now been populated with your query results.</p>";				
				document.querySelector('#success').style.display = "block";
				document.getElementById('download').style.visibility= 'visible';
				downloadSingleFile();
			}
		} else {
			//singleFileUploadSuccess.style.display = "none";
			singleFileUploadError.innerHTML = (response && response.message) || "Some Error Occurred";
		}
	}

	xhr.send(formData);
}

//var singleUploadForm = document.querySelector('#singleUploadForm');
var singleFileUploadInput = document.querySelector('#singleFileUploadInput');
var singleFileUploadError = document.querySelector('#singleFileUploadError');
//var singleFileUploadSuccess = document.querySelector('#singleFileUploadSuccess');

// singleUploadForm.addEventListener('submit', function(event){
//   console.log("ONSUBMIT CLICKED");
//     var files = singleFileUploadInput.files;
//     if(files.length === 0) {
//         singleFileUploadError.innerHTML = "Please SELECT a file";
//         console.log("DIDN'T SELECT FILE");
//         singleFileUploadError.style.display = "block";
//     }
//     uploadSingleFile(files[0]);
//     event.preventDefault();
//     // window.location.href = "step4.html";

// }, true);


function downloadSingleFile() {
	event.preventDefault();
	var xhr = new XMLHttpRequest();
	xhr.open("POST", "/returnDownloadFile");

	xhr.onload = function () {
		console.log(xhr.responseText);
		var response = JSON.parse(xhr.responseText);

		if (xhr.status == 200) {
			downloadLink.href = response.fileDownloadUri;
			downloadLink.download = response.fileDownloadUri;
			document.getElementById('download').style.visibility= 'visible'; //make download btn visible
			console.log("download link:" + downloadLink.download);

		} else {
			console.log("error in download");
		}
	}
	xhr.send();
	event.preventDefault();
}

var download = document.querySelector('#download');
var downloadLink = document.querySelector('#downloadLink');

// download.addEventListener('click', function(event) {

//     console.log("ONSUBMIT CLICKED");
//     downloadSingleFile();

// })

// $( "#download" ).click(function() {
// 	e.preventDefault();
//   console.log("DOWNLOAD CLICKED");
//   downloadSingleFile();
// });

// $( "#uploadFile" ).click(function() {
//   console.log("ONSUBMIT CLICKED");
//     var files = singleFileUploadInput.files;
//     if(files.length === 0) {
//         singleFileUploadError.innerHTML = "Please SELECT a file";
//         console.log("DIDN'T SELECT FILE");
// 		singleFileUploadError.style.display = "block";
// 		return;
//     }
// 	uploadSingleFile(files[0]);
// 	$( ".next" ).trigger( "click" );
//     event.preventDefault();
// });

//helper function
function arrayRemove(arr, value) {

	return arr.filter(function(ele){
		return ele != value;
	});
 
 }

function sendVars() {
	// var formData = new FormData();
	// formData.append("file", file);

	// var xhr = new XMLHttpRequest();
	// xhr.open("POST", "/uploadFile");

	// xhr.onload = function() {
	//     console.log(xhr.responseText);
	//     var response = JSON.parse(xhr.responseText);
	//     if(xhr.status == 200) {
	// 		singleFileUploadError.style.display = "none";
	//         //singleFileUploadSuccess.innerHTML = "<p>File Uploaded Successfully.</p><p>DownloadUrl : <a href='" + response.fileDownloadUri + "' target='_blank'>" + response.fileDownloadUri + "</a></p>";
	//         //singleFileUploadSuccess.style.display = "block";
	//     } else {
	//         //singleFileUploadSuccess.style.display = "none";
	//         singleFileUploadError.innerHTML = (response && response.message) || "Some Error Occurred";
	//     }
	// }

	// xhr.send(formData)

	var listofSubjectVariables = [];
	if($('#vars').val()){
		listofSubjectVariables = $('#vars').val();
	}
	var listOfDetailedVariables = [];
	var detailedVar1 = "Median Gross Rent as a % of Household Income - Renter-Occupied Households paying cash rent";
	var detailedVar2 = "GINI Index of Income Inequality Households";
	if(listofSubjectVariables.includes(detailedVar1)){
		listofSubjectVariables = arrayRemove(listofSubjectVariables, detailedVar1);
		listOfDetailedVariables.push(detailedVar1);
	}
	if(listofSubjectVariables.includes(detailedVar2)){
		listofSubjectVariables = arrayRemove(listofSubjectVariables, detailedVar2);
		listOfDetailedVariables.push(detailedVar2);
	}

  //set up the json object
  var obj = new Object();
  // TODO: Modify below as needed
  obj.listOfSubjectVariables = listofSubjectVariables;
  obj.listOfDetailedVariables = [];

  obj.requestedSsdiInfo = false;
  obj.requestedBmiInfo = false;
  if(document.getElementById("BMI_switch").checked){
	obj.requestedBmiInfo = true;
  }
  if(document.getElementById("SSDI_switch").checked){
	obj.requestedSsdiInfo = true;
  }
	var jsonString= JSON.stringify(obj);
	console.log("json:\n " + jsonString);
	

	var xmlhttp = new XMLHttpRequest();   // new HttpRequest instance 
	var theUrl = "/chooseVariables";
	xmlhttp.open("POST", theUrl);
	xmlhttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");

	xmlhttp.onload = function () {
		console.log(xmlhttp.responseText);
		var response = JSON.parse(xmlhttp.responseText);
		if (xmlhttp.status == 200) {
			console.log("post request successful");
		} else {
			//singleFileUploadSuccess.style.display = "none";
			console.log(this.responseText);
			//singleFileUploadError.innerHTML = (response && response.message) || "Some Error Occurred";
		}
	}
	xmlhttp.send(jsonString);

}