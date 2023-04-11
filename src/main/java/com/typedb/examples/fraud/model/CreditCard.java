package com.typedb.examples.fraud.model;

import com.opencsv.bean.CsvBindByName;
import java.util.Objects;

public class CreditCard {

  @CsvBindByName(column = "cc_num")
  private String card_number;

  private Bank bank;

  public CreditCard(String card_number, Bank bank) {
    this.card_number = card_number;
    this.bank = bank;
  }

  public Bank getBank() {
    return bank;
  }

  public void setBank(Bank bank) {
    this.bank = bank;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CreditCard that)) {
      return false;
    }
    return card_number.equals(that.card_number) && bank.equals(that.bank);
  }

  @Override
  public int hashCode() {
    return Objects.hash(card_number, bank);
  }
}
