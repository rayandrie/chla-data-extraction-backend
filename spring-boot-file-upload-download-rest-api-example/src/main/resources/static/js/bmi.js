'use strict';

function callBmiEndpoint(weight, weightMetric, height, heightMetric, gender, dob, officeVisit) {
  let dataBmi = {
    weight: weight,
    weightMetric: weightMetric,
    height: height,
    heightMetric: heightMetric,
    gender: gender,
    dob: dob,
    officeVisitDate: officeVisit,
  }
  dataBmi = JSON.stringify(dataBmi);

  $.ajax({
    url: '/testBmiInfo',
    method: 'POST',
    data: dataBmi,
    contentType: "application/json;charset=utf-8",
    success: function(data) {
      console.log(data);
      parseBmiResponse(data);
    },
    error: function(request, error) {
      console.log(error);
    }
  });
}

function parseBmiResponse(data) {
  if (data == null) return;
  console.log("IN EHRE");
  // Set Weight
  $("#weightResult").html(data.weight);
  // Set Height
  $("#heightResult").html(data.height);
  // Set Gender
  $("#genderResult").html(data.gender);
  // Set DOB
  $("#dobResult").html(data.dob);
  // Set Date of Office Visit
  $("#officeVisitResult").html(data.officeVisitDate);
  // Set Age in Months
  $("#ageInMonthsResult").html(data.ageInMonthsAtTimeOfVisit);
  // Set BMI
  $("#bmiResult").html(data.bmi);
  // Set Z-Score
  $("#zScoreResult").html(data.zscore);
  // Set Percentile
  $("#percentileResult").html(data.percentile);
}

$("form").submit(function(event) {
  event.preventDefault();

  // TODO: Add Validation

  // Get Weight
  const weight = $("#weightParam").val();
  const weightMetric = $("input[name=weightMetric]:checked").val();

  // Get Height
  const height = $("#heightParam").val();
  const heightMetric = $("input[name=heightMetric]:checked").val();

  // Get Gender
  const gender = $("input[name=gender]:checked").val();

  // Get DOB
  const dob = $("#dobParam").val();

  // Get Office Visit Date
  const officeVisit = $("#officeVisitParam").val();

  // Check Output
  console.log(weight);
  console.log(weightMetric);
  console.log(height);
  console.log(heightMetric);
  console.log(gender);
  console.log(dob);
  console.log(officeVisit);

  // POST BMI Form
  callBmiEndpoint(weight, weightMetric, height, heightMetric, gender, dob, officeVisit);
});