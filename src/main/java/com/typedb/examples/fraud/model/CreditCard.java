package com.typedb.examples.fraud.model;

import com.opencsv.bean.CsvBindByName;

public class CreditCard {
    @CsvBindByName(column = "cc_num")
    private String card_number;

    private Bank bank;

    public CreditCard(String card_number, Bank bank) {
        this.card_number = card_number;
        this.bank = bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public Bank getBank() {
        return bank;
    }

    public String getCard_number() {
        return card_number;
    }

    @Override
    public String toString() {
        return "CreditCard{" +
                ", card_number='" + card_number + '\'' +
                ", bank='" + bank + '\'' +
                '}';
    }
}
