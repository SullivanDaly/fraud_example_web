package com.typedb.examples.fraud.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvRecurse;

import java.util.Objects;

public class Transaction {
    @CsvBindByName(column = "amt")
    private String amount;
    @CsvBindByName(column = "trans_num")
    private String transaction_number;
    @CsvBindByName(column = "trans_date_trans_time")
    private String date_transaction;
    @CsvRecurse
    private Merchant merchant;
    @CsvRecurse
    private Cardholder cardholder;

    public Transaction(String amount, String transaction_number, String date_transaction, Merchant merchant, Cardholder cardholder) {
        this.amount = amount;
        this.transaction_number = transaction_number;
        this.date_transaction = date_transaction;
        this.merchant = merchant;
        this.cardholder = cardholder;
    }

    public String getDate_transaction_transform() {
        String[] tmp = date_transaction.split(" ");
        return tmp[0] + "T" + tmp[1];
    }

    public String getDate_transaction() {
        return date_transaction;
    }

    public String getAmount() {
        return amount;
    }

    public String getTransaction_number() {
        return transaction_number;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public Cardholder getCardholder() {
        return cardholder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction that)) return false;
        return amount.equals(that.amount) && transaction_number.equals(that.transaction_number) &&
                date_transaction.equals(that.date_transaction) && merchant.equals(that.merchant) &&
                cardholder.equals(that.cardholder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, transaction_number, date_transaction, merchant, cardholder);
    }
}
