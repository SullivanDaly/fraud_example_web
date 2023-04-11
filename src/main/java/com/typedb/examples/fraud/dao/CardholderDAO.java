package com.typedb.examples.fraud.dao;

import com.typedb.examples.fraud.db.TypeDbSessionWrapper;
import com.typedb.examples.fraud.model.Cardholder;
import com.typedb.examples.fraud.result.CardHolderMerchant;
import java.util.Hashtable;
import java.util.Set;
import java.util.stream.Collectors;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class CardholderDAO {

  private static final String INSERT_QUERY_TEMPLATE =
      "match " +
      "  $bank isa Bank, has name \"%s\";" +
      "insert " +
      "  $cardholderCoords isa Geo_coordinate, has latitude %s, has longitude %s;" +
      "  $cardholderAddr isa Address, has street \"%s\", has city \"%s\", has state \"%s\", has zip %s;" +
      "  $cardholder isa Person, has first_name \"%s\", has last_name \"%s\", has gender \"%s\", has job \"%s\", has date_of_birth %s;" +
      "  $cardholderLoc (location: $cardholderAddr, geo: $cardholderCoords, identify: $cardholder) isa locate;" +
      "  $cardholderAccount (owner: $cardholder, attached_card: $cc, attached_bank: $bank) isa bank_account;" +
      "  $cc isa Card, has card_number %s;";

  private static final String SAFE_TX_MATCH =
      "  (person: $cardholder, merchant: $merchant) isa same_place;";

  private static final String UNSAFE_TX_MATCH =
      "  (person: $cardholder, marchant: $merchant) isa unsafe_relationship";

  protected static final String CARDHOLDER_MATCH =
      "  $cardholderCoords isa Geo_coordinate, has latitude $cardholderLat, has longitude $cardholderLon;" +
      "  $cardholderAddr isa Address, has street $street, has city $city, has state $state, has zip $zip;" +
      "  $cardholder isa Person, has first_name $firstName, has last_name $lastName, has gender $gender, has job $job, has date_of_birth $birthDate;" +
      "  $cardholderLoc (location: $cardholderAddr, geo: $cardholderCoords, identify: $cardholder) isa locate;" +
      "  $cc isa Card, has card_number $ccNum;" +
      "  $cardholderAccount (owner: $cardholder, attached_card: $cc, attached_bank: $bank) isa bank_account;";

  @Inject
  TypeDbSessionWrapper db;

  public Set<Cardholder> getAll() {

    var getQueryStr = "match " + CARDHOLDER_MATCH + BankDAO.BANK_MATCH;

    var results = db.getAll(getQueryStr);

    var cardholders = results.stream().map(CardholderDAO::fromResult).collect(Collectors.toSet());

    return cardholders;
  }

  public Set<CardHolderMerchant> getWithMerchants(boolean safeTx) {

    var getQueryStr = "match " + CARDHOLDER_MATCH + BankDAO.BANK_MATCH + MerchantDAO.MERCHANT_MATCH;

    if (safeTx) {
      getQueryStr += SAFE_TX_MATCH;
    }
    else {
      getQueryStr += UNSAFE_TX_MATCH;
    }

    var results = db.getAll(getQueryStr);

    var cardholderMerchants = results.stream().map(CardholderDAO::fromResultWithMerchant).collect(Collectors.toSet());

    return cardholderMerchants;
  }

  public Set<CardHolderMerchant> getWithSafeMerchants() {

    return getWithMerchants(true);
  }

  public Set<CardHolderMerchant> getWithUnsafeMerchants() {

    return getWithMerchants(false);
  }

  public void insertAll(Set<Cardholder> cardholders) {

    var queries = cardholders.stream().map(this::getInsertQueryStr).collect(Collectors.toSet());

    db.insertAll(queries);
  }

  protected static Cardholder fromResult(Hashtable<String, String> result) {

    var cc = CreditCardDAO.fromResult(result);
    var coords = CardholderCoordsDAO.fromResult(result);
    var addr = AddressDAO.fromResult(result);

    var firstName = result.get("firstName");
    var lastName = result.get("lastName");
    var gender = result.get("gender");
    var job = result.get("job");
    var birthDate = result.get("birthDate");

    var cardholder = new Cardholder(firstName, lastName, gender, job, birthDate, addr, coords, cc);

    return cardholder;
  }

  protected static CardHolderMerchant fromResultWithMerchant(Hashtable<String, String> result) {

    var cardholder = fromResult(result);
    var merchant = MerchantDAO.fromResult(result);

    var cardholderMerchant = new CardHolderMerchant(cardholder, merchant);

    return cardholderMerchant;
  }

  private String getInsertQueryStr(Cardholder cardholder) {

    var insertQueryStr = INSERT_QUERY_TEMPLATE.formatted(
        cardholder.getCc().getBank().getName(),
        cardholder.getCoords().getLongitude(),
        cardholder.getCoords().getLatitude(),
        cardholder.getAddress().getStreet(),
        cardholder.getAddress().getCity(),
        cardholder.getAddress().getState(),
        cardholder.getAddress().getZip(),
        cardholder.getFirstName(),
        cardholder.getLastName(),
        cardholder.getGender(),
        cardholder.getJob(),
        cardholder.getBirthDate(),
        cardholder.getCc().getNumber()
    );

    return insertQueryStr;
  }
}
