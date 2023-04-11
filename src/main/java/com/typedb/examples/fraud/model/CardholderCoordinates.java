package com.typedb.examples.fraud.model;

import com.opencsv.bean.CsvBindByName;
import java.util.Objects;

public class CardholderCoordinates {

  @CsvBindByName(column = "lat")
  private String latitude;
  @CsvBindByName(column = "long")
  private String longitude;

  public CardholderCoordinates(String latitude, String longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public String getLatitude() {
    return latitude;
  }

  public String getLongitude() {
    return longitude;
  }

  @Override
  public String toString() {
    return "Coordinates{" +
        "latitude='" + latitude + '\'' +
        ", longitude='" + longitude + '\'' +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CardholderCoordinates that)) {
      return false;
    }
    return latitude.equals(that.latitude) && longitude.equals(that.longitude);
  }

  @Override
  public int hashCode() {
    return Objects.hash(latitude, longitude);
  }
}
