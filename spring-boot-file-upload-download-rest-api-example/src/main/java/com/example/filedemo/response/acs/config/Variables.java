package com.example.filedemo.response.acs.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Variables {

  // ACS 5 Year Estimate Table Map
  public static final Map<String, String> VAR_ID_BY_NAME;
  static {
    Map<String, String> map = new HashMap<String, String>();
    // S0101
    map.put("% of Total Population - Under 5 Years", "S0101_C02_002E");
    map.put("% of Total Population - 5 to 14 Years", "S0101_C02_020E");
    map.put("% of Total Population - 65 Years and Over", "S0101_C02_030E");
    map.put("Age Dependency Ratio", "S0101_C02_034E");
    // S0601
    map.put("% of Total Population - White", "S0601PR_C01_014E");
    map.put("% of Total Population - Black/African-American", "S0601PR_C01_015E");
    map.put("% of Total Population - American Indian and Alaskan Native", "S0601PR_C01_016E");
    map.put("% of Total Population - Asian", "S0601PR_C01_017E");
    map.put("% of Total Population - Native Hawaiian and Other Pacific Islander", "S0601PR_C01_018E");
    map.put("% of Total Population - Some Other Race", "S0601PR_C01_019E");
    map.put("% of Total Population - Two or More Races", "S0601PR_C01_020E");
    map.put("% of Total Population - Hispanic or Latino Origin (any race)", "S0601PR_C01_021E");
    map.put("% of Total Population - Not Hispanic or Latino", "S0601PR_C01_022E");
    map.put("% of Total Population 5 years and over - Speak language other than English", "S0601PR_C01_024E");
    map.put("% of Total Population 5 years and over - Speak English 'very well'", "S0601PR_C01_025E");
    map.put("% of Total Population 5 years and over - Speak English less than 'very well'", "S0601PR_C01_026E");
    // S1101 - TODO: (Raymond) Get percentages
    map.put("Total Households", "S1101_C01_001E");
    map.put("Average Household Size", "S1101_C01_002E");
    map.put("Average Family Size", "S1101_C01_003E");
    map.put("Owner-Occupied Housing Units", "S1101_C01_019E");
    map.put("Renter-Occupied Housing Units", "S1101_C01_020E");
    // S1501
    map.put("% of Total Population 25 years and over - High School Graduate or Higher", "S1501_C02_014E");
    map.put("% of Total Population 25 years and over - Bachelor's Degree or Higher", "S1501_C02_015E");
    // S1701
    map.put("% of Total Population for whom Poverty Status is determined - Percent below Poverty Level", "S1701_C03_001E");
    map.put("% of Total Population for whom Poverty Status is determined - Percent under 5 Years", "S1701_C03_003E");
    map.put("% of Total Population for whom Poverty Status is determined - Percent 5 to 17 Years", "S1701_C03_004E");
    map.put("% of Total Population for whom Poverty Status is determined - Percent below 200% Poverty Level", "S1701_C03_042E");
    // S2201
    map.put("% of Total Households - Percent Households receiving food stamps/SNAP", "S2201_C04_001E");
    // S2302
    map.put("% of Families with own children under 18 years - No workers in the past 12 months", "S2302_C04_015E");
    map.put("% of Families with own children under 18 years - 1 worker in the past 12 months", "S2302_C04_016E");
    map.put("% of Families with own children under 18 years - 2 or more workers in the past 12 months", "S2302_C04_017E");
    // S2504
    map.put("% of Total Households, Renter-Occupied - 2014 or Later", "S2504_C06_009E");
    map.put("% of Total Households, Renter-Occupied - 2010 to 2013", "S2504_C06_010E");
    map.put("% of Total Households, Renter-Occupied - 2000 to 2009", "S2504_C06_011E");
    map.put("% of Total Households, Renter-Occupied - 1980 to 1999", "S2504_C06_012E");
    map.put("% of Total Households, Renter-Occupied - 1960 to 1979", "S2504_C06_013E");
    map.put("% of Total Households, Renter-Occupied - 1940 to 1959", "S2504_C06_014E");
    map.put("% of Total Households, Renter-Occupied - 1939 or Earlier", "S2504_C06_015E");
    map.put("% of Total Households, Owner-Occupied - 2014 or Later", "S2504_C04_009E");
    map.put("% of Total Households, Owner-Occupied - 2010 to 2013", "S2504_C04_010E");
    map.put("% of Total Households, Owner-Occupied - 2000 to 2009", "S2504_C04_011E");
    map.put("% of Total Households, Owner-Occupied - 1980 to 1999", "S2504_C04_012E");
    map.put("% of Total Households, Owner-Occupied - 1960 to 1979", "S2504_C04_013E");
    map.put("% of Total Households, Owner-Occupied - 1940 to 1959", "S2504_C04_014E");
    map.put("% of Total Households, Owner-Occupied - 1939 or Earlier", "S2504_C04_015E");
    // S2501
    map.put("% of Total Households, Renter-Occupied - Household Size", "S2501_C06_001E");
    map.put("% of Total Households, Renter-Occupied - 1.00 or less Occupants per room", "S2501_C06_006E");
    map.put("% of Total Households, Renter-Occupied - 1.01 to 1.50 Occupants per room", "S2501_C06_007E");
    map.put("% of Total Households, Renter-Occupied - 1.51 or more Occupants per room", "S2501_C06_008E");
    map.put("% of Total Households, Owner-Occupied - Household Size", "S2501_C04_001E");
    map.put("% of Total Households, Owner-Occupied - 1.00 or less Occupants per room", "S2501_C04_006E");
    map.put("% of Total Households, Owner-Occupied - 1.01 to 1.50 Occupants per room", "S2501_C04_007E");
    map.put("% of Total Households, Owner-Occupied - 1.51 or more Occupants per room", "S2501_C04_008E");
    // S1002
    map.put("% Distribution - Grandparents living with own grandchildren under 18 years in households", "S1002_C02_001E");
    map.put("% Distribution - Householder/Spouse responsible for grandchildren with no parent present", "S1002_C02_028E");
    map.put("% Distribution - Households with grandparents living with grandchildren", "S1002_C02_030E");
    // B25071
    map.put("Median Gross Rent as a % of Household Income - Renter-Occupied Households paying cash rent", "B25071_001E");
    // B19083
    map.put("GINI Index of Income Inequality Households", "B19083_001E");
    VAR_ID_BY_NAME = Collections.unmodifiableMap(map);
  }
  
}