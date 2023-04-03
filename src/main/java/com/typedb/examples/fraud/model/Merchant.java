package com.typedb.examples.fraud.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvRecurse;

public class Merchant {
    @CsvBindByName(column = "merchant")
    private String company_name;
    @CsvBindByName(column = "category")
    private String company_cat;
    @CsvRecurse
    private MerchantCoordinates merchantCardholderCoordinates;

    public Merchant(String company_name, String company_cat, MerchantCoordinates merchantCardholderCoordinates) {
        this.company_name = company_name;
        this.company_cat = company_cat;
        this.merchantCardholderCoordinates = merchantCardholderCoordinates;
    }

    public String getCompany_name() {
        return company_name;
    }

    public String getCompany_cat() {
        return company_cat;
    }

    public MerchantCoordinates getMerchantCoordinates() {
        return merchantCardholderCoordinates;
    }

    @Override
    public String toString() {
        return "Merchant{" +
                "company_name='" + company_name + '\'' +
                ", company_cat='" + company_cat + '\'' +
                ", merchantCoordinates=" + merchantCardholderCoordinates +
                '}';
    }
}
