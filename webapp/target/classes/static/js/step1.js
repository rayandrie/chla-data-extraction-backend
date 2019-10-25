'use strict';

function onPageLoad() {
  // Testing Endpoint
  // $.ajax({
  //   url: '/testing',
  //   method: 'GET',
  //   success: function(data) {
  //     console.log("Status: ", data.status);
  //     console.log("Message: ", data.message);
  //   },
  //   error: function(request, error) {
  //     console.log(error);
  //   }
  // });

  // Sample POST Call
  let dataAcs = {
    listOfDetailedVariables: [
      'Median Gross Rent as a % of Household Income - Renter-Occupied Households paying cash rent',
      'GINI Index of Income Inequality Households'
    ],
    listOfSubjectVariables: [
      '% of Total Population - Under 5 Years',
      '% of Total Households, Renter-Occupied - Household Size'
    ]
  };
  dataAcs = JSON.stringify(dataAcs);

  $.ajax({
    url: '/appendACSVariables',
    method: 'POST',
    data: dataAcs,
    contentType: "application/json;charset=utf-8",
    success: function(data) {
      console.log("Status: ", data.status);
      console.log("Message: ", data.message);
    },
    error: function(request, error) {
      console.log(error);
    }
  });

  let dataSsdi = {
    firstName: "Elvis",
    lastName: "Presley"
  };
  dataSsdi = JSON.stringify(dataSsdi);

  $.ajax({
    url: '/getSsdiInfo',
    method: 'POST',
    data: dataSsdi,
    contentType: "application/json;charset=utf-8",
    success: function(data) {
      console.log("SSDI INFO: ");
      console.log(data);
    },
    error: function(request, error) {
      console.log(error);
    }
  });

  let dataBmi = {
    weight: "70",
    weightMetric: "kg",
    height: "180",
    heightMetric: "cm",
    gender: "male",
    dob: "08/04/2000",
    officeVisitDate: "08/04/2019",
  }
  dataBmi = JSON.stringify(dataBmi);

  $.ajax({
    url: '/getBmiInfo',
    method: 'POST',
    data: dataBmi,
    contentType: "application/json;charset=utf-8",
    success: function(data) {
      console.log("BMI DATA: ");
      console.log(data);
    },
    error: function(request, error) {
      console.log(error);
    }
  });
}

onPageLoad();