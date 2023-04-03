package com.typedb.examples.fraud.model;

import com.opencsv.bean.CsvBindByName;

public class Address {
    @CsvBindByName(column = "street")
    private String street;
    @CsvBindByName(column = "city")
    private String city;
    @CsvBindByName(column = "state")
    private String state;
    @CsvBindByName(column = "zip")
    private String zip;

    public Address(String street, String city, String state, String zip) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.zip = zip;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZip() {
        return zip;
    }

    @Override
    public String toString() {
        return "Address{" +
                "street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zip='" + zip + '\'' +
                '}';
    }

}
