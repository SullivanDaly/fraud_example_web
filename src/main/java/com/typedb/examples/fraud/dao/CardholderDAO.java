package com.typedb.examples.fraud.dao;

import com.typedb.examples.fraud.model.Address;
import com.typedb.examples.fraud.model.Bank;
import com.typedb.examples.fraud.model.Cardholder;
import com.typedb.examples.fraud.model.CardholderCoordinates;
import com.typedb.examples.fraud.model.CreditCard;
import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.example.TypeDB_SessionWrapper;

public class CardholderDAO {

  private final TypeDB_SessionWrapper wrapper;
  private final String queryInsert = "insert\n"
      + "$gcp isa Geo_coordinate, has longitude %s, has latitude %s;\n"
      + "$add isa Address, has street \"%s\", has city \"%s\", has state \"%s\", has zip %s;\n"
      + "$per isa Person, has first_name \"%s\", has last_name \"%s\", has gender \"%s\", has job \"%s\", has date_of_birth %s;\n"
      + "$car isa Card, has card_number %s;"
      + "$r3(location: $add, geo: $gcp, identify: $per) isa locate;\n";

  private final String queryGet = "$geo isa Geo_coordinate, has longitude $lon, has latitude $lat;"
      + "$add isa Address, has street $street, has city $city, has state $state, has zip $zip;"
      + "$per isa Person, has first_name $first, has last_name $last, has gender $gen, has job $job, has date_of_birth $birth;"
      + "$car isa Card, has card_number $nbcar;"
      + "$ban isa Bank, has name $bank;"
      + "(location: $add, geo: $geo, identify: $per) isa locate;"
      + "(owner: $per, attached_card: $car, attached_bank: $ban) isa bank_account;";


  private final List<String> lArg = Stream.of("first", "last", "gen", "job", "birth", "street",
      "city", "state", "zip", "nbcar", "bank", "lat", "lon").collect(Collectors.toList());

  public CardholderDAO(TypeDB_SessionWrapper wrapper) {
    this.wrapper = wrapper;
  }

  private String getQueryStr(Cardholder currentCardholder) {
    String result = queryInsert.formatted(
        currentCardholder.getCardholderCoordinates().getLongitude(),
        currentCardholder.getCardholderCoordinates().getLatitude(),
        currentCardholder.getAddress().getStreet(),
        currentCardholder.getAddress().getCity(),
        currentCardholder.getAddress().getState(),
        currentCardholder.getAddress().getZip(),
        currentCardholder.getPerson_first_name(),
        currentCardholder.getPerson_last_name(),
        currentCardholder.getGender(),
        currentCardholder.getJob(),
        currentCardholder.getDate_of_birth(),
        currentCardholder.getCreditCard().getCard_number()
    );
    return (result);
  }


  public void insert_all(Set<Cardholder> lCardholder) throws IOException {
    Set<String> queryStrs = lCardholder.stream().map(this::getQueryStr).collect(Collectors.toSet());
    wrapper.load_data(queryStrs);
  }

  public Set<Cardholder> retrieveAll() throws IOException {
    BankDAO bankDAO = new BankDAO(wrapper);
    Hashtable<String, Bank> hBank = bankDAO.retrieveInternal();
    Set<Cardholder> sCardholder = new HashSet<Cardholder>();
    Set<List<String>> sCardholderStr = wrapper.read_data("match " + queryGet, lArg);
    for (List<String> currentStrCardholder : sCardholderStr) {
      sCardholder.add(cardholderBuilder(currentStrCardholder, hBank));
    }
    return sCardholder;
  }

  public Hashtable<String, Cardholder> retrieveInternal() throws IOException {
    BankDAO bankDAO = new BankDAO(wrapper);
    Hashtable<String, Bank> hBank = bankDAO.retrieveInternal();
    Hashtable<String, Cardholder> hCardholder = new Hashtable<String, Cardholder>();
    Set<List<String>> sCardholderStr = wrapper.read_data("match " + queryGet, lArg);
    for (List<String> currentStrCardholder : sCardholderStr) {
      hCardholder.put(currentStrCardholder.get(0) + currentStrCardholder.get(1),
          cardholderBuilder(currentStrCardholder, hBank));
    }
    return hCardholder;
  }

  public Cardholder cardholderBuilder(List<String> pList, Hashtable<String, Bank> hBank) {
    return cardholderBuilder(pList, hBank, 0);
  }

  public Cardholder cardholderBuilder(List<String> pList, Hashtable<String, Bank> hBank,
      int pBegin) {

    Address tempAddress = new Address(pList.get(pBegin + 5), pList.get(pBegin + 6),
        pList.get(pBegin + 7), pList.get(pBegin + 8));
    CardholderCoordinates tempCoord = new CardholderCoordinates(pList.get(pBegin + 11),
        pList.get(pBegin + 12));
    CreditCard tempCard = new CreditCard(pList.get(pBegin + 9), hBank.get(pList.get(pBegin + 10)));
    Cardholder tmpCardholder = new Cardholder(pList.get(pBegin + 0), pList.get(pBegin + 1),
        pList.get(pBegin + 2),
        pList.get(pBegin + 3), pList.get(pBegin + 4), tempAddress, tempCoord, tempCard);

    return tmpCardholder;
  }

  public String getQueryGet() {
    return queryGet;
  }

  public List<String> getlArg() {
    return lArg;
  }
}
