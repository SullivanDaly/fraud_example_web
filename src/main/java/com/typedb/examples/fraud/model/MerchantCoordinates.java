package com.typedb.examples.fraud.model;

import com.opencsv.bean.CsvBindByName;
import java.util.Objects;

public class MerchantCoordinates {

  @CsvBindByName(column = "merch_lat")
  private String latitude_company;
  @CsvBindByName(column = "merch_long")
  private String longitude_company;

  public MerchantCoordinates(String latitude_company, String longitude_company) {
    this.latitude_company = latitude_company;
    this.longitude_company = longitude_company;
  }

  public String getLatitude() {
    return latitude_company;
  }

  public String getLongitude() {
    return longitude_company;
  }

  @Override
  public String toString() {
    return "MerchantCoordinates{" +
        "latitude_company='" + latitude_company + '\'' +
        ", longitude_company='" + longitude_company + '\'' +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof MerchantCoordinates that)) {
      return false;
    }
    return latitude_company.equals(that.latitude_company) && longitude_company.equals(
        that.longitude_company);
  }

  @Override
  public int hashCode() {
    return Objects.hash(latitude_company, longitude_company);
  }
}
