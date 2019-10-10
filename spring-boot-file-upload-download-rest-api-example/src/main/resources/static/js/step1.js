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
    listOfDetailedVariables: [
      'Median Gross Rent as a % of Household Income - Renter-Occupied Households paying cash rent',
      'GINI Index of Income Inequality Households'
    ],
    listOfSubjectVariables: [
      '% of Total Population - Under 5 Years',
      '% of Total Households, Renter-Occupied - Household Size'
    ]
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

onPageLoad();