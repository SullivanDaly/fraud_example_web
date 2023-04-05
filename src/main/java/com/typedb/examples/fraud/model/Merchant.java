package com.typedb.examples.fraud.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvRecurse;
import java.util.Objects;

public class Merchant {

  @CsvBindByName(column = "merchant")
  private String company_name;
  @CsvBindByName(column = "category")
  private String company_cat;
  @CsvRecurse
  private MerchantCoordinates merchantCoordinates;

  public Merchant(String company_name, String company_cat,
      MerchantCoordinates merchantCoordinates) {
    this.company_name = company_name;
    this.company_cat = company_cat;
    this.merchantCoordinates = merchantCoordinates;
  }

  public String getCompany_name() {
    return company_name;
  }

  public String getCompany_cat() {
    return company_cat;
  }

  public MerchantCoordinates getMerchantCoordinates() {
    return merchantCoordinates;
  }

  @Override
  public String toString() {
    return "Merchant{" +
        "company_name='" + company_name + '\'' +
        ", company_cat='" + company_cat + '\'' +
        ", merchantCoordinates=" + merchantCoordinates +
        '}';
  }

  @Override
  public boolean equals(Object o) {
      if (this == o) {
          return true;
      }
      if (!(o instanceof Merchant merchant)) {
          return false;
      }
    return company_name.equals(merchant.company_name) && company_cat.equals(merchant.company_cat) &&
        merchantCoordinates.equals(merchant.merchantCoordinates);
  }

  @Override
  public int hashCode() {
    return Objects.hash(company_name, company_cat);
  }
}
