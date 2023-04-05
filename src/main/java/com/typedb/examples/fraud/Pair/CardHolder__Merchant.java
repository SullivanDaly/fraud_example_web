package com.typedb.examples.fraud.Pair;

import com.typedb.examples.fraud.model.Cardholder;
import com.typedb.examples.fraud.model.Merchant;
import java.util.Objects;

public class CardHolder__Merchant {

  private Cardholder cardholder;
  private Merchant merchant;

  public CardHolder__Merchant(Cardholder cardholder, Merchant merchant) {
    this.cardholder = cardholder;
    this.merchant = merchant;
  }

  public Cardholder getCardholder() {
    return cardholder;
  }

  public Merchant getMerchant() {
    return merchant;
  }

  @Override
  public boolean equals(Object o) {
      if (this == o) {
          return true;
      }
      if (!(o instanceof CardHolder__Merchant that)) {
          return false;
      }
    return cardholder.equals(that.cardholder) && merchant.equals(that.merchant);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cardholder, merchant);
  }
}
