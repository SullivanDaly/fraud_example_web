package com.typedb.examples.fraud.model;

public class Bank {
    private String bank_name;
    private BankCoordinates bankCoordinates;
    public String getBank_name() {
        return bank_name;
    }

    public BankCoordinates getBankCoordinates() {
        return bankCoordinates;
    }

    public Bank(String bank_name, BankCoordinates bankCoordinates) {
        this.bank_name = bank_name;
        this.bankCoordinates = bankCoordinates;
    }

    @Override
    public String toString() {
        return "Bank{" +
                "bank_name='" + bank_name + '\'' +
                ", bankCoordinates=" + bankCoordinates +
                '}';
    }
}
