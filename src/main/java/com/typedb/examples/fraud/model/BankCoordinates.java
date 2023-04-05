package com.typedb.examples.fraud.model;

import java.util.Objects;

public class BankCoordinates {

  private String latitude;
  private String longitude;

  public BankCoordinates(String latitude, String longitude) {
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
    if (!(o instanceof BankCoordinates that)) {
      return false;
    }
    return latitude.equals(that.latitude) && longitude.equals(that.longitude);
  }

  @Override
  public int hashCode() {
    return Objects.hash(latitude, longitude);
  }
}