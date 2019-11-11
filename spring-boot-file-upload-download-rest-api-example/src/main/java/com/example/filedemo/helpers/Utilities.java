package com.example.filedemo.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Utilities {
  public static String getTodayDate() {
    // MM/DD/YYYY
    Date date = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
    String dateStr = formatter.format(date);
    return dateStr;
  }

  public static Map<String, String> parseDOB(String dob) {
    // MM/DD/YYYY
    Date date = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

    try {
      date = formatter.parse(dob);
    } catch (ParseException io) {
      io.printStackTrace();
    }

    LocalDate localDate = date.toInstant()
      .atZone(ZoneId.systemDefault())
      .toLocalDate();

    // Map that will hold Year, Month, Day Keys
    Map<String, String> dateMap = new HashMap<String, String>();

    // Get Year
    int year = localDate.getYear();
    dateMap.put("Year", String.valueOf(year));

    // Get Month
    int month = localDate.getMonthValue(); // 1 to 12
    String monthStr = String.valueOf(month);
    if (month < 10) {
      monthStr = "0" + monthStr;
    }
    dateMap.put("Month", monthStr);

    // Get Day
    int day = localDate.getDayOfMonth();
    String dayStr = String.valueOf(day);
    if (day < 10) {
      dayStr = "0" + dayStr;
    }
    dateMap.put("Day", dayStr);

    return dateMap;
  }

  public static boolean ageIsLessThan20(String dob, String timeOfMeasurement) {
    // MM/DD/YYYY
    Date dobDate = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
    Date timeMeasurementDate = new Date();
    SimpleDateFormat formatter2 = new SimpleDateFormat("MM/dd/yyyy");

    try {
      dobDate = formatter.parse(dob);
      timeMeasurementDate = formatter2.parse(timeOfMeasurement);
    } catch (ParseException io) {
      io.printStackTrace();
    }

    long difference = timeMeasurementDate.getTime() - dobDate.getTime();
    float daysDiff = (difference / (1000*60*60*24));
    
    if (daysDiff < 7300) {
      // Less than 20 years old
      return true;
    }
    return false;
  }
}