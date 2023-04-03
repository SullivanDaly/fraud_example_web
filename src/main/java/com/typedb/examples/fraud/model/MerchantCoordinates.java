package com.typedb.examples.fraud.model;

import com.opencsv.bean.CsvBindByName;

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
}
