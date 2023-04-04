package com.typedb.examples.fraud.model;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bank bank)) return false;
        return bank_name.equals(bank.bank_name) && bankCoordinates.equals(bank.bankCoordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bank_name, bankCoordinates);
    }
}
