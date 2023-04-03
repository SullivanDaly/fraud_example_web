package com.typedb.examples.fraud.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvRecurse;

public class Cardholder {
    @CsvBindByName(column = "first")
    private String person_first_name;
    @CsvBindByName(column = "last")
    private String person_last_name;
    @CsvBindByName(column = "gender")
    private String gender;
    @CsvBindByName(column = "job")
    private String job;
    @CsvBindByName(column = "dob")
    private String date_of_birth;
    @CsvRecurse
    private Address address;
    @CsvRecurse
    private CardholderCoordinates cardholderCoordinates;
    @CsvRecurse
    private CreditCard creditCard;

    public Cardholder(String person_first_name, String person_last_name, String gender, String job, String date_of_birth,
                      Address address, CardholderCoordinates cardholderCoordinates, CreditCard creditCard) {
        this.person_first_name = person_first_name;
        this.person_last_name = person_last_name;
        this.gender = gender;
        this.job = job;
        this.date_of_birth = date_of_birth;
        this.address = address;
        this.cardholderCoordinates = cardholderCoordinates;
        this.creditCard = creditCard;
    }

    public String getPerson_first_name() {
        return person_first_name;
    }

    public String getPerson_last_name() {
        return person_last_name;
    }

    public String getGender() {
        return gender;
    }

    public String getJob() {
        return job;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public Address getAddress() {
        return address;
    }

    public CardholderCoordinates getCardholderCoordinates() {
        return cardholderCoordinates;
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }

    @Override
    public String toString() {
        return "Cardholder{" +
                "person_first_name='" + person_first_name + '\'' +
                ", person_last_name='" + person_last_name + '\'' +
                ", gender='" + gender + '\'' +
                ", job='" + job + '\'' +
                ", date_of_birth='" + date_of_birth + '\'' +
                ", address=" + address +
                ", cardholderCoordinates=" + cardholderCoordinates +
                '}';
    }
}
