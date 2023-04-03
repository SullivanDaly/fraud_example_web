package com.typedb.examples.fraud.model;

import com.opencsv.bean.CsvBindByName;

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
}
